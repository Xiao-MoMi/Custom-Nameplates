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

package net.momirealms.customnameplates.paper.storage.method.database.sql;

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.data.StorageType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.h2.jdbcx.JdbcConnectionPool;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * An implementation of AbstractSQLDatabase that uses the H2 embedded database for player data storage.
 */
public class H2Impl extends AbstractSQLDatabase {

    private JdbcConnectionPool connectionPool;

    public H2Impl(CustomNameplatesPlugin plugin) {
        super(plugin);
    }

    /**
     * Initialize the H2 database and connection pool based on the configuration.
     */
    @Override
    public void initialize() {
        YamlConfiguration config = plugin.getConfig("database.yml");
        File databaseFile = new File(plugin.getDataFolder(), config.getString("H2.file", "data.db"));
        super.tablePrefix = config.getString("H2.table-prefix", "customnameplates");

        final String url = String.format("jdbc:h2:%s", databaseFile.getAbsolutePath());
        this.connectionPool = JdbcConnectionPool.create(url, "sa", "");
        super.createTableIfNotExist();
    }

    /**
     * Disable the H2 database by disposing of the connection pool.
     */
    @Override
    public void disable() {
        if (connectionPool != null) {
            connectionPool.dispose();
        }
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.H2;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }
}
