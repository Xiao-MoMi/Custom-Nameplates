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

package net.momirealms.customnameplates.paper.storage.method.database.nosql;

import net.momirealms.customnameplates.api.data.PlayerData;
import net.momirealms.customnameplates.api.data.StorageType;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.paper.CustomNameplatesPluginImpl;
import net.momirealms.customnameplates.paper.storage.method.AbstractStorage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A RedisManager class responsible for managing interactions with a Redis server for data storage.
 */
public class RedisManager extends AbstractStorage {

    private static RedisManager instance;
    private JedisPool jedisPool;
    private String password;
    private int port;
    private String host;
    private boolean useSSL;

    public RedisManager(CustomNameplatesPluginImpl plugin) {
        super(plugin);
        instance = this;
    }

    /**
     * Get the singleton instance of the RedisManager.
     *
     * @return The RedisManager instance.
     */
    public static RedisManager getInstance() {
        return instance;
    }

    /**
     * Get a Jedis resource for interacting with the Redis server.
     *
     * @return A Jedis resource.
     */
    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    @Override
    public Set<UUID> getUniqueUsers(boolean legacy) {
        return new HashSet<>();
    }

    /**
     * Initialize the Redis connection and configuration based on the plugin's YAML configuration.
     */
    @Override
    public void initialize() {
        YamlConfiguration config = plugin.getConfig("database.yml");
        ConfigurationSection section = config.getConfigurationSection("Redis");
        if (section == null) {
            LogUtils.warn("Failed to load database config. It seems that your config is broken. Please regenerate a new one.");
            return;
        }

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setTestWhileIdle(true);
        jedisPoolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(30000));
        jedisPoolConfig.setNumTestsPerEvictionRun(-1);
        jedisPoolConfig.setMinEvictableIdleDuration(Duration.ofMillis(section.getInt("MinEvictableIdleTimeMillis", 1800000)));
        jedisPoolConfig.setMaxTotal(section.getInt("MaxTotal",8));
        jedisPoolConfig.setMaxIdle(section.getInt("MaxIdle",8));
        jedisPoolConfig.setMinIdle(section.getInt("MinIdle",1));
        jedisPoolConfig.setMaxWait(Duration.ofMillis(section.getInt("MaxWaitMillis")));

        password = section.getString("password", "");
        port = section.getInt("port", 6379);
        host = section.getString("host", "localhost");
        useSSL = section.getBoolean("use-ssl", false);

        if (password.isBlank()) {
            jedisPool = new JedisPool(jedisPoolConfig, host, port, 0, useSSL);
        } else {
            jedisPool = new JedisPool(jedisPoolConfig, host, port, 0, password, useSSL);
        }
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.ping();
            LogUtils.info("Redis server connected.");
        } catch (JedisException e) {
            LogUtils.warn("Failed to connect redis.");
        }
    }

    /**
     * Disable the Redis connection by closing the JedisPool.
     */
    @Override
    public void disable() {
        if (jedisPool != null && !jedisPool.isClosed())
            jedisPool.close();
    }

    /**
     * Send a message to Redis on a specified channel.
     *
     * @param channel The Redis channel to send the message to.
     * @param message The message to send.
     */
    public void sendRedisMessage(@NotNull String channel, @NotNull String message) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(channel, message);
            plugin.debug("Sent Redis message: " + message);
        }
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.Redis;
    }

    @Override
    public CompletableFuture<Optional<PlayerData>> getPlayerData(UUID uuid) {
        var future = new CompletableFuture<Optional<PlayerData>>();
        plugin.getScheduler().runTaskAsync(() -> {
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] key = getRedisKey("cn_data", uuid);
            byte[] data = jedis.get(key);
            jedis.del(key);
            if (data != null) {
                future.complete(Optional.of(plugin.getStorageManager().fromBytes(data)));
                plugin.debug("Redis data retrieved for " + uuid + "; normal data");
            } else {
                future.complete(Optional.empty());
                plugin.debug("Redis data retrieved for " + uuid + "; empty data");
            }
        } catch (Exception e) {
            future.complete(Optional.empty());
            LogUtils.warn("Failed to get redis data for " + uuid, e);
        }
        });
        return future;
    }

    @Override
    public CompletableFuture<Boolean> updatePlayerData(UUID uuid, PlayerData playerData) {
        var future = new CompletableFuture<Boolean>();
        plugin.getScheduler().runTaskAsync(() -> {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(
                    getRedisKey("cn_data", uuid),
                    600,
                    plugin.getStorageManager().toBytes(playerData)
            );
            future.complete(true);
            plugin.debug("Redis data set for " + uuid);
        } catch (Exception e) {
            future.complete(false);
            LogUtils.warn("Failed to set redis data for player " + uuid, e);
        }
        });
        return future;
    }

    /**
     * Generate a Redis key for a specified key and UUID.
     *
     * @param key  The key identifier.
     * @param uuid The UUID to include in the key.
     * @return A byte array representing the Redis key.
     */
    private byte[] getRedisKey(String key, @NotNull UUID uuid) {
        return (key + ":" + uuid).getBytes(StandardCharsets.UTF_8);
    }
}
