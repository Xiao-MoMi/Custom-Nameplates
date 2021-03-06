package net.momirealms.customnameplates.data;

import net.momirealms.customnameplates.AdventureManager;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.utils.SqlConnection;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.UUID;

public class SqlHandler {

    public static String tableName = ConfigManager.DatabaseConfig.tableName;
    public final static SqlConnection database = new SqlConnection();

    public static boolean connect() {
        return database.setGlobalConnection();
    }

    public static void close() {
        database.close();
    }

    public static void getWaitTimeOut() {
        if (ConfigManager.DatabaseConfig.use_mysql && !ConfigManager.DatabaseConfig.enable_pool) {
            try {
                Connection connection = database.getConnectionAndCheck();
                String query = "show variables LIKE 'wait_timeout'";
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    int waitTime = rs.getInt(2);
                    if (waitTime > 50) {
                        database.waitTimeOut = waitTime - 30;
                    }
                }
                rs.close();
                statement.close();
                database.closeHikariConnection(connection);
            } catch (SQLException ignored) {
                AdventureManager.consoleMessage("[CustomNameplates] Failed to get wait time out");
            }
        }
    }

    public static void createTable() {
        try {
            Connection connection = database.getConnectionAndCheck();
            Statement statement = connection.createStatement();
            if (statement == null) {
                return;
            }
            String query;
            if (ConfigManager.DatabaseConfig.use_mysql) {
                query = "CREATE TABLE IF NOT EXISTS " + tableName
                        + "(player VARCHAR(50) NOT NULL, equipped VARCHAR(50) NOT NULL, accepted INT(1) NOT NULL,"
                        + " PRIMARY KEY (player)) DEFAULT charset = " + ConfigManager.DatabaseConfig.ENCODING + ";";
            } else {
                query = "CREATE TABLE IF NOT EXISTS " + tableName
                        + "(player VARCHAR(50) NOT NULL, equipped VARCHAR(50) NOT NULL, accepted INT(1) NOT NULL,"
                        + " PRIMARY KEY (player));";
            }
            statement.executeUpdate(query);
            statement.close();
            database.closeHikariConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static PlayerData getPlayerData(UUID uuid) {
        PlayerData playerData = null;
        try {
            Connection connection = database.getConnectionAndCheck();
            String sql = "SELECT * FROM " + tableName + " WHERE player = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                playerData = new PlayerData(rs.getString(2), rs.getInt(3));
            }else {
                sql = "INSERT INTO " + tableName + "(player,equipped,accepted) values(?,?,?)";
                statement = connection.prepareStatement(sql);
                statement.setString(1, uuid.toString());
                statement.setString(2, "none");
                statement.setInt(3, 0);
                statement.executeUpdate();
            }
            rs.close();
            statement.close();
            database.closeHikariConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerData;
    }

    public static void save(PlayerData playerData, UUID uuid) {
        Connection connection = database.getConnectionAndCheck();
        try {
            String query = " SET equipped = ?, accepted = ? WHERE player = ?";
            PreparedStatement statement = connection.prepareStatement("UPDATE " + tableName + query);
            statement.setString(1, playerData.getEquippedNameplate());
            statement.setInt(2, playerData.getAccepted());
            statement.setString(3, uuid.toString());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        database.closeHikariConnection(connection);
    }

    public static void saveAll() {
        Connection connection = database.getConnectionAndCheck();
        Bukkit.getOnlinePlayers().forEach(player -> {
            try {
                PlayerData playerData = DataManager.cache.get(player.getUniqueId());
                String query = " SET equipped = ?, accepted = ? WHERE player = ?";
                PreparedStatement statement = connection.prepareStatement("UPDATE " + tableName + query);
                statement.setString(1, playerData.getEquippedNameplate());
                statement.setInt(2, playerData.getAccepted());
                statement.setString(3, String.valueOf(player.getUniqueId()));
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        database.closeHikariConnection(connection);
    }
}
