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

package net.momirealms.customnameplates.utils;

import com.zaxxer.hikari.HikariDataSource;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnection {

    private String driver = "com.mysql.jdbc.Driver";

    private final File dataFolder = CustomNameplates.instance.getDataFolder();

    private boolean secon = false;
    private boolean isfirstry = true;

    public int waitTimeOut = 10;

    public File userdata = new File(dataFolder, "data.db");
    private Connection connection = null;
    private HikariDataSource hikari = null;

    /*
    新建Hikari配置
     */
    private void createNewHikariConfiguration() {
        hikari = new HikariDataSource();
        hikari.setPoolName("[Nameplates]");
        hikari.setJdbcUrl(ConfigManager.DatabaseConfig.url);
        hikari.setUsername(ConfigManager.DatabaseConfig.user);
        hikari.setPassword(ConfigManager.DatabaseConfig.password);
        hikari.setMaximumPoolSize(ConfigManager.DatabaseConfig.maximum_pool_size);
        hikari.setMinimumIdle(ConfigManager.DatabaseConfig.minimum_idle);
        hikari.setMaxLifetime(ConfigManager.DatabaseConfig.maximum_lifetime);
        hikari.addDataSourceProperty("cachePrepStmts", "true");
        hikari.addDataSourceProperty("prepStmtCacheSize", "250");
        hikari.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikari.addDataSourceProperty("userServerPrepStmts", "true");
        if (hikari.getMinimumIdle() < hikari.getMaximumPoolSize()) {
            hikari.setIdleTimeout(ConfigManager.DatabaseConfig.idle_timeout);
        } else {
            hikari.setIdleTimeout(0);
        }
    }

    /*
    设置驱动，区分Mysql5与8
     */
    private void setDriver() {
        if(ConfigManager.DatabaseConfig.use_mysql){
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                driver = ("com.mysql.jdbc.Driver");
                return;
            }
            driver = ("com.mysql.cj.jdbc.Driver");
        }else {
            driver = ("org.sqlite.JDBC");
        }
    }

    public boolean setGlobalConnection() {
        setDriver();
        try {
            if (ConfigManager.DatabaseConfig.enable_pool) {
                createNewHikariConfiguration();
                Connection connection = getConnection();
                closeHikariConnection(connection);
            } else {
                Class.forName(driver);
                if(ConfigManager.DatabaseConfig.use_mysql){
                    connection = DriverManager.getConnection(ConfigManager.DatabaseConfig.url, ConfigManager.DatabaseConfig.user, ConfigManager.DatabaseConfig.password);
                }else {
                    connection = DriverManager.getConnection("jdbc:sqlite:" + userdata.toString());
                }
            }
            if (secon) {
                AdventureUtil.consoleMessage("<gradient:#DDE4FF:#8DA2EE>[CustomNameplates]</gradient> <color:#F5F5F5>Successfully reconnect to SQL!");
            } else {
                secon = true;
            }
            return true;
        } catch (SQLException e) {
            AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to connect to SQL!</red>");
            e.printStackTrace();
            close();
            return false;
        } catch (ClassNotFoundException e) {
            AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to load JDBC driver</red>");
        }
        return false;
    }

    public Connection getConnectionAndCheck() {
        if (!canConnect()) {
            return null;
        }
        try {
            return getConnection();
        } catch (SQLException e) {
            if (isfirstry) {
                isfirstry = false;
                close();
                return getConnectionAndCheck();
            } else {
                isfirstry = true;
                AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to connect to SQL!</red>");
                close();
                e.printStackTrace();
                return null;
            }
        }
    }

    public Connection getConnection() throws SQLException {
        if (ConfigManager.DatabaseConfig.enable_pool) {
            return hikari.getConnection();
        } else {
            return connection;
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canConnect() {
        try {
            if (ConfigManager.DatabaseConfig.enable_pool) {
                if (hikari == null) {
                    return setGlobalConnection();
                }
                if (hikari.isClosed()) {
                    return setGlobalConnection();
                }
            } else {
                if (connection == null) {
                    return setGlobalConnection();
                }
                if (connection.isClosed()) {
                    return setGlobalConnection();
                }
                if (ConfigManager.DatabaseConfig.use_mysql) {
                    if (!connection.isValid(waitTimeOut)) {
                        secon = false;
                        return setGlobalConnection();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void closeHikariConnection(Connection connection) {
        if (!ConfigManager.DatabaseConfig.enable_pool) {
            return;
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
            if (hikari != null) {
                hikari.close();
            }
        } catch (SQLException e) {
            AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to close SQL!</red>");
            e.printStackTrace();
        }
    }
}