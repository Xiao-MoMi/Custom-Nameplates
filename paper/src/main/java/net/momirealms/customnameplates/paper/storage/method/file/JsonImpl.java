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

import com.google.gson.Gson;
import net.momirealms.customnameplates.api.data.PlayerData;
import net.momirealms.customnameplates.api.data.StorageType;
import net.momirealms.customnameplates.paper.CustomNameplatesPluginImpl;
import net.momirealms.customnameplates.paper.storage.method.AbstractStorage;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A data storage implementation that uses JSON files to store player data.
 */
public class JsonImpl extends AbstractStorage {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public JsonImpl(CustomNameplatesPluginImpl plugin) {
        super(plugin);
        File folder = new File(plugin.getDataFolder(), "data");
        if (!folder.exists()) folder.mkdirs();
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.JSON;
    }

    @Override
    public CompletableFuture<Optional<PlayerData>> getPlayerData(UUID uuid) {
        File file = getPlayerDataFile(uuid);
        PlayerData playerData;
        if (file.exists()) {
            playerData = readFromJsonFile(file, PlayerData.class);
        } else if (Bukkit.getPlayer(uuid) != null) {
            playerData = PlayerData.empty();
        } else {
            playerData = null;
        }
        return CompletableFuture.completedFuture(Optional.ofNullable(playerData));
    }

    /**
     * Get the file associated with a player's UUID for storing JSON data.
     *
     * @param uuid The UUID of the player.
     * @return The file for the player's data.
     */
    public File getPlayerDataFile(UUID uuid) {
        return new File(plugin.getDataFolder(), "data" + File.separator + uuid + ".json");
    }

    /**
     * Save an object to a JSON file.
     *
     * @param obj      The object to be saved as JSON.
     * @param filepath The file path where the JSON file should be saved.
     */
    public void saveToJsonFile(Object obj, File filepath) {
        Gson gson = new Gson();
        try (FileWriter file = new FileWriter(filepath)) {
            gson.toJson(obj, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read JSON content from a file and parse it into an object of the specified class.
     *
     * @param file      The JSON file to read.
     * @param classOfT  The class of the object to parse the JSON into.
     * @param <T>       The type of the object.
     * @return The parsed object.
     */
    public <T> T readFromJsonFile(File file, Class<T> classOfT) {
        Gson gson = new Gson();
        String jsonContent = new String(readFileToByteArray(file), StandardCharsets.UTF_8);
        return gson.fromJson(jsonContent, classOfT);
    }

    /**
     * Read the contents of a file and return them as a byte array.
     *
     * @param file The file to read.
     * @return The byte array representing the file's content.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public byte[] readFileToByteArray(File file) {
        byte[] fileBytes = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(fileBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileBytes;
    }

    @Override
    public CompletableFuture<Boolean> updatePlayerData(UUID uuid, PlayerData playerData) {
        this.saveToJsonFile(playerData, getPlayerDataFile(uuid));
        return CompletableFuture.completedFuture(true);
    }

    // Retrieve a set of unique user UUIDs based on JSON data files in the 'data' folder.
    @Override
    public Set<UUID> getUniqueUsers(boolean legacy) {
        // No legacy files
        File folder = new File(plugin.getDataFolder(), "data");
        Set<UUID> uuids = new HashSet<>();
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    uuids.add(UUID.fromString(file.getName().substring(file.getName().length() - 5)));
                }
            }
        }
        return uuids;
    }
}
