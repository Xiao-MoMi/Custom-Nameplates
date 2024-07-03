package net.momirealms.customnameplates.paper.storage.method;

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.data.PlayerData;
import net.momirealms.customnameplates.api.data.StorageType;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class NoneStorage extends AbstractStorage {

    public NoneStorage(CustomNameplatesPlugin plugin) {
        super(plugin);
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.None;
    }

    @Override
    public CompletableFuture<Optional<PlayerData>> getPlayerData(UUID uuid) {
        return CompletableFuture.completedFuture(Optional.of(PlayerData.empty()));
    }

    @Override
    public CompletableFuture<Boolean> updatePlayerData(UUID uuid, PlayerData playerData) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public Set<UUID> getUniqueUsers(boolean legacy) {
        return Set.of();
    }
}
