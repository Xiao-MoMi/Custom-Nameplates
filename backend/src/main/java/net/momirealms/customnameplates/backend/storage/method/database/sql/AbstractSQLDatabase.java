package net.momirealms.customnameplates.backend.storage.method.database.sql;

import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.storage.data.PlayerData;
import net.momirealms.customnameplates.backend.storage.method.AbstractStorage;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * An abstract base class for SQL database implementations that handle player data storage.
 */
public abstract class AbstractSQLDatabase extends AbstractStorage {

    protected String tablePrefix;

    public AbstractSQLDatabase(CustomNameplates plugin) {
        super(plugin);
    }

    /**
     * Get a connection to the SQL database.
     *
     * @return A database connection.
     * @throws SQLException If there is an error establishing a connection.
     */
    public abstract Connection getConnection() throws SQLException;

    /**
     * Create tables for storing data if they don't exist in the database.
     */
    public void createTableIfNotExist() {
        try (Connection connection = getConnection()) {
            final String[] databaseSchema = getSchema(storageType().name().toLowerCase(Locale.ENGLISH));
            try (Statement statement = connection.createStatement()) {
                for (String tableCreationStatement : databaseSchema) {
                    statement.execute(tableCreationStatement);
                }
            } catch (SQLException e) {
                plugin.getPluginLogger().warn("Failed to create tables", e);
            }
        } catch (SQLException e) {
            plugin.getPluginLogger().warn("Failed to get sql connection", e);
        } catch (IOException e) {
            plugin.getPluginLogger().warn("Failed to get schema resource", e);
        }
    }

    /**
     * Get the SQL schema from a resource file.
     *
     * @param fileName The name of the schema file.
     * @return An array of SQL statements to create tables.
     * @throws IOException If there is an error reading the schema resource.
     */
    private String[] getSchema(@NotNull String fileName) throws IOException {
        return replaceSchemaPlaceholder(new String(Objects.requireNonNull(plugin.getResourceStream("schema/" + fileName + ".sql"))
               .readAllBytes(), StandardCharsets.UTF_8)).split(";");
    }

    /**
     * Replace placeholder values in SQL schema with the table prefix.
     *
     * @param sql The SQL schema string.
     * @return The SQL schema string with placeholders replaced.
     */
    private String replaceSchemaPlaceholder(@NotNull String sql) {
        return sql.replace("{prefix}", tablePrefix);
    }

    /**
     * Get the name of a database table based on a sub-table name and the table prefix.
     *
     * @param sub The sub-table name.
     * @return The full table name.
     */
    public String getTableName(String sub) {
        return getTablePrefix() + "_" + sub;
    }

    /**
     * Get the current table prefix.
     *
     * @return The table prefix.
     */
    public String getTablePrefix() {
        return tablePrefix;
    }

    @Override
    public CompletableFuture<Optional<PlayerData>> getPlayerData(UUID uuid, Executor executor) {
        CompletableFuture<Optional<PlayerData>> future = new CompletableFuture<>();
        if (executor == null) executor = plugin.getScheduler().async();
        executor.execute(() -> {
            try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(String.format(SqlConstants.SQL_SELECT_BY_UUID, getTableName("data")))
            ) {
                statement.setString(1, uuid.toString());
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    Blob blob = rs.getBlob("data");
                    byte[] dataByteArray = blob.getBytes(1, (int) blob.length());
                    blob.free();
                    future.complete(Optional.of(plugin.getStorageManager().fromBytes(uuid, dataByteArray)));
                } else if (plugin.getPlayer(uuid) != null) {
                    var data = PlayerData.empty(uuid);
                    this.insertPlayerData(uuid, data);
                    future.complete(Optional.of(data));
                } else {
                    future.complete(Optional.empty());
                }
            } catch (SQLException e) {
                plugin.getPluginLogger().warn("Failed to get " + uuid + "'s data.", e);
                future.complete(Optional.empty());
            }
        });
        return future;
    }

    public void insertPlayerData(UUID uuid, PlayerData playerData) {
        try (
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(String.format(SqlConstants.SQL_INSERT_DATA_BY_UUID, getTableName("data")))
        ) {
            statement.setString(1, uuid.toString());
            statement.setBlob(2, new ByteArrayInputStream(plugin.getStorageManager().toBytes(playerData)));
            statement.execute();
        } catch (SQLException e) {
            plugin.getPluginLogger().warn("Failed to insert " + uuid + "'s data.", e);
        }
    }

    @Override
    public CompletableFuture<Boolean> updatePlayerData(PlayerData playerData, Executor executor) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (executor == null) executor = plugin.getScheduler().async();
        executor.execute(() -> {
            try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(String.format(SqlConstants.SQL_UPDATE_BY_UUID, getTableName("data")))
            ) {
                statement.setBlob(1, new ByteArrayInputStream(plugin.getStorageManager().toBytes(playerData)));
                statement.setString(2, playerData.uuid().toString());
                statement.executeUpdate();
                future.complete(true);
            } catch (SQLException e) {
                plugin.getPluginLogger().warn("Failed to update " + playerData.uuid() + "'s data.", e);
                future.complete(false);
            }
        });
        return future;
    }

    @Override
    public Set<UUID> getUniqueUsers() {
        Set<UUID> uuids = new HashSet<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(String.format(SqlConstants.SQL_SELECT_ALL_UUID, getTableName("data")))) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    uuids.add(uuid);
                }
            }
        } catch (SQLException e) {
            plugin.getPluginLogger().warn("Failed to get unique data.", e);
        }
        return uuids;
    }

    public static class SqlConstants {
        public static final String SQL_SELECT_BY_UUID = "SELECT * FROM `%s` WHERE `uuid` = ?";
        public static final String SQL_SELECT_ALL_UUID = "SELECT uuid FROM `%s`";
        public static final String SQL_UPDATE_BY_UUID = "UPDATE `%s` SET `data` = ? WHERE `uuid` = ?";
        public static final String SQL_INSERT_DATA_BY_UUID = "INSERT INTO `%s`(`uuid`, `data`) VALUES(?, ?)";
    }
}
