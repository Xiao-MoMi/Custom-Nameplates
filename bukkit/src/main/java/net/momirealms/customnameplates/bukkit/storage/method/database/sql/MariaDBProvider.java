package net.momirealms.customnameplates.bukkit.storage.method.database.sql;

import net.momirealms.customnameplates.api.storage.StorageType;
import net.momirealms.customnameplates.bukkit.BukkitCustomNameplates;

public class MariaDBProvider extends AbstractHikariDatabase {

    public MariaDBProvider(BukkitCustomNameplates plugin) {
        super(plugin);
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.MariaDB;
    }
}
