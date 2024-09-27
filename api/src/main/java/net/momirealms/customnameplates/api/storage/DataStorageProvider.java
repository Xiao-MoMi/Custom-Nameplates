package net.momirealms.customnameplates.api.storage;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.momirealms.customnameplates.api.storage.data.PlayerData;
import net.momirealms.customnameplates.api.storage.user.UserData;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Interface representing a provider for data storage.
 */
public interface DataStorageProvider {

	/**
	 * Initializes the data storage provider with the given configuration.
	 *
	 * @param config the {@link YamlDocument} configuration for the storage provider
	 */
	void initialize(YamlDocument config);

	/**
	 * Disables the data storage provider, performing any necessary cleanup.
	 */
	void disable();

	/**
	 * Retrieves the type of storage used by this provider.
	 *
	 * @return the {@link StorageType} of this provider
	 */
	StorageType getStorageType();

	/**
	 * Retrieves the player data for the specified UUID.
	 *
	 * @param uuid     the UUID of the player
	 * @param executor The executor, can be null
	 * @return a {@link CompletableFuture} containing an {@link Optional} with the player data, or empty if not found
	 */
	CompletableFuture<Optional<PlayerData>> getPlayerData(UUID uuid, Executor executor);

	/**
	 * Updates the player data for the specified UUID.
	 *
	 * @param uuid       the UUID of the player
	 * @param playerData the {@link PlayerData} to be updated
	 * @return a {@link CompletableFuture} containing a boolean indicating success or failure
	 */
	CompletableFuture<Boolean> updatePlayerData(UUID uuid, PlayerData playerData);

	/**
	 * Updates or inserts the player data for the specified UUID.
	 *
	 * @param uuid       the UUID of the player
	 * @param playerData the {@link PlayerData} to be updated or inserted
	 * @return a {@link CompletableFuture} containing a boolean indicating success or failure
	 */
	CompletableFuture<Boolean> updateOrInsertPlayerData(UUID uuid, PlayerData playerData);

	/**
	 * Retrieves the set of unique user UUIDs.
	 *
	 * @return a set of unique user UUIDs
	 */
	Set<UUID> getUniqueUsers();
}
