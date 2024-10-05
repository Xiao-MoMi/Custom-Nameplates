package net.momirealms.customnameplates.backend.storage.method.file;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.storage.StorageType;
import net.momirealms.customnameplates.api.storage.data.PlayerData;
import net.momirealms.customnameplates.backend.storage.method.AbstractStorage;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class YAMLProvider extends AbstractStorage {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public YAMLProvider(CustomNameplates plugin) {
        super(plugin);
        File folder = new File(plugin.getDataFolder(), "data");
        if (!folder.exists()) folder.mkdirs();
    }

    @Override
    public StorageType storageType() {
        return StorageType.YAML;
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

    @Override
    public CompletableFuture<Optional<PlayerData>> getPlayerData(UUID uuid, Executor executor) {
        CompletableFuture<Optional<PlayerData>> future = new CompletableFuture<>();
        if (executor == null) executor = plugin.getScheduler().async();
        executor.execute(() -> {
            File dataFile = getPlayerDataFile(uuid);
            if (!dataFile.exists()) {
                if (plugin.getPlayer(uuid) != null) {
                    future.complete(Optional.of(PlayerData.empty(uuid)));
                } else {
                    future.complete(Optional.empty());
                }
                return;
            }
            YamlDocument data = plugin.getConfigManager().loadData(dataFile);
            PlayerData playerData = PlayerData.builder()
                    .uuid(uuid)
                    .nameplate(data.getString("nameplate", "none"))
                    .bubble(data.getString("bubble", "none"))
                    .build();
            future.complete(Optional.of(playerData));
        });
        return future;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public CompletableFuture<Boolean> updatePlayerData(PlayerData playerData, Executor executor) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (executor == null) executor = plugin.getScheduler().async();
        executor.execute(() -> {
            try {
                File dataFile = getPlayerDataFile(playerData.uuid());
                if (!dataFile.exists()) {
                    dataFile.getParentFile().mkdirs();
                    dataFile.createNewFile();
                }
                YamlDocument data = plugin.getConfigManager().loadData(dataFile);
                data.set("bubble", playerData.bubble());
                data.set("nameplate", playerData.nameplate());
                data.save(dataFile);
                future.complete(true);
            } catch (IOException e) {
                future.complete(false);
            }
        });
        return future;
    }

    @Override
    public Set<UUID> getUniqueUsers() {
        File folder = new File(plugin.getDataFolder(), "data");
        Set<UUID> uuids = new HashSet<>();
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    uuids.add(UUID.fromString(file.getName().substring(0, file.getName().length() - 4)));
                }
            }
        }
        return uuids;
    }
}
