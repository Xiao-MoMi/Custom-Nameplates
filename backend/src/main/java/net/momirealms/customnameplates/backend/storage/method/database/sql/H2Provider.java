package net.momirealms.customnameplates.backend.storage.method.database.sql;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.storage.StorageType;
import net.momirealms.customnameplates.common.dependency.Dependency;

import java.io.File;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.EnumSet;

/**
 * An implementation of AbstractSQLDatabase that uses the H2 embedded database for player data storage.
 */
public class H2Provider extends AbstractSQLDatabase {

    private Object connectionPool;
    private Method disposeMethod;
    private Method getConnectionMethod;

    public H2Provider(CustomNameplates plugin) {
        super(plugin);
    }

    /**
     * Initialize the H2 database and connection pool based on the configuration.
     */
    @Override
    public void initialize(YamlDocument config) {
        File databaseFile = new File(plugin.getDataFolder(), config.getString("H2.file", "data.db"));
        super.tablePrefix = config.getString("H2.table-prefix", "nameplates");

        final String url = String.format("jdbc:h2:%s", databaseFile.getAbsolutePath());
        ClassLoader classLoader = plugin.getDependencyManager().obtainClassLoaderWith(EnumSet.of(Dependency.H2_DRIVER));
        try {
            Class<?> connectionClass = classLoader.loadClass("org.h2.jdbcx.JdbcConnectionPool");
            Method createPoolMethod = connectionClass.getMethod("create", String.class, String.class, String.class);
            this.connectionPool = createPoolMethod.invoke(null, url, "sa", "");
            this.disposeMethod = connectionClass.getMethod("dispose");
            this.getConnectionMethod = connectionClass.getMethod("getConnection");
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        super.createTableIfNotExist();
    }

    @Override
    public void disable() {
        if (connectionPool != null) {
            try {
                disposeMethod.invoke(connectionPool);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public StorageType storageType() {
        return StorageType.H2;
    }

    @Override
    public Connection getConnection() {
        try {
            return (Connection) getConnectionMethod.invoke(connectionPool);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
