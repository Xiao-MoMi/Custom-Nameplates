package net.momirealms.customnameplates.backend.storage;

import com.google.gson.JsonSyntaxException;
import dev.dejvokep.boostedyaml.YamlDocument;
import net.momirealms.customnameplates.api.AbstractCNPlayer;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.event.DataLoadEvent;
import net.momirealms.customnameplates.api.feature.JoinQuitListener;
import net.momirealms.customnameplates.api.helper.GsonHelper;
import net.momirealms.customnameplates.api.storage.DataStorageProvider;
import net.momirealms.customnameplates.api.storage.StorageManager;
import net.momirealms.customnameplates.api.storage.StorageType;
import net.momirealms.customnameplates.api.storage.data.JsonData;
import net.momirealms.customnameplates.api.storage.data.PlayerData;
import net.momirealms.customnameplates.backend.storage.method.DummyStorage;
import net.momirealms.customnameplates.backend.storage.method.database.nosql.MongoDBProvider;
import net.momirealms.customnameplates.backend.storage.method.database.nosql.RedisManager;
import net.momirealms.customnameplates.backend.storage.method.database.sql.H2Provider;
import net.momirealms.customnameplates.backend.storage.method.database.sql.MariaDBProvider;
import net.momirealms.customnameplates.backend.storage.method.database.sql.MySQLProvider;
import net.momirealms.customnameplates.backend.storage.method.database.sql.SQLiteProvider;
import net.momirealms.customnameplates.backend.storage.method.file.JsonProvider;
import net.momirealms.customnameplates.backend.storage.method.file.YAMLProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executor;

public class StorageManagerImpl implements StorageManager, JoinQuitListener {

	private final CustomNameplates plugin;
	private DataStorageProvider dataSource;
	private StorageType previousType;
	private boolean hasRedis;
	private RedisManager redisManager;
	private String serverID;

	public StorageManagerImpl(final CustomNameplates plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("DuplicatedCode")
	@Override
	public void onPlayerJoin(CNPlayer player) {
		UUID uuid = player.uuid();
		Executor async = plugin.getScheduler().async();
		if (hasRedis) {
			this.redisManager.getPlayerData(uuid, async).thenAccept(playerData1 -> {
				if (playerData1.isPresent()) {
					PlayerData data = playerData1.get();
					((AbstractCNPlayer) player).setLoaded(true);
					handleDataLoad(player, data);
					plugin.getEventManager().dispatch(DataLoadEvent.class, data);
					this.redisManager.updatePlayerData(data, async).thenAccept(result -> {
						if (!result) {
							plugin.getPluginLogger().warn("Failed to refresh redis player data for " + player.name());
						}
					});
				} else {
					this.dataSource().getPlayerData(uuid, async).thenAccept(playerData2 -> {
						if (playerData2.isPresent()) {
							PlayerData data = playerData2.get();
							((AbstractCNPlayer) player).setLoaded(true);
							handleDataLoad(player, data);
							plugin.getEventManager().dispatch(DataLoadEvent.class, data);
							this.redisManager.updatePlayerData(data, async).thenAccept(result -> {
								if (!result) {
									plugin.getPluginLogger().warn("Failed to refresh redis player data for " + player.name());
								}
							});
						} else {
							plugin.getPluginLogger().warn("Failed to load player data for " + player.name());
						}
					});
				}
			});
		} else {
			this.dataSource().getPlayerData(uuid, async).thenAccept(playerData -> {
				if (playerData.isPresent()) {
					PlayerData data = playerData.get();
					((AbstractCNPlayer) player).setLoaded(true);
					handleDataLoad(player, data);
				} else {
					plugin.getPluginLogger().warn("Failed to load player data for " + player.name());
				}
			});
		}
	}

	private void handleDataLoad(CNPlayer player, PlayerData data) {
		player.setBubbleData(data.bubble());
		player.setNameplateData(data.nameplate());
		plugin.getUnlimitedTagManager().togglePreviewing(player, data.previewTags());
	}

	@Override
	public void onPlayerQuit(CNPlayer player) {
		((AbstractCNPlayer) player).setLoaded(false);
	}

	@Override
	public void reload() {
		YamlDocument config = plugin.getConfigManager().loadConfig("database.yml");
		this.serverID = config.getString("unique-server-id", "default");
		try {
			config.save(new File(plugin.getDataFolder(), "database.yml"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		// Check if storage type has changed and reinitialize if necessary
		StorageType storageType = StorageType.valueOf(config.getString("data-storage-method", "H2").toUpperCase(Locale.ENGLISH));
		if (storageType != previousType) {
			if (this.dataSource != null) this.dataSource.disable();
			this.previousType = storageType;
			switch (storageType) {
				case H2 -> this.dataSource = new H2Provider(plugin);
				case JSON -> this.dataSource = new JsonProvider(plugin);
				case YAML -> this.dataSource = new YAMLProvider(plugin);
				case SQLITE -> this.dataSource = new SQLiteProvider(plugin);
				case MYSQL -> this.dataSource = new MySQLProvider(plugin);
				case MARIADB -> this.dataSource = new MariaDBProvider(plugin);
				case MONGODB -> this.dataSource = new MongoDBProvider(plugin);
				case NONE -> this.dataSource = new DummyStorage(plugin);
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
		if (this.dataSource != null)
			this.dataSource.disable();
		if (this.redisManager != null)
			this.redisManager.disable();
	}

	@NotNull
	@Override
	public String serverID() {
		return serverID;
	}

	@NotNull
	@Override
	public DataStorageProvider dataSource() {
		return dataSource;
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
		return GsonHelper.get().toJson(data.toJsonData());
	}

	@NotNull
	@Override
	public PlayerData fromJson(UUID uuid, String json) {
		try {
			return GsonHelper.get().fromJson(json, JsonData.class).toPlayerData(uuid);
		} catch (JsonSyntaxException e) {
			plugin.getPluginLogger().severe("Failed to get PlayerData from json. Json: " + json);
			throw new RuntimeException(e);
		}
	}

	@NotNull
	@Override
	public PlayerData fromBytes(UUID uuid, byte[] data) {
		return fromJson(uuid, new String(data, StandardCharsets.UTF_8));
	}
}
