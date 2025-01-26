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

import net.momirealms.customnameplates.api.storage.data.PlayerData;
import net.momirealms.customnameplates.common.plugin.feature.Reloadable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Interface to manage data storage
 */
public interface StorageManager extends Reloadable {

	/**
	 * Returns the server ID associated with this storage manager.
	 *
	 * @return the server ID as a string
	 */
	@NotNull
	String serverID();

	/**
	 * Returns the current data storage provider.
	 *
	 * @return the {@link DataStorageProvider} instance
	 */
	@NotNull
	DataStorageProvider dataSource();

	/**
	 * Checks if Redis is enabled for the storage manager.
	 *
	 * @return true if Redis is enabled, false otherwise
	 */
	boolean isRedisEnabled();

	/**
	 * Converts player data into a byte array.
	 *
	 * @param data the {@link PlayerData} to convert
	 * @return the byte array representation of the player data
	 */
	byte[] toBytes(@NotNull PlayerData data);

	/**
	 * Converts player data into a JSON string.
	 *
	 * @param data the {@link PlayerData} to convert
	 * @return the JSON string representation of the player data
	 */
	@NotNull
	String toJson(@NotNull PlayerData data);

	/**
	 * Converts a JSON string into player data.
	 *
	 * @param uuid the UUID of the player
	 * @param json the JSON string containing the player data
	 * @return the {@link PlayerData} instance created from the JSON data
	 */
	@NotNull
	PlayerData fromJson(UUID uuid, String json);

	/**
	 * Converts a byte array into player data.
	 *
	 * @param uuid the UUID of the player
	 * @param data the byte array containing the player data
	 * @return the {@link PlayerData} instance created from the byte array
	 */
	@NotNull
	PlayerData fromBytes(UUID uuid, byte[] data);
}
