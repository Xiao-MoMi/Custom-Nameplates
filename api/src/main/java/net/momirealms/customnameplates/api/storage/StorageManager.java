package net.momirealms.customnameplates.api.storage;

import net.momirealms.customnameplates.api.storage.data.PlayerData;
import net.momirealms.customnameplates.api.storage.user.UserData;
import net.momirealms.customnameplates.common.plugin.feature.Reloadable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for managing storage.
 */
public interface StorageManager extends Reloadable {

	/**
	 * Retrieves the server ID.
	 *
	 * @return the server ID as a String
	 */
	@NotNull
	String getServerID();

	/**
	 * Retrieves the user data for an online user by their UUID.
	 *
	 * @param uuid the UUID of the user
	 * @return an {@link Optional} containing the {@link UserData} if the user is online, or empty if not
	 */
	@NotNull
	Optional<UserData> getOnlineUser(UUID uuid);

	/**
	 * Retrieves a collection of all online users.
	 *
	 * @return a collection of {@link UserData} for all online users
	 */
	@NotNull
	Collection<UserData> getOnlineUsers();

	/**
	 * Saves the user data.
	 *
	 * @param userData the {@link UserData} to be saved
	 * @return a {@link CompletableFuture} containing a boolean indicating success or failure
	 */
	CompletableFuture<Boolean> saveUserData(UserData userData);

	/**
	 * Retrieves the data storage provider.
	 *
	 * @return the {@link DataStorageProvider} instance
	 */
	@NotNull
	DataStorageProvider getDataSource();

	/**
	 * Checks if Redis is enabled for data storage.
	 *
	 * @return true if Redis is enabled, false otherwise
	 */
	boolean isRedisEnabled();

	/**
	 * Converts {@link PlayerData} to a byte array.
	 *
	 * @param data the {@link PlayerData} to be converted
	 * @return the byte array representation of {@link PlayerData}
	 */
	byte[] toBytes(@NotNull PlayerData data);

	/**
	 * Converts {@link PlayerData} to JSON format.
	 *
	 * @param data the {@link PlayerData} to be converted
	 * @return the JSON string representation of {@link PlayerData}
	 */
	@NotNull String toJson(@NotNull PlayerData data);

	/**
	 * Converts a JSON string to {@link PlayerData}.
	 *
	 * @param json the JSON string to be converted
	 * @return the {@link PlayerData} object
	 */
	@NotNull PlayerData fromJson(String json);

	/**
	 * Converts a byte array to {@link PlayerData}.
	 *
	 * @param data the byte array to be converted
	 * @return the {@link PlayerData} object
	 */
	@NotNull PlayerData fromBytes(byte[] data);
}
