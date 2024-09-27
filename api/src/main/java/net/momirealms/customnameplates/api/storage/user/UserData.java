package net.momirealms.customnameplates.api.storage.user;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.storage.data.PlayerData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface UserData {

	/**
	 * Retrieves the username.
	 *
	 * @return the username as a {@link String}
	 */
	@NotNull
	String name();

	/**
	 * Retrieves the user's UUID.
	 *
	 * @return the UUID as a {@link UUID}
	 */
	@NotNull
	UUID uuid();

	/**
	 * Retrieves the {@link CNPlayer} instance if the player is online.
	 *
	 * @return the {@link CNPlayer} instance, or null if the player is offline
	 */
	@Nullable
	CNPlayer player();

	/**
	 * Retrieves the player's nameplate data.
	 *
	 * @return the nameplate as a {@link String}
	 */
	@NotNull
	String nameplate();

	/**
	 * Retrieves the player's bubble data.
	 *
	 * @return the bubble as a {@link String}
	 */
	@NotNull
	String bubble();

	/**
	 * Converts the user data to a minimized format that can be saved.
	 *
	 * @return the {@link PlayerData}
	 */
	@NotNull
	PlayerData toPlayerData();

	/**
	 * Creates a new {@link Builder} instance to construct {@link UserData}.
	 *
	 * @return a new {@link Builder} instance
	 */
	static Builder builder() {
		return new UserDataImpl.BuilderImpl();
	}

	/**
	 * Builder interface for constructing instances of {@link UserData}.
	 */
	interface Builder {

		/**
		 * Sets the username for the {@link UserData} being built.
		 *
		 * @param name the username to set
		 * @return the current {@link Builder} instance for method chaining
		 */
		Builder name(String name);

		/**
		 * Sets the UUID for the {@link UserData} being built.
		 *
		 * @param uuid the UUID to set
		 * @return the current {@link Builder} instance for method chaining
		 */
		Builder uuid(UUID uuid);

		/**
		 * Sets the nameplate for the {@link UserData} being built.
		 *
		 * @param nameplate the nameplate to set
		 * @return the current {@link Builder} instance for method chaining
		 */
		Builder nameplate(String nameplate);

		/**
		 * Sets the bubble for the {@link UserData} being built.
		 *
		 * @param bubble the bubble to set
		 * @return the current {@link Builder} instance for method chaining
		 */
		Builder bubble(String bubble);

		/**
		 * Sets the player data for the {@link UserData} being built.
		 *
		 * @param playerData the {@link PlayerData} to set
		 * @return the current {@link Builder} instance for method chaining
		 */
		Builder data(PlayerData playerData);

		/**
		 * Builds and returns the {@link UserData} instance based on the current state of the {@link Builder}.
		 *
		 * @return the constructed {@link UserData} instance
		 */
		UserData build();
	}
}
