package net.momirealms.customnameplates.data;

import net.momirealms.customnameplates.AdventureManager;
import net.momirealms.customnameplates.ConfigManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataManager {

    public static Map<UUID, PlayerData> cache;
    public DataManager() {
        cache = new HashMap<>();
    }

    public PlayerData getOrCreate(UUID uuid) {
        if (cache.containsKey(uuid)) {
            return cache.get(uuid);
        }
        PlayerData playerData = SqlHandler.getPlayerData(uuid);
        if (playerData == null) {
            playerData = PlayerData.EMPTY;
        }
        cache.put(uuid, playerData);
        return playerData;
    }

    public void unloadPlayer(UUID uuid) {
        if (!cache.containsKey(uuid)) {
            return;
        }
        SqlHandler.save(cache.get(uuid), uuid);
        cache.remove(uuid);
    }

    public void savePlayer(UUID uuid) {
        SqlHandler.save(cache.get(uuid), uuid);
    }

    public static boolean create() {
        if(ConfigManager.DatabaseConfig.use_mysql){
            AdventureManager.consoleMessage("<gradient:#DDE4FF:#8DA2EE>[CustomNameplates]</gradient> <color:#00CED1>Storage Mode - MYSQL");
        }else {
            AdventureManager.consoleMessage("<gradient:#DDE4FF:#8DA2EE>[CustomNameplates]</gradient> <color:#00CED1>Storage Mode - SQLite");
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