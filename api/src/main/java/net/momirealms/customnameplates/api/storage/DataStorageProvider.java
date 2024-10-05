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

	StorageType getStorageType();

	CompletableFuture<Optional<PlayerData>> getPlayerData(UUID uuid, Executor executor);

	CompletableFuture<Boolean> updatePlayerData(PlayerData playerData, Executor executor);

	Set<UUID> getUniqueUsers();
}
