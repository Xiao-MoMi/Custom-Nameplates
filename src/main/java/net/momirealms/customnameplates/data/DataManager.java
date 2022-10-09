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

import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.utils.AdventureUtil;
import net.momirealms.customnameplates.CustomNameplates;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class DataManager {

    private final HashMap<UUID, PlayerData> cache = new HashMap<>();

    public PlayerData getOrEmpty(Player player) {
        if (cache.get(player.getUniqueId()) == null) {
            return new PlayerData("none","none");
        }
        else {
            return cache.get(player.getUniqueId());
        }
    }

    public void loadData(Player player) {
        UUID uuid = player.getUniqueId();
        if (ConfigManager.Database.async) {
            Bukkit.getScheduler().runTaskAsynchronously(CustomNameplates.instance, () -> {
                PlayerData playerData = SqlHandler.getPlayerData(uuid);
                cache.put(uuid, Optional.ofNullable(playerData).orElse(new PlayerData(ConfigManager.Nameplate.default_nameplate, ConfigManager.Bubbles.defaultBubble)));
                if (ConfigManager.Module.nameplate) CustomNameplates.instance.getTeamManager().createTeam(player);
            });
        }
        else {
            PlayerData playerData = SqlHandler.getPlayerData(uuid);
            cache.put(uuid, Optional.ofNullable(playerData).orElse(new PlayerData(ConfigManager.Nameplate.default_nameplate, ConfigManager.Bubbles.defaultBubble)));
            if (ConfigManager.Module.nameplate) CustomNameplates.instance.getTeamManager().createTeam(player);
        }
    }

    public PlayerData getOrCreate(UUID uuid) {
        if (cache.containsKey(uuid)) {
            return cache.get(uuid);
        }
        PlayerData playerData = SqlHandler.getPlayerData(uuid);
        if (playerData == null) {
            playerData = new PlayerData(ConfigManager.Nameplate.default_nameplate, ConfigManager.Bubbles.defaultBubble);
        }
        cache.put(uuid, playerData);
        return playerData;
    }

    public void unloadPlayer(UUID uuid) {
        if (!cache.containsKey(uuid)) {
            return;
        }
        if (ConfigManager.Database.async){
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
        if (ConfigManager.Database.async){
            Bukkit.getScheduler().runTaskAsynchronously(CustomNameplates.instance, () -> {
                SqlHandler.save(cache.get(uuid), uuid);
            });
        }else {
            SqlHandler.save(cache.get(uuid), uuid);
        }
    }

    public boolean create() {
        if (ConfigManager.Database.use_mysql) AdventureUtil.consoleMessage("[CustomNameplates] Storage Mode - <green>MYSQL");
        else AdventureUtil.consoleMessage("[CustomNameplates] Storage Mode - <green>SQLite");
        if (SqlHandler.connect()) {
            if (ConfigManager.Database.use_mysql) {
                SqlHandler.getWaitTimeOut();
            }
            SqlHandler.createTable();
            return true;
        }
        else return false;
    }

    public HashMap<UUID, PlayerData> getCache() {
        return cache;
    }
}