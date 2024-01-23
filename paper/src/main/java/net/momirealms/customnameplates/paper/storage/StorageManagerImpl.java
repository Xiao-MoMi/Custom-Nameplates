/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customnameplates.paper.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.momirealms.customnameplates.api.data.DataStorageInterface;
import net.momirealms.customnameplates.api.data.OnlineUser;
import net.momirealms.customnameplates.api.data.PlayerData;
import net.momirealms.customnameplates.api.data.StorageType;
import net.momirealms.customnameplates.api.event.NameplateDataLoadEvent;
import net.momirealms.customnameplates.api.manager.StorageManager;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.paper.CustomNameplatesPluginImpl;
import net.momirealms.customnameplates.paper.storage.method.database.nosql.MongoDBImpl;
import net.momirealms.customnameplates.paper.storage.method.database.nosql.RedisManager;
import net.momirealms.customnameplates.paper.storage.method.database.sql.H2Impl;
import net.momirealms.customnameplates.paper.storage.method.database.sql.MariaDBImpl;
import net.momirealms.customnameplates.paper.storage.method.database.sql.MySQLImpl;
import net.momirealms.customnameplates.paper.storage.method.database.sql.SQLiteImpl;
import net.momirealms.customnameplates.paper.storage.method.file.JsonImpl;
import net.momirealms.customnameplates.paper.storage.method.file.YAMLImpl;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class implements the StorageManager interface and is responsible for managing player data storage.
 * It includes methods to handle player data retrieval, storage, and serialization.
 */
public class StorageManagerImpl implements Listener, StorageManager {

    private final CustomNameplatesPluginImpl plugin;
    private DataStorageInterface dataSource;
    private StorageType previousType;
    private boolean hasRedis;
    private RedisManager redisManager;
    private final Gson gson;
    private final ConcurrentHashMap<UUID, OnlineUser> onlineUserMap;

    public StorageManagerImpl(CustomNameplatesPluginImpl plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().create();
        this.onlineUserMap = new ConcurrentHashMap<>(64);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var uuid = event.getPlayer().getUniqueId();
        this.getPlayerData(uuid).thenAccept(optData -> {
           if (optData.isPresent()) {
               var playerData = optData.get();
               var player = Bukkit.getPlayer(uuid);
               if (player == null || !player.isOnline()) return;
               var onlineUser = new OnlineUser(player, playerData);
               this.putOnlineUserInMap(onlineUser);
               NameplateDataLoadEvent syncEvent = new NameplateDataLoadEvent(uuid, onlineUser);
               plugin.getServer().getPluginManager().callEvent(syncEvent);
           }
        });
    }

    @Override
    public Collection<OnlineUser> getOnlineUsers() {
        return onlineUserMap.values();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.removeOnlineUserFromMap(event.getPlayer().getUniqueId());
    }

    public void putOnlineUserInMap(OnlineUser onlineUser) {
        onlineUserMap.put(onlineUser.getUUID(), onlineUser);
    }

    public void removeOnlineUserFromMap(UUID uuid) {
        onlineUserMap.remove(uuid);
    }

    @Override
    public CompletableFuture<Optional<PlayerData>> getPlayerData(UUID uuid) {
        OnlineUser onlineUser = onlineUserMap.get(uuid);
        if (onlineUser != null) {
            return CompletableFuture.completedFuture(Optional.of(onlineUser.toPlayerData()));
        }
        if (hasRedis) {
            return redisManager.getPlayerData(uuid).thenCompose(
                    result -> {
                        if (result.isEmpty()) {
                            return dataSource.getPlayerData(uuid);
                        } else {
                            return CompletableFuture.completedFuture(result);
                        }
                    }
            );
        } else {
            return dataSource.getPlayerData(uuid);
        }
    }

