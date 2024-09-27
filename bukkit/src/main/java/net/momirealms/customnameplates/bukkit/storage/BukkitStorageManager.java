package net.momirealms.customnameplates.bukkit.storage;

import com.google.gson.JsonSyntaxException;
import dev.dejvokep.boostedyaml.YamlDocument;
import net.momirealms.customnameplates.api.helper.GsonHelper;
import net.momirealms.customnameplates.api.storage.DataStorageProvider;
import net.momirealms.customnameplates.api.storage.StorageManager;
import net.momirealms.customnameplates.api.storage.StorageType;
import net.momirealms.customnameplates.api.storage.data.PlayerData;
import net.momirealms.customnameplates.api.storage.user.UserData;
import net.momirealms.customnameplates.bukkit.BukkitCustomNameplates;
import net.momirealms.customnameplates.bukkit.storage.method.database.nosql.MongoDBProvider;
import net.momirealms.customnameplates.bukkit.storage.method.database.nosql.RedisManager;
import net.momirealms.customnameplates.bukkit.storage.method.database.sql.H2Provider;
import net.momirealms.customnameplates.bukkit.storage.method.database.sql.MariaDBProvider;
import net.momirealms.customnameplates.bukkit.storage.method.database.sql.MySQLProvider;
import net.momirealms.customnameplates.bukkit.storage.method.database.sql.SQLiteProvider;
import net.momirealms.customnameplates.bukkit.storage.method.file.JsonProvider;
import net.momirealms.customnameplates.bukkit.storage.method.file.YAMLProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class BukkitStorageManager implements StorageManager, Listener {

	private final BukkitCustomNameplates plugin;
	private DataStorageProvider dataSource;
	private StorageType previousType;
	private final ConcurrentHashMap<UUID, UserData> onlineUserMap;
	private boolean hasRedis;
	private RedisManager redisManager;
	private String serverID;

	public BukkitStorageManager(final BukkitCustomNameplates plugin) {
		this.plugin = plugin;
		this.onlineUserMap = new ConcurrentHashMap<>();
		Bukkit.getPluginManager().registerEvents(this, plugin.getBootstrap());
	}

	@Override
	public void reload() {
		YamlDocument config = plugin.getConfigManager().loadConfig("database.yml");
		this.serverID = config.getString("unique-server-id", "default");
		try {
			config.save(new File(plugin.getBootstrap().getDataFolder(), "database.yml"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		// Check if storage type has changed and reinitialize if necessary
		StorageType storageType = StorageType.valueOf(config.getString("data-storage-method", "H2"));
		if (storageType != previousType) {
			if (this.dataSource != null) this.dataSource.disable();
			this.previousType = storageType;
			switch (storageType) {
				case H2 -> this.dataSource = new H2Provider(plugin);
				case JSON -> this.dataSource = new JsonProvider(plugin);
				case YAML -> this.dataSource = new YAMLProvider(plugin);
				case SQLite -> this.dataSource = new SQLiteProvider(plugin);
				case MySQL -> this.dataSource = new MySQLProvider(plugin);
				case MariaDB -> this.dataSource = new MariaDBProvider(plugin);
				case MongoDB -> this.dataSource = new MongoDBProvider(plugin);
			}
			if (this.dataSource != null) this.dataSource.initialize(config);
			else plugin.getPluginLogger().severe("No storage type is set.");
		}

		// Handle Redis configuration
		if (!this.hasRedis && config.getBoolean("Redis.enable", false)) {
			this.redisManager = new RedisManager(plugin);
			this.redisManager.initialize(config);
			this.hasRedis = true;
		}

		// Disable Redis if it was enabled but is now disabled
		if (this.hasRedis && !config.getBoolean("Redis.enable", false) && this.redisManager != null) {
			this.hasRedis = false;
			this.redisManager.disable();
			this.redisManager = null;
		}
	}

	@Override
	public void disable() {
		HandlerList.unregisterAll(this);
		if (this.dataSource != null)
			this.dataSource.disable();
		if (this.redisManager != null)
			this.redisManager.disable();
		this.onlineUserMap.clear();
	}

	@NotNull
	@Override
	public String getServerID() {
		return serverID;
	}

	@NotNull
	@Override
	public Optional<UserData> getOnlineUser(UUID uuid) {
		return Optional.ofNullable(onlineUserMap.get(uuid));
	}

	@NotNull
	@Override
	public Collection<UserData> getOnlineUsers() {
		return onlineUserMap.values();
	}

	@Override
	public CompletableFuture<Boolean> saveUserData(UserData userData) {
		return dataSource.updatePlayerData(userData.uuid(), userData.toPlayerData());
	}

	@NotNull
	@Override
	public DataStorageProvider getDataSource() {
		return dataSource;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();

		dataSource.getPlayerData(uuid, null).thenAccept(optionalData -> {
			if (optionalData.isPresent()) {
				PlayerData playerData = optionalData.get();
				if (!player.isOnline()) return;
				addOnlineUser(player, playerData);

				//plugin.getScheduler().runTask(() -> {
				//	NameplateDataLoadEvent syncEvent = new NameplateDataLoadEvent(uuid, userData);
				//	plugin.getServer().getPluginManager().callEvent(syncEvent);
				//});
			}
		});
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();
		onlineUserMap.remove(uuid);
	}

	private void addOnlineUser(Player player, PlayerData playerData) {
		this.onlineUserMap.put(player.getUniqueId(), UserData.builder()
				.data(playerData)
				// update the name
				.name(player.getName())
				.build());
	}

	@Override
	public boolean isRedisEnabled() {
		return hasRedis;
	}

	@Override
	public byte[] toBytes(@NotNull PlayerData data) {
		return toJson(data).getBytes(StandardCharsets.UTF_8);
	}

	@NotNull
	@Override
	public String toJson(@NotNull PlayerData data) {
		return GsonHelper.get().toJson(data);
	}

	@NotNull
	@Override
	public PlayerData fromJson(String json) {
		try {
			return GsonHelper.get().fromJson(json, PlayerData.class);
		} catch (JsonSyntaxException e) {
			plugin.getPluginLogger().severe("Failed to parse PlayerData from json");
			plugin.getPluginLogger().info("Json: " + json);
			throw new RuntimeException(e);
		}
	}

	@NotNull
	@Override
	public PlayerData fromBytes(byte[] data) {
		return fromJson(new String(data, StandardCharsets.UTF_8));
	}
}
