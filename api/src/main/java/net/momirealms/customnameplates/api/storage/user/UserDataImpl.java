package net.momirealms.customnameplates.api.storage.user;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.storage.data.PlayerData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class UserDataImpl implements UserData {

	private final String name;
	private final UUID uuid;
	private final String nameplate;
	private final String bubble;

	public UserDataImpl(String name, UUID uuid, String nameplate, String bubble) {
		this.name = name;
		this.uuid = uuid;
		this.nameplate = nameplate;
		this.bubble = bubble;
	}

	public static class BuilderImpl implements Builder {
		private String name;
		private UUID uuid;
		private String nameplate;
		private String bubble;

		@Override
		public Builder name(String name) {
			this.name = name;
			return this;
		}

		@Override
		public Builder uuid(UUID uuid) {
			this.uuid = uuid;
			return this;
		}

		@Override
		public Builder nameplate(String nameplate) {
			this.nameplate = nameplate;
			return this;
		}

		@Override
		public Builder bubble(String bubble) {
			this.bubble = bubble;
			return this;
		}

		@Override
		public Builder data(PlayerData playerData) {
			this.name = playerData.getName();
			this.uuid = playerData.getUuid();
			this.nameplate = playerData.getNameplate();
			this.bubble = playerData.getBubble();
			return this;
		}

		@Override
		public UserData build() {
			return new UserDataImpl(name, uuid, nameplate, bubble);
		}
	}

	@NotNull
	@Override
	public String name() {
		return name;
	}

	@NotNull
	@Override
	public UUID uuid() {
		return uuid;
	}

	@Override
	@Nullable
	public CNPlayer player() {
		return CustomNameplates.getInstance().getPlayer(uuid);
	}

	@NotNull
	@Override
	public String nameplate() {
		return nameplate;
	}

	@NotNull
	@Override
	public String bubble() {
		return bubble;
	}

	@NotNull
	@Override
	public PlayerData toPlayerData() {
		return PlayerData.builder()
				.uuid(uuid)
				.name(name)
				.nameplate(nameplate)
				.bubble(bubble)
				.build();
	}
}
