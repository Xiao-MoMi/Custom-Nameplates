package net.momirealms.customnameplates.bukkit.storage.method.database.sql;


import net.momirealms.customnameplates.api.storage.StorageType;
import net.momirealms.customnameplates.bukkit.BukkitCustomNameplates;

public class MySQLProvider extends AbstractHikariDatabase {

    public MySQLProvider(BukkitCustomNameplates plugin) {
        super(plugin);
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.MySQL;
    }
}
