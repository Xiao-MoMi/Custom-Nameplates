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

package net.momirealms.customnameplates.objects.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import net.momirealms.customnameplates.utils.AdventureUtil;
import net.momirealms.customnameplates.utils.ConfigUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.sql.Connection;
import java.sql.SQLException;

public class SqlConnection {

    private boolean secondTry = false;
    private boolean firstTry = false;
    private HikariDataSource hikariDataSource;
    private String table_name;

    public void createNewHikariConfiguration() {

        YamlConfiguration config = ConfigUtil.getConfig("database.yml");
        String storageMode = config.getString("data-storage-method", "MySQL");

        HikariConfig hikariConfig = new HikariConfig();
        String sql = "mysql";
        if (storageMode.equalsIgnoreCase("MariaDB")) {
            hikariConfig.setDriverClassName("org.mariadb.jdbc.Driver");
            sql = "mariadb";
        }
        table_name = config.getString(storageMode + ".table");
        hikariConfig.setPoolName("[CustomNameplates]");
        hikariConfig.setJdbcUrl(String.format("jdbc:%s://%s/%s", sql, config.getString(storageMode + ".host") + ":" + config.getString(storageMode + ".port"), config.getString(storageMode + ".database")));
        hikariConfig.setUsername(config.getString(storageMode + ".user"));
        hikariConfig.setPassword(config.getString(storageMode + ".password"));
        hikariConfig.setMaximumPoolSize(config.getInt(storageMode + ".Pool-Settings.maximum-pool-size"));
        hikariConfig.setMinimumIdle(config.getInt(storageMode + ".Pool-Settings.minimum-idle"));
        hikariConfig.setMaxLifetime(config.getInt(storageMode + ".Pool-Settings.maximum-lifetime"));
        for (String property : config.getConfigurationSection(storageMode + ".properties").getKeys(false)) {
            hikariConfig.addDataSourceProperty(property, config.getString(storageMode + ".properties." + property));
        }
        if (hikariConfig.getMinimumIdle() < hikariConfig.getMaximumPoolSize()) {
            hikariConfig.setIdleTimeout(config.getInt(storageMode + ".Pool-Settings.idle-timeout"));
        } else {
            hikariConfig.setIdleTimeout(0);
        }

        try {
            hikariDataSource = new HikariDataSource(hikariConfig);

        } catch (HikariPool.PoolInitializationException e) {
            AdventureUtil.consoleMessage("[CustomNameplates] Failed to create sql connection");
        }
        AdventureUtil.consoleMessage("[CustomNameplates] Storage Mode: " + storageMode);
    }

    public boolean setGlobalConnection() {
        try {
            createNewHikariConfiguration();
            Connection connection = getConnection();
            connection.close();
            if (secondTry) {
                AdventureUtil.consoleMessage("[CustomNameplates] Successfully reconnect to SQL!");
            } else {
                secondTry = true;
            }
            return true;
        } catch (SQLException e) {
            AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to connect to SQL!</red>");
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

    public String getTable_name() {
        return table_name;
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
                AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to connect to SQL!</red>");
                close();
                e.printStackTrace();
                return null;
            }
        }
    }
}