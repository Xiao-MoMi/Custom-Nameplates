package net.momirealms.customnameplates.backend.storage.method;

import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.storage.StorageType;
import net.momirealms.customnameplates.api.storage.data.PlayerData;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class DummyStorage extends AbstractStorage {

    public DummyStorage(CustomNameplates plugin) {
        super(plugin);
    }

    @Override
    public StorageType storageType() {
        return StorageType.NONE;
    }

    @Override
    public CompletableFuture<Optional<PlayerData>> getPlayerData(UUID uuid, Executor executor) {
        return CompletableFuture.completedFuture(Optional.of(PlayerData.empty(uuid)));
    }

    @Override
    public CompletableFuture<Boolean> updatePlayerData(PlayerData playerData, Executor executor) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public Set<UUID> getUniqueUsers() {
        return Set.of();
    }
}
