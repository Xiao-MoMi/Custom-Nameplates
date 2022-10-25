package net.momirealms.customnameplates.objects.data;

import org.bukkit.OfflinePlayer;

public interface DataStorageInterface {

    void initialize();
    void disable();
    PlayerData loadData(OfflinePlayer player);
    void saveData(PlayerData playerData);

}
