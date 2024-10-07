package net.momirealms.customnameplates.backend.storage.method.database.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.storage.StorageType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public abstract class AbstractHikariDatabase extends AbstractSQLDatabase {

    private HikariDataSource dataSource;
    private final String driverClass;
    private final String sqlBrand;

    public AbstractHikariDatabase(CustomNameplates plugin) {
        super(plugin);
        this.driverClass = storageType() == StorageType.MARIADB ? "org.mariadb.jdbc.Driver" : "com.mysql.cj.jdbc.Driver";
        this.sqlBrand = storageType() == StorageType.MARIADB ? "MariaDB" : "MySQL";
        try {
            Class.forName(this.driverClass);
        } catch (ClassNotFoundException e1) {
            if (storageType() == StorageType.MARIADB) {
                plugin.getPluginLogger().warn("No MariaDB driver is found");
            } else if (storageType() == StorageType.MYSQL) {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                } catch (ClassNotFoundException e2) {
                    plugin.getPluginLogger().warn("No MySQL driver is found");
                }
            }
        }
    }

    @Override
    public void initialize(YamlDocument config) {
        Section section = config.getSection(sqlBrand);

        if (section == null) {
            plugin.getPluginLogger().warn("Failed to load database config. It seems that your config is broken. Please regenerate a new one.");
            return;
        }

        super.tablePrefix = section.getString("table-prefix", "nameplates");
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setUsername(section.getString("user", "root"));
        hikariConfig.setPassword(section.getString("password", "pa55w0rd"));
        hikariConfig.setJdbcUrl(String.format("jdbc:%s://%s:%s/%s%s",
                sqlBrand.toLowerCase(Locale.ENGLISH),
                section.getString("host", "localhost"),
                section.getString("port", "3306"),
                section.getString("database", "minecraft"),
                section.getString("connection-parameters")
        ));
        hikariConfig.setDriverClassName(driverClass);
        hikariConfig.setMaximumPoolSize(section.getInt("Pool-Settings.max-pool-size", 10));
        hikariConfig.setMinimumIdle(section.getInt("Pool-Settings.min-idle", 10));
        hikariConfig.setMaxLifetime(section.getLong("Pool-Settings.max-lifetime", 180000L));
        hikariConfig.setConnectionTimeout(section.getLong("Pool-Settings.time-out", 20000L));
        hikariConfig.setPoolName("CustomNameplatesHikariPool");
        try {
            hikariConfig.setKeepaliveTime(section.getLong("Pool-Settings.keep-alive-time", 60000L));
        } catch (NoSuchMethodError ignored) {
        }

        final Properties properties = new Properties();
        properties.putAll(
                Map.of("cachePrepStmts", "true",
                        "prepStmtCacheSize", "250",
                        "prepStmtCacheSqlLimit", "2048",
                        "useServerPrepStmts", "true",
                        "useLocalSessionState", "true",
                        "useLocalTransactionState", "true"
                ));
        properties.putAll(
                Map.of(
                        "rewriteBatchedStatements", "true",
                        "cacheResultSetMetadata", "true",
                        "cacheServerConfiguration", "true",
                        "elideSetAutoCommits", "true",
                        "maintainTimeStats", "false")
        );
        hikariConfig.setDataSourceProperties(properties);
        dataSource = new HikariDataSource(hikariConfig);
        super.createTableIfNotExist();
    }

    @Override
    public void disable() {
        if (dataSource != null && !dataSource.isClosed())
            dataSource.close();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
