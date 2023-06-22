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

package net.momirealms.customnameplates.manager;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.data.*;
import net.momirealms.customnameplates.listener.JoinQuitListener;
import net.momirealms.customnameplates.object.DisplayMode;
import net.momirealms.customnameplates.object.Function;
import net.momirealms.customnameplates.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DataManager extends Function {

    private final CustomNameplates plugin;
    private DataStorageInterface dataStorageInterface;
    private final JoinQuitListener joinQuitListener;
    private StorageType storageType;
    private final ConcurrentHashMap<UUID, PlayerData> playerDataMap;
    private final ConcurrentHashMap<UUID, Integer> triedTimes;

    public DataManager(CustomNameplates plugin) {
        this.plugin = plugin;
        this.playerDataMap = new ConcurrentHashMap<>();
        this.joinQuitListener = new JoinQuitListener(this);
        this.triedTimes = new ConcurrentHashMap<>();
    }

    @Override
    public void load() {
        if (loadStorageMode()) this.dataStorageInterface.initialize();
        Bukkit.getPluginManager().registerEvents(joinQuitListener, plugin);
    }

    @Override
    public void unload() {
        YamlConfiguration config = ConfigUtils.getConfig("database.yml");
        StorageType st = config.getString("data-storage-method","YAML").equalsIgnoreCase("YAML") ? StorageType.YAML : StorageType.SQL;
        if (this.dataStorageInterface != null && dataStorageInterface.getStorageType() != st) this.dataStorageInterface.disable();
        HandlerList.unregisterAll(joinQuitListener);
    }

    @Override
    public void disable() {
        if (this.dataStorageInterface != null) {
            this.dataStorageInterface.disable();
        }
    }

    @Override
    public void onJoin(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> readData(player.getUniqueId()));
    }

    public void readData(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.isOnline() || !checkTriedTimes(uuid)) return;
        PlayerData playerData = this.dataStorageInterface.loadData(uuid);
        if (playerData == null) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> readData(uuid), 20);
        }
        else {
            playerDataMap.put(uuid, playerData);
            if (!ConfigManager.enableNameplates || plugin.getNameplateManager().getMode() == DisplayMode.DISABLE) return;
            plugin.getTeamManager().getTeamNameInterface().onJoin(player);
            plugin.getTeamManager().createTeam(uuid);
        }
    }

    @Override
    public void onQuit(Player player) {
        PlayerData playerData = playerDataMap.remove(player.getUniqueId());
        if (playerData != null) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> this.dataStorageInterface.saveData(playerData));
        }
        plugin.getTeamManager().onQuit(player);
        triedTimes.remove(player.getUniqueId());
    }

    public void saveData(Player player) {
        PlayerData playerData = playerDataMap.get(player.getUniqueId());
        if (playerData != null) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> this.dataStorageInterface.saveData(playerData));
        }
    }

    public DataStorageInterface getDataStorageInterface() {
        return dataStorageInterface;
    }

    private boolean loadStorageMode() {
        YamlConfiguration config = ConfigUtils.getConfig("database.yml");
        if (config.getString("data-storage-method","YAML").equalsIgnoreCase("YAML")) {
            if (storageType != StorageType.YAML) {
                this.dataStorageInterface = new FileStorageImpl(plugin);
                this.storageType = StorageType.YAML;
                return true;
            }
        } else {
            if (storageType != StorageType.SQL) {
                this.dataStorageInterface = new MySQLStorageImpl(plugin);
                this.storageType = StorageType.SQL;
                return true;
            }
        }
        return false;
    }

    public String getEquippedNameplate(Player player) {
        return Optional.ofNullable(playerDataMap.get(player.getUniqueId())).orElse(PlayerData.EMPTY).getNameplate();
    }

    public String getEquippedBubble(Player player) {
        return Optional.ofNullable(playerDataMap.get(player.getUniqueId())).orElse(PlayerData.EMPTY).getBubble();
    }

    public void equipNameplate(Player player, String nameplate) {
        PlayerData playerData = playerDataMap.get(player.getUniqueId());
        if (playerData != null) {
            playerData.setNameplate(nameplate);
        }
    }

    public void equipBubble(Player player, String bubble) {
        PlayerData playerData = playerDataMap.get(player.getUniqueId());
        if (playerData != null) {
            playerData.setBubble(bubble);
        }
    }

    protected boolean checkTriedTimes(UUID uuid) {
        Integer previous = triedTimes.get(uuid);
        if (previous == null) {
            triedTimes.put(uuid, 1);
            return true;
        }
        else if (previous > 2) {
            triedTimes.remove(uuid);
            return false;
        }
        else {
            triedTimes.put(uuid, previous + 1);
            return true;
        }
    }
}
