package net.momirealms.customnameplates.backend.storage.method.file;

import com.google.gson.Gson;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.storage.StorageType;
import net.momirealms.customnameplates.api.storage.data.JsonData;
import net.momirealms.customnameplates.api.storage.data.PlayerData;
import net.momirealms.customnameplates.backend.storage.method.AbstractStorage;

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
import java.util.concurrent.Executor;

/**
 * A data storage implementation that uses JSON files to store player data.
 */
public class JsonProvider extends AbstractStorage {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public JsonProvider(CustomNameplates plugin) {
        super(plugin);
        File folder = new File(plugin.getDataFolder(), "data");
        if (!folder.exists()) folder.mkdirs();
    }

    @Override
    public StorageType storageType() {
        return StorageType.JSON;
    }

    @Override
    public CompletableFuture<Optional<PlayerData>> getPlayerData(UUID uuid, Executor executor) {
        CompletableFuture<Optional<PlayerData>> future = new CompletableFuture<>();
        if (executor == null) executor = plugin.getScheduler().async();
        executor.execute(() -> {
            File file = getPlayerDataFile(uuid);
            PlayerData playerData;
            if (file.exists()) {
                playerData = readFromJsonFile(file, JsonData.class).toPlayerData(uuid);
            } else if (plugin.getPlayer(uuid) != null) {
                playerData = PlayerData.empty(uuid);
            } else {
                playerData = null;
            }
            future.complete(Optional.ofNullable(playerData));
        });
        return future;
    }

    @Override
    public CompletableFuture<Boolean> updatePlayerData(PlayerData playerData, Executor executor) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (executor == null) executor = plugin.getScheduler().async();
        executor.execute(() -> {
            try {
                this.saveToJsonFile(playerData.toJsonData(), getPlayerDataFile(playerData.uuid()));
            } catch (Exception e) {
                future.complete(false);
            }
            future.complete(true);
        });
        return future;
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
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
        return fileBytes;
    }

    // Retrieve a set of unique user UUIDs based on JSON data files in the 'data' folder.
    @Override
    public Set<UUID> getUniqueUsers() {
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
