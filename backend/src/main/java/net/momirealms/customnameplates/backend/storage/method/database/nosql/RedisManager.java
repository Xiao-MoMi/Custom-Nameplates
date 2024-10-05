package net.momirealms.customnameplates.backend.storage.method.database.nosql;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.storage.StorageType;
import net.momirealms.customnameplates.api.storage.data.PlayerData;
import net.momirealms.customnameplates.backend.storage.method.AbstractStorage;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class RedisManager extends AbstractStorage {

    private static RedisManager instance;
    private final static String STREAM = "customnameplate";
    private JedisPool jedisPool;
    private String password;
    private int port;
    private String host;
    private boolean useSSL;

    public RedisManager(CustomNameplates plugin) {
        super(plugin);
        instance = this;
    }

    /**
     * Get the singleton instance of the RedisManager.
     *
     * @return The RedisManager instance.
     */
    public static RedisManager getInstance() {
        return instance;
    }

    /**
     * Get a Jedis resource for interacting with the Redis server.
     *
     * @return A Jedis resource.
     */
    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    /**
     * Initialize the Redis connection and configuration based on the plugin's YAML configuration.
     */
    @Override
    public void initialize(YamlDocument config) {
        Section section = config.getSection("Redis");
        if (section == null) {
            plugin.getPluginLogger().warn("Failed to load database config. It seems that your config is broken. Please regenerate a new one.");
            return;
        }

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setTestWhileIdle(true);
        jedisPoolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(30000));
        jedisPoolConfig.setNumTestsPerEvictionRun(-1);
        jedisPoolConfig.setMinEvictableIdleDuration(Duration.ofMillis(section.getInt("MinEvictableIdleTimeMillis", 1800000)));
        jedisPoolConfig.setMaxTotal(section.getInt("MaxTotal",8));
        jedisPoolConfig.setMaxIdle(section.getInt("MaxIdle",8));
        jedisPoolConfig.setMinIdle(section.getInt("MinIdle",1));
        jedisPoolConfig.setMaxWait(Duration.ofMillis(section.getInt("MaxWaitMillis")));

        password = section.getString("password", "");
        port = section.getInt("port", 6379);
        host = section.getString("host", "localhost");
        useSSL = section.getBoolean("use-ssl", false);

        if (password.isBlank()) {
            jedisPool = new JedisPool(jedisPoolConfig, host, port, 0, useSSL);
        } else {
            jedisPool = new JedisPool(jedisPoolConfig, host, port, 0, password, useSSL);
        }
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.info();
            plugin.getPluginLogger().info("Redis server connected.");
        } catch (JedisException e) {
            plugin.getPluginLogger().warn("Failed to connect redis.", e);
        }
    }

    /**
     * Disable the Redis connection by closing the JedisPool.
     */
    @Override
    public void disable() {
        if (jedisPool != null && !jedisPool.isClosed())
            jedisPool.close();
    }

    @Override
    public StorageType storageType() {
        return StorageType.Redis;
    }

    @Override
    public CompletableFuture<Optional<PlayerData>> getPlayerData(UUID uuid, Executor executor) {
        CompletableFuture<Optional<PlayerData>> future = new CompletableFuture<>();
        if (executor == null) executor = plugin.getScheduler().async();
        executor.execute(() -> {
            try (Jedis jedis = getJedis()) {
                byte[] key = getRedisKey("cn_data", uuid);
                byte[] data = jedis.get(key);
                if (data != null) {
                    future.complete(Optional.of(plugin.getStorageManager().fromBytes(uuid, data)));
                    plugin.debug(() -> "Redis data retrieved for " + uuid + "; normal data");
                } else {
                    future.complete(Optional.empty());
                    plugin.debug(() -> "Redis data retrieved for " + uuid + "; empty data");
                }
            } catch (Exception e) {
                plugin.getPluginLogger().warn("Failed to get redis data for " + uuid, e);
                future.complete(Optional.empty());
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<Boolean> updatePlayerData(PlayerData playerData, Executor executor) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (executor == null) executor = plugin.getScheduler().async();
        executor.execute(() -> {
            try (Jedis jedis = getJedis()) {
                jedis.setex(
                        getRedisKey("cn_data", playerData.uuid()),
                        1200,
                        plugin.getStorageManager().toBytes(playerData)
                );
                plugin.debug(() -> "Redis data set for " + playerData.uuid());
                future.complete(true);
            } catch (Exception e) {
                plugin.getPluginLogger().warn("Failed to set redis data for player " + playerData.uuid(), e);
                future.complete(false);
            }
        });
        return future;
    }

    @Override
    public Set<UUID> getUniqueUsers() {
        return new HashSet<>();
    }

    /**
     * Generate a Redis key for a specified key and UUID.
     *
     * @param key  The key identifier.
     * @param uuid The UUID to include in the key.
     * @return A byte array representing the Redis key.
     */
    private byte[] getRedisKey(String key, @NotNull UUID uuid) {
        return (key + ":" + uuid).getBytes(StandardCharsets.UTF_8);
    }
}
