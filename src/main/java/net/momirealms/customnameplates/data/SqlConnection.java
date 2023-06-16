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

package net.momirealms.customnameplates.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import net.momirealms.customnameplates.utils.AdventureUtils;
import net.momirealms.customnameplates.utils.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.sql.Connection;
import java.sql.SQLException;

public class SqlConnection {

    private boolean secondTry = false;
    private boolean firstTry = false;
    private HikariDataSource hikariDataSource;
    private String table;
    private final MySQLStorageImpl mySQLStorage;

    public SqlConnection(MySQLStorageImpl mySQLStorage) {
        this.mySQLStorage = mySQLStorage;
    }

    public void createNewHikariConfiguration() {
        ConfigUtils.update("database.yml");
        YamlConfiguration config = ConfigUtils.getConfig("database.yml");
        String storageMode = config.getString("data-storage-method", "MySQL");

        HikariConfig hikariConfig = new HikariConfig();
        String sql = "mysql";
        if (storageMode.equalsIgnoreCase("MariaDB")) {
            try {
                Class.forName("org.mariadb.jdbc.Driver");
            } catch (ClassNotFoundException e1) {
                AdventureUtils.consoleMessage("<red>[CustomNameplates] No sql driver is found.");
            }
            hikariConfig.setDriverClassName("org.mariadb.jdbc.Driver");
            sql = "mariadb";
        } else {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e1) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException e2) {
                    AdventureUtils.consoleMessage("<red>[CustomNameplates] No sql driver is found.");
                }
            }
        }
        table = config.getString(storageMode + ".table");
        hikariConfig.setPoolName("[CustomNameplates]");
        hikariConfig.setJdbcUrl(String.format("jdbc:%s://%s/%s", sql, config.getString(storageMode + ".host") + ":" + config.getString(storageMode + ".port"), config.getString(storageMode + ".database")));
        hikariConfig.setUsername(config.getString(storageMode + ".user"));
        hikariConfig.setPassword(config.getString(storageMode + ".password"));
        hikariConfig.setMaximumPoolSize(config.getInt(storageMode + ".Pool-Settings.maximum-pool-size"));
        hikariConfig.setMinimumIdle(config.getInt(storageMode + ".Pool-Settings.minimum-idle"));
        hikariConfig.setMaxLifetime(config.getInt(storageMode + ".Pool-Settings.maximum-lifetime"));
        hikariConfig.setConnectionTimeout(3000);
        hikariConfig.setIdleTimeout(hikariConfig.getMinimumIdle() < hikariConfig.getMaximumPoolSize() ? config.getInt(storageMode + ".Pool-Settings.idle-timeout") : 0);
        ConfigurationSection section = config.getConfigurationSection(storageMode + ".properties");
        if (section != null) {
            for (String property : section.getKeys(false)) {
                hikariConfig.addDataSourceProperty(property, config.getString(storageMode + ".properties." + property));
            }
        }
        try {
            hikariDataSource = new HikariDataSource(hikariConfig);
        } catch (HikariPool.PoolInitializationException e) {
            AdventureUtils.consoleMessage("[CustomNameplates] Failed to create sql connection");
        }
    }

    public boolean setGlobalConnection() {
        try {
            createNewHikariConfiguration();
            Connection connection = getConnection();
            connection.close();
            if (secondTry) {
                AdventureUtils.consoleMessage("[CustomNameplates] Successfully reconnect to SQL!");
            } else {
                secondTry = true;
            }
            return true;
        } catch (SQLException e) {
            AdventureUtils.consoleMessage("<red>[CustomNameplates] Error! Failed to connect to SQL!</red>");
            e.printStackTrace();
            close();
            return false;
        }
    }

    public Connection getConnection() throws SQLException {
        return hikariDataSource.getConnection();
    }

    public boolean canConnect() {
        if (hikariDataSource == null) {
            return setGlobalConnection();
        }
        if (hikariDataSource.isClosed()) {
            return setGlobalConnection();
        }
        return true;
    }

    public void close() {
        if (hikariDataSource != null && hikariDataSource.isRunning()) {
            hikariDataSource.close();
        }
    }

    public Connection getConnectionAndCheck() {
        if (!canConnect()) {
            return null;
        }
        try {
            return getConnection();
        } catch (SQLException e) {
            if (firstTry) {
                firstTry = false;
                close();
                return getConnectionAndCheck();
            } else {
                firstTry = true;
                AdventureUtils.consoleMessage("<red>[CustomNameplates] Error! Failed to connect to SQL!</red>");
                close();
                e.printStackTrace();
                return null;
            }
        }
    }

    public String getTable() {
        return table;
    }
}