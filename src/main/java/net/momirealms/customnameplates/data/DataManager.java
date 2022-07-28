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

package net.momirealms.customnameplates.data;

import net.momirealms.customnameplates.AdventureManager;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.hook.TABHook;
import net.momirealms.customnameplates.scoreboard.ScoreBoardManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataManager {

    public static Map<UUID, PlayerData> cache;
    private final CustomNameplates plugin;

    public DataManager(CustomNameplates plugin) {
        this.plugin = plugin;
        cache = new HashMap<>();
    }

    public void loadData(Player player) {
        UUID uuid = player.getUniqueId();
        if (ConfigManager.DatabaseConfig.async){
            Bukkit.getScheduler().runTaskAsynchronously(CustomNameplates.instance, () -> {
                PlayerData playerData = SqlHandler.getPlayerData(uuid);
                if (playerData == null) {
                    playerData = new PlayerData(ConfigManager.MainConfig.default_nameplate, 0);
                }
                cache.put(uuid, playerData);
                plugin.getScoreBoardManager().getOrCreateTeam(player);
            });
        }else {
            PlayerData playerData = SqlHandler.getPlayerData(uuid);
            if (playerData == null) {
                playerData = new PlayerData(ConfigManager.MainConfig.default_nameplate, 0);
            }
            cache.put(uuid, playerData);
            plugin.getScoreBoardManager().getOrCreateTeam(player);
        }
    }

    public PlayerData getOrCreate(UUID uuid) {
        if (cache.containsKey(uuid)) {
            return cache.get(uuid);
        }
        PlayerData playerData = SqlHandler.getPlayerData(uuid);
        if (playerData == null) {
            playerData = new PlayerData(ConfigManager.MainConfig.default_nameplate, 0);
        }
        cache.put(uuid, playerData);
        return playerData;
    }

    public void unloadPlayer(UUID uuid) {
        if (!cache.containsKey(uuid)) {
            return;
        }
        if (ConfigManager.DatabaseConfig.async){
            Bukkit.getScheduler().runTaskAsynchronously(CustomNameplates.instance, ()-> {
                SqlHandler.save(cache.get(uuid), uuid);
                cache.remove(uuid);
            });
        }
        else {
            SqlHandler.save(cache.get(uuid), uuid);
            cache.remove(uuid);
        }
    }

    public void savePlayer(UUID uuid) {
        if (ConfigManager.DatabaseConfig.async){
            Bukkit.getScheduler().runTaskAsynchronously(CustomNameplates.instance, () -> {
                SqlHandler.save(cache.get(uuid), uuid);
            });
        }else {
            SqlHandler.save(cache.get(uuid), uuid);
        }
    }

    public static boolean create() {
        if(ConfigManager.DatabaseConfig.use_mysql){
            AdventureManager.consoleMessage("<gradient:#2E8B57:#48D1CC>[CustomNameplates]</gradient> <color:#22e281>Storage Mode - MYSQL");
        }else {
            AdventureManager.consoleMessage("<gradient:#2E8B57:#48D1CC>[CustomNameplates]</gradient> <color:#22e281>Storage Mode - SQLite");
        }
        if (SqlHandler.connect()) {
            if (ConfigManager.DatabaseConfig.use_mysql) {
                SqlHandler.getWaitTimeOut();
            }
            SqlHandler.createTable();
        } else {
            AdventureManager.consoleMessage("<red>//DATA storage ERROR//</red>");
            return false;
        }
        return true;
    }
}