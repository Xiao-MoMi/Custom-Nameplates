package net.momirealms.customnameplates.bukkit.storage.method.database.sql;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dev.dejvokep.boostedyaml.YamlDocument;
import net.momirealms.customnameplates.api.storage.StorageType;
import net.momirealms.customnameplates.api.storage.data.PlayerData;
import net.momirealms.customnameplates.api.storage.user.UserData;
import net.momirealms.customnameplates.bukkit.BukkitCustomNameplates;
import net.momirealms.customnameplates.common.dependency.Dependency;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SQLiteProvider extends AbstractSQLDatabase {

    private Connection connection;
    private File databaseFile;
    private Constructor<?> connectionConstructor;
    private ExecutorService executor;

    public SQLiteProvider(BukkitCustomNameplates plugin) {
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

        this.executor = Executors.newFixedThreadPool(1, new ThreadFactoryBuilder().setNameFormat("cf-sqlite-%d").build());

        this.databaseFile = new File(plugin.getDataFolder(), config.getString("SQLite.file", "data") + ".db");
        super.tablePrefix = config.getString("SQLite.table-prefix", "customfishing");
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
    public StorageType getStorageType() {
        return StorageType.SQLite;
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

    @SuppressWarnings("DuplicatedCode")
    @Override
    public CompletableFuture<Optional<PlayerData>> getPlayerData(UUID uuid, boolean lock, Executor executor) {
        var future = new CompletableFuture<Optional<PlayerData>>();
        if (executor == null) executor = this.executor;
        executor.execute(() -> {
        try (
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(String.format(SqlConstants.SQL_SELECT_BY_UUID, getTableName("data")))
        ) {
            statement.setString(1, uuid.toString());
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                final byte[] dataByteArray = rs.getBytes("data");
                PlayerData data = plugin.getStorageManager().fromBytes(dataByteArray);
                data.uuid(uuid);
                int lockValue = rs.getInt(2);
                if (lockValue != 0 && getCurrentSeconds() - 30 <= lockValue) {
                    connection.close();
                    data.locked(true);
                    future.complete(Optional.of(data));
                    return;
                }
                if (lock) lockOrUnlockPlayerData(uuid, true);
                future.complete(Optional.of(data));
            } else if (Bukkit.getPlayer(uuid) != null) {
                var data = PlayerData.empty();
                data.uuid(uuid);
                insertPlayerData(uuid, data, lock, connection);
                future.complete(Optional.of(data));
            } else {
                future.complete(Optional.empty());
            }
        } catch (SQLException e) {
            plugin.getPluginLogger().warn("Failed to get " + uuid + "'s data.", e);
            future.completeExceptionally(e);
        }
        });
        return future;
    }

    @Override
    public CompletableFuture<Boolean> updateOrInsertPlayerData(UUID uuid, PlayerData playerData, boolean unlock) {
        var future = new CompletableFuture<Boolean>();
        executor.execute(() -> {
            try (
                    Connection connection = getConnection();
                    PreparedStatement statement = connection.prepareStatement(String.format(SqlConstants.SQL_SELECT_BY_UUID, getTableName("data")))
            ) {
                statement.setString(1, uuid.toString());
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    try (
                        PreparedStatement statement2 = connection.prepareStatement(String.format(SqlConstants.SQL_UPDATE_BY_UUID, getTableName("data")))
                    ) {
                        statement2.setInt(1, unlock ? 0 : getCurrentSeconds());
                        statement2.setBytes(2, plugin.getStorageManager().toBytes(playerData));
                        statement2.setString(3, uuid.toString());
                        statement2.executeUpdate();
                    } catch (SQLException e) {
                        plugin.getPluginLogger().warn("Failed to update " + uuid + "'s data.", e);
                    }
                    future.complete(true);
                } else {
                    insertPlayerData(uuid, playerData, !unlock, connection);
                    future.complete(true);
                }
            } catch (SQLException e) {
                plugin.getPluginLogger().warn("Failed to get " + uuid + "'s data.", e);
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<Boolean> updatePlayerData(UUID uuid, PlayerData playerData, boolean unlock) {
        var future = new CompletableFuture<Boolean>();
        executor.execute(() -> {
        try (
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(String.format(SqlConstants.SQL_UPDATE_BY_UUID, getTableName("data")))
        ) {
            statement.setInt(1, unlock ? 0 : getCurrentSeconds());
            statement.setBytes(2, playerData.toBytes());
            statement.setString(3, uuid.toString());
            statement.executeUpdate();
            future.complete(true);
        } catch (SQLException e) {
            plugin.getPluginLogger().warn("Failed to update " + uuid + "'s data.", e);
            future.completeExceptionally(e);
        }
        });
        return future;
    }

    @Override
    public void updateManyPlayersData(Collection<? extends UserData> users, boolean unlock) {
        String sql = String.format(SqlConstants.SQL_UPDATE_BY_UUID, getTableName("data"));
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (UserData user : users) {
                    statement.setInt(1, unlock ? 0 : getCurrentSeconds());
                    statement.setBytes(2, plugin.getStorageManager().toBytes(user.toPlayerData()));
                    statement.setString(3, user.uuid().toString());
                    statement.addBatch();
                }
                statement.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                plugin.getPluginLogger().warn("Failed to update bag data for online players", e);
            }
        } catch (SQLException e) {
            plugin.getPluginLogger().warn("Failed to get connection when saving online players' data", e);
        }
    }

    @Override
    protected void insertPlayerData(UUID uuid, PlayerData playerData, boolean lock, @Nullable Connection previous) {
        try (
            Connection connection = previous == null ? getConnection() : previous;
            PreparedStatement statement = connection.prepareStatement(String.format(SqlConstants.SQL_INSERT_DATA_BY_UUID, getTableName("data")))
        ) {
            statement.setString(1, uuid.toString());
            statement.setInt(2, lock ? getCurrentSeconds() : 0);
            statement.setBytes(3, plugin.getStorageManager().toBytes(playerData));
            statement.execute();
        } catch (SQLException e) {
            plugin.getPluginLogger().warn("Failed to insert " + uuid + "'s data.", e);
        }
    }
}