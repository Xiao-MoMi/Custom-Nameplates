package net.momirealms.customnameplates.backend.storage.method.database.sql;

import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.storage.StorageType;

public class MySQLProvider extends AbstractHikariDatabase {

    public MySQLProvider(CustomNameplates plugin) {
        super(plugin);
    }

    @Override
    public StorageType storageType() {
        return StorageType.MYSQL;
    }
}
