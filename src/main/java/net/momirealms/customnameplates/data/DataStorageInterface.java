package net.momirealms.customnameplates.data;

import java.util.UUID;

public interface DataStorageInterface {
    void initialize();
    void disable();
    PlayerData loadData(UUID uuid);
    void saveData(PlayerData playerData);
    StorageType getStorageType();
}
