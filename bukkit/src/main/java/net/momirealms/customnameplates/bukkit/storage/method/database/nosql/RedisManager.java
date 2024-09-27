package net.momirealms.customnameplates.bukkit.storage.method.database.nosql;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customnameplates.api.storage.StorageType;
import net.momirealms.customnameplates.api.storage.data.PlayerData;
import net.momirealms.customnameplates.bukkit.BukkitCustomNameplates;
import net.momirealms.customnameplates.bukkit.storage.method.AbstractStorage;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.params.XReadParams;
import redis.clients.jedis.resps.StreamEntry;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
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
    private BlockingThreadTask threadTask;
    private boolean isNewerThan5;

    public RedisManager(BukkitCustomNameplates plugin) {
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
        String info;
        try (Jedis jedis = jedisPool.getResource()) {
            info = jedis.info();
            plugin.getPluginLogger().info("Redis server connected.");
        } catch (JedisException e) {
            plugin.getPluginLogger().warn("Failed to connect redis.", e);
            return;
        }

        String version = parseRedisVersion(info);
        if (isRedisNewerThan5(version)) {
            // For Redis 5.0+
            this.threadTask = new BlockingThreadTask();
            this.isNewerThan5 = true;
        } else {
            // For Redis 2.0+
            this.subscribe();
            this.isNewerThan5 = false;
        }
    }

    /**
     * Disable the Redis connection by closing the JedisPool.
     */
    @Override
    public void disable() {
        if (threadTask != null)
            threadTask.stop();
        if (jedisPool != null && !jedisPool.isClosed())
            jedisPool.close();
    }

    /**
     * Send a message to Redis on a specified channel.
     *
     * @param message The message to send.
     */
    public void publishRedisMessage(@NotNull String message) {
        if (isNewerThan5) {
            try (Jedis jedis = jedisPool.getResource()) {
                HashMap<String, String> messages = new HashMap<>();
                messages.put("value", message);
                jedis.xadd(getStream(), StreamEntryID.NEW_ENTRY, messages);
            }
        } else {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.publish(getStream(), message);
            }
        }
    }

    /**
     * Subscribe to Redis messages on a separate thread and handle received messages.
     */
    private void subscribe() {
        Thread thread = new Thread(() -> {
            try (final Jedis jedis = password.isBlank() ?
                    new Jedis(host, port, 0, useSSL) :
                    new Jedis(host, port, DefaultJedisClientConfig
                            .builder()
                            .password(password)
                            .timeoutMillis(0)
                            .ssl(useSSL)
                            .build())
            ) {
                jedis.connect();
                jedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        if (!channel.equals(getStream())) {
                            return;
                        }
                        try {
                            handleMessage(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, getStream());
            }
        });
        thread.start();
    }

    private void handleMessage(String message) throws IOException {
        DataInputStream input = new DataInputStream(new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8)));
        String server = input.readUTF();
        if (!ConfigManager.serverGroup().equals(server))
            return;
        String type = input.readUTF();
        switch (type) {
            case "competition" -> {
                String action = input.readUTF();
                switch (action) {
                    case "start" -> {
                        plugin.getCompetitionManager().startCompetition(input.readUTF(), true, null);
                    }
                    case "end" -> {
                        if (plugin.getCompetitionManager().getOnGoingCompetition() != null)
                            plugin.getCompetitionManager().getOnGoingCompetition().end(true);
                    }
                    case "stop" -> {
                        if (plugin.getCompetitionManager().getOnGoingCompetition() != null)
                            plugin.getCompetitionManager().getOnGoingCompetition().stop(true);
                    }
                }
            }
            case "online" -> {
                plugin.getCompetitionManager().updatePlayerCount(
                        UUID.fromString(input.readUTF()),
                        input.readInt()
                );
            }
        }
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.Redis;
    }

    /**
     * Set a "change server" flag for a specified player UUID in Redis.
     *
     * @param uuid The UUID of the player.
     * @return A CompletableFuture indicating the operation's completion.
     */
    public CompletableFuture<Void> setChangeServer(UUID uuid) {
        var future = new CompletableFuture<Void>();
        plugin.getScheduler().async().execute(() -> {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(
                    getRedisKey("cf_server", uuid),
                    10,
                    new byte[0]
            );
        }
        future.complete(null);
        });
        return future;
    }

    /**
     * Get the "change server" flag for a specified player UUID from Redis and remove it.
     *
     * @param uuid The UUID of the player.
     * @return A CompletableFuture with a Boolean indicating whether the flag was set.
     */
    public CompletableFuture<Boolean> getChangeServer(UUID uuid) {
        var future = new CompletableFuture<Boolean>();
        plugin.getScheduler().async().execute(() -> {
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] key = getRedisKey("cf_server", uuid);
            if (jedis.get(key) != null) {
                jedis.del(key);
                future.complete(true);
            } else {
                future.complete(false);
            }
        }
        });
        return future;
    }

    /**
     * Asynchronously retrieve player data from Redis.
     *
     * @param uuid     The UUID of the player.
     * @param lock     Flag indicating whether to lock the data.
     * @param executor The executor, can be null
     * @return A CompletableFuture with an optional PlayerData.
     */
    @Override
    public CompletableFuture<Optional<PlayerData>> getPlayerData(UUID uuid, Executor executor) {
        var future = new CompletableFuture<Optional<PlayerData>>();
        if (executor == null) executor = plugin.getScheduler().async();
        executor.execute(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                byte[] key = getRedisKey("cf_data", uuid);
                byte[] data = jedis.get(key);
                jedis.del(key);
                if (data != null) {
                    future.complete(Optional.of(plugin.getStorageManager().fromBytes(data)));
                } else {
                    future.complete(Optional.empty());
                }
            } catch (Exception e) {
                future.complete(Optional.empty());
            }
        });
        return future;
    }

    /**
     * Asynchronously update player data in Redis.
     *
     * @param uuid       The UUID of the player.
     * @param playerData The player's data to update.
     * @return A CompletableFuture indicating the update result.
     */
    @Override
    public CompletableFuture<Boolean> updatePlayerData(UUID uuid, PlayerData playerData) {
        var future = new CompletableFuture<Boolean>();
        plugin.getScheduler().async().execute(() -> {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(
                    getRedisKey("cf_data", uuid),
                    10,
                    playerData.toBytes()
            );
            future.complete(true);
        } catch (Exception e) {
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

    public static String getStream() {
        return STREAM;
    }

    private boolean isRedisNewerThan5(String version) {
        String[] split = version.split("\\.");
        int major = Integer.parseInt(split[0]);
        if (major < 7) {
            plugin.getPluginLogger().warn(String.format("Detected that you are running an outdated Redis server. v%s. ", version));
            plugin.getPluginLogger().warn("It's recommended to update to avoid security vulnerabilities!");
        }
        return major >= 5;
    }

    private String parseRedisVersion(String info) {
        for (String line : info.split("\n")) {
            if (line.startsWith("redis_version:")) {
                return line.split(":")[1];
            }
        }
        return "Unknown";
    }

    public class BlockingThreadTask {

        private boolean stopped;

        public void stop() {
            stopped = true;
        }

        public BlockingThreadTask() {
            Thread thread = new Thread(() -> {
                var map = new HashMap<String, StreamEntryID>();
                map.put(getStream(), StreamEntryID.XREAD_NEW_ENTRY);
                while (!this.stopped) {
                    try {
                        var connection = getJedis();
                        if (connection != null) {
                            var messages = connection.xread(XReadParams.xReadParams().count(1).block(2000), map);
                            connection.close();
                            if (messages != null && !messages.isEmpty()) {
                                for (Map.Entry<String, List<StreamEntry>> message : messages) {
                                    if (message.getKey().equals(getStream())) {
                                        var value = message.getValue().get(0).getFields().get("value");
                                        try {
                                            handleMessage(value);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        } else {
                            Thread.sleep(2000);
                        }
                    } catch (Exception e) {
                        plugin.getPluginLogger().warn("Failed to connect redis. Try reconnecting 10s later",e);
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException ex) {
                            this.stopped = true;
                        }
                    }
                }
            });
            thread.start();
        }
    }
}
