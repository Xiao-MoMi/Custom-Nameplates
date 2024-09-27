package net.momirealms.customnameplates.bukkit.storage.method;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.momirealms.customnameplates.api.storage.DataStorageProvider;
import net.momirealms.customnameplates.api.storage.data.PlayerData;
import net.momirealms.customnameplates.bukkit.BukkitCustomNameplates;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractStorage implements DataStorageProvider {

	protected BukkitCustomNameplates plugin;

	public AbstractStorage(BukkitCustomNameplates plugin) {
		this.plugin = plugin;
	}

	@Override
	public void initialize(YamlDocument config) {
		// This method can be overridden in subclasses to perform initialization tasks specific to the storage type.
	}

	@Override
	public void disable() {
		// This method can be overridden in subclasses to perform cleanup or shutdown tasks specific to the storage type.
	}

	/**
	 * Get the current time in seconds since the Unix epoch.
	 *
	 * @return The current time in seconds.
	 */
	public int getCurrentSeconds() {
		return (int) Instant.now().getEpochSecond();
	}

	@Override
	public CompletableFuture<Boolean> updateOrInsertPlayerData(UUID uuid, PlayerData playerData) {
		// By default, delegate to the updatePlayerData method to update or insert player data.
		return updatePlayerData(uuid, playerData);
	}
}
