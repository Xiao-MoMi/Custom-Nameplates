package net.momirealms.customnameplates.api.storage.data;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

/**
 * The PlayerData class holds data related to a player.
 * It includes the player's name, their nameplate and bubble data.
 */
public class PlayerData {

	public static final String DEFAULT_NAME = "";
	public static final String DEFAULT_NAMEPLATE = "none";
	public static final String DEFAULT_BUBBLE  = "none";

	@SerializedName("name")
	protected String name;
	@SerializedName("nameplate")
	protected String nameplate;
	@SerializedName("bubble")
	protected String bubble;
	protected UUID uuid;

	public PlayerData(UUID uuid, String name, String nameplate, String bubble) {
		this.uuid = uuid;
		this.name = name;
		this.nameplate = nameplate;
		this.bubble = bubble;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static PlayerData empty() {
		return new Builder()
				.uuid(new UUID(0, 0))
				.name(DEFAULT_NAME)
				.nameplate(DEFAULT_NAMEPLATE)
				.bubble(DEFAULT_BUBBLE)
				.build();
	}

	/**
	 * The Builder class provides a fluent API for constructing PlayerData instances.
	 */
	public static class Builder {

		private String name;
		private String nameplate;
		private String bubble;
		private UUID uuid;

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder nameplate(String nameplate) {
			this.nameplate = nameplate;
			return this;
		}

		public Builder bubble(String bubble) {
			this.bubble = bubble;
			return this;
		}

		public Builder uuid(UUID uuid) {
			this.uuid = uuid;
			return this;
		}

		public PlayerData build() {
			return new PlayerData(uuid, name, nameplate, bubble);
		}
	}

	public String getName() {
		return name;
	}

	public String getNameplate() {
		return nameplate;
	}

	public String getBubble() {
		return bubble;
	}

	public UUID getUuid() {
		return uuid;
	}

	/**
	 * Set the uuid of the data
	 *
	 * @param uuid uuid
	 */
	public void uuid(UUID uuid) {
		this.uuid = uuid;
	}
}
