package net.momirealms.customnameplates.backend.storage.method.database.sql;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.storage.StorageType;
import net.momirealms.customnameplates.api.storage.data.PlayerData;
import net.momirealms.customnameplates.common.dependency.Dependency;

import java.io.File;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SQLiteProvider extends AbstractSQLDatabase {

    private Connection connection;
    private File databaseFile;
    private Constructor<?> connectionConstructor;
    private ExecutorService executor;

    public SQLiteProvider(CustomNameplates plugin) {
        super(plugin);
    }

    @Override
    public void initialize(YamlDocument config) {
        ClassLoader classLoader = plugin.getDependencyManager().obtainClassLoaderWith(EnumSet.of(Dependency.SQLITE_DRIVER, Dependency.SLF4J_SIMPLE, Dependency.SLF4J_API));
        try {
            Class<?> connectionClass = classLoader.loadClass("org.sqlite.jdbc4.JDBC4Connection");
            connectionConstructor = connectionClass.getConstructor(String.class, String.class, Properties.class);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        this.executor = Executors.newFixedThreadPool(1);
        this.databaseFile = new File(plugin.getDataFolder(), config.getString("SQLite.file", "data") + ".db");
        super.tablePrefix = config.getString("SQLite.table-prefix", "nameplates");
        super.createTableIfNotExist();
    }

    @Override
    public void disable() {
        if (executor != null) {
            executor.shutdown();
        }
        try {
            if (connection != null && !connection.isClosed())
                connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StorageType storageType() {
        return StorageType.SQLITE;
    }

    /**
     * Get a connection to the SQLite database.
     *
     * @return A database connection.
     * @throws SQLException If there is an error establishing a connection.
     */
    @Override
    public Connection getConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        }
        try {
            var properties = new Properties();
            properties.setProperty("foreign_keys", Boolean.toString(true));
            properties.setProperty("encoding", "'UTF-8'");
            properties.setProperty("synchronous", "FULL");
            connection = (Connection) this.connectionConstructor.newInstance("jdbc:sqlite:" + databaseFile.toString(), databaseFile.toString(), properties);
            return connection;
        } catch (ReflectiveOperationException e) {
            if (e.getCause() instanceof SQLException) {
                throw (SQLException) e.getCause();
            }
            throw new RuntimeException(e);
        }
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
                    byte[] dataByteArray = rs.getBytes("data");
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
            statement.setBytes(2, plugin.getStorageManager().toBytes(playerData));
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
                statement.setBytes(1, plugin.getStorageManager().toBytes(playerData));
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

}