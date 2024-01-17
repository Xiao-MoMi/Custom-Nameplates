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

package net.momirealms.customnameplates.paper.storage.method.file;

import net.momirealms.customnameplates.api.data.PlayerData;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.paper.CustomNameplatesPluginImpl;
import net.momirealms.customnameplates.paper.storage.StorageType;
import net.momirealms.customnameplates.paper.storage.method.AbstractStorage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A data storage implementation that uses YAML files to store player data, with support for legacy data.
 */
public class YAMLImpl extends AbstractStorage {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public YAMLImpl(CustomNameplatesPluginImpl plugin) {
        super(plugin);
        File folder = new File(plugin.getDataFolder(), "data");
        if (!folder.exists()) folder.mkdirs();
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.YAML;
    }

    @Override
    public CompletableFuture<Optional<PlayerData>> getPlayerData(UUID uuid) {
        File dataFile = getPlayerDataFile(uuid);
        if (!dataFile.exists()) {
            if (Bukkit.getPlayer(uuid) != null) {
                return CompletableFuture.completedFuture(Optional.of(PlayerData.empty()));
            } else {
                return CompletableFuture.completedFuture(Optional.empty());
            }
        }
        YamlConfiguration data = readData(dataFile);
        PlayerData playerData = new PlayerData.Builder()
                .setBubble(data.getString("bubble", ""))
                .setNameplate(data.getString("nameplate", ""))
                .build();
        return CompletableFuture.completedFuture(Optional.of(playerData));
    }

    /**
     * Get the file associated with a player's UUID for storing YAML data.
     *
     * @param uuid The UUID of the player.
     * @return The file for the player's data.
     */
    public File getPlayerDataFile(UUID uuid) {
        return new File(plugin.getDataFolder(), "data" + File.separator + uuid + ".yml");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public YamlConfiguration readData(File file) {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                LogUtils.warn("Failed to generate data files!</red>");
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public CompletableFuture<Boolean> updatePlayerData(UUID uuid, PlayerData playerData) {
        YamlConfiguration data = new YamlConfiguration();
        data.set("bubble", playerData.getBubble());
        data.set("nameplate", playerData.getNameplate());
        try {
            data.save(getPlayerDataFile(uuid));
        } catch (IOException e) {
            LogUtils.warn("Failed to save player data", e);
        }
        return CompletableFuture.completedFuture(true);
    }
}