    @Override
    public CompletableFuture<Boolean> saveOnlinePlayerData(UUID uuid) {
        OnlineUser onlineUser = onlineUserMap.get(uuid);
        if (onlineUser == null) {
            return CompletableFuture.completedFuture(false);
        }
        return savePlayerData(uuid, onlineUser.toPlayerData());
    }

    @Override
    public CompletableFuture<Boolean> savePlayerData(UUID uuid, PlayerData playerData) {
        if (hasRedis) {
            return redisManager.updatePlayerData(uuid, playerData).thenCompose(
                        result -> {
                            if (!result) {
                                return CompletableFuture.completedFuture(false);
                            }
                            return dataSource.updatePlayerData(uuid, playerData);
                        }
                    );
        } else {
            return dataSource.updatePlayerData(uuid, playerData);
        }
    }

    @Override
    public Optional<OnlineUser> getOnlineUser(UUID uuid) {
        return Optional.ofNullable(onlineUserMap.get(uuid));
    }

    /**
     * Reloads the storage manager configuration.
     */
    public void reload() {
        YamlConfiguration config = plugin.getConfig("database.yml");

        // Check if storage type has changed and reinitialize if necessary
        StorageType storageType = StorageType.valueOf(config.getString("data-storage-method", "H2"));
        if (storageType != previousType) {
            if (this.dataSource != null) this.dataSource.disable();
            this.previousType = storageType;
            switch (storageType) {
                case H2 -> this.dataSource = new H2Impl(plugin);
                case JSON -> this.dataSource = new JsonImpl(plugin);
                case YAML -> this.dataSource = new YAMLImpl(plugin);
                case SQLite -> this.dataSource = new SQLiteImpl(plugin);
                case MySQL -> this.dataSource = new MySQLImpl(plugin);
                case MariaDB -> this.dataSource = new MariaDBImpl(plugin);
                case MongoDB -> this.dataSource = new MongoDBImpl(plugin);
            }
            if (this.dataSource != null) this.dataSource.initialize();
            else LogUtils.severe("No storage type is set.");
        }

        // Handle Redis configuration
        if (!this.hasRedis && config.getBoolean("Redis.enable", false)) {
            this.hasRedis = true;
            this.redisManager = new RedisManager(plugin);
            this.redisManager.initialize();
        }

        // Disable Redis if it was enabled but is now disabled
        if (this.hasRedis && !config.getBoolean("Redis.enable", false) && this.redisManager != null) {
            this.redisManager.disable();
            this.redisManager = null;
        }
    }

    /**
     * Disables the storage manager and cleans up resources.
     */
    public void disable() {
        HandlerList.unregisterAll(this);
        if (this.dataSource != null)
            this.dataSource.disable();
        if (this.redisManager != null)
            this.redisManager.disable();
    }

    /**
     * Checks if Redis is enabled.
     *
     * @return True if Redis is enabled; otherwise, false.
     */
    public boolean isRedisEnabled() {
        return hasRedis;
    }

    /**
     * Gets the RedisManager instance.
     *
     * @return The RedisManager instance.
     */
    @Nullable
    public RedisManager getRedisManager() {
        return redisManager;
    }

    @NotNull
    @Override
    public PlayerData fromJson(String json) {
        return gson.fromJson(json, PlayerData.class);
    }

    @Override
    public PlayerData fromBytes(byte[] data) {
        try {
            return gson.fromJson(new String(data, StandardCharsets.UTF_8), PlayerData.class);
        } catch (JsonSyntaxException e) {
            throw new DataSerializationException("Failed to get PlayerData from bytes", e);
        }
    }

    @Override
    public byte[] toBytes(PlayerData playerData) {
        return toJson(playerData).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    @NotNull
    public String toJson(@NotNull PlayerData playerData) {
        return gson.toJson(playerData);
    }

    @Override
    public DataStorageInterface getDataSource() {
        return dataSource;
    }

    /**
     * Custom exception class for data serialization errors.
     */
    public static class DataSerializationException extends RuntimeException {
        protected DataSerializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
