/*
 *  Copyright (C) <2024> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customnameplates.api.storage;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.momirealms.customnameplates.api.storage.data.PlayerData;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Interface representing a provider for data storage. This provider handles the retrieval and updating of player data.
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
	 * Returns the type of storage used by this provider.
	 *
	 * @return the {@link StorageType} of the storage provider
	 */
	StorageType storageType();

	/**
	 * Retrieves the player data for a specific UUID.
	 *
	 * @param uuid     the UUID of the player
	 * @param executor the executor to run the task asynchronously
	 * @return a {@link CompletableFuture} containing an {@link Optional} of {@link PlayerData}
	 */
	CompletableFuture<Optional<PlayerData>> getPlayerData(UUID uuid, Executor executor);

	/**
	 * Updates the player data in the storage.
	 *
	 * @param playerData the player data to update
	 * @param executor   the executor to run the task asynchronously
	 * @return a {@link CompletableFuture} indicating whether the update was successful
	 */
	CompletableFuture<Boolean> updatePlayerData(PlayerData playerData, Executor executor);

	/**
	 * Returns a set of all unique users (UUIDs) in the storage.
	 *
	 * @return a set of unique user UUIDs
	 */
	Set<UUID> getUniqueUsers();
}

