package net.momirealms.customnameplates.api.data;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerData {

    @SerializedName("nameplate")
    private String nameplate;
    @SerializedName("bubble")
    private String bubble;

    public static Builder builder() {
        return new Builder();
    }

    public static PlayerData empty() {
        return new PlayerData.Builder()
                .setNameplate("none")
                .setBubble("none")
                .build();
    }

    public String getNameplate() {
        return nameplate;
    }

    public String getBubble() {
        return bubble;
    }

    public static class Builder {

        private final PlayerData playerData;

        public Builder() {
            this.playerData = new PlayerData();
        }

        @NotNull
        public Builder setNameplate(@Nullable String nameplate) {
            this.playerData.nameplate = nameplate;
            return this;
        }

        @NotNull
        public Builder setBubble(@Nullable String bubble) {
            this.playerData.bubble = bubble;
            return this;
        }

        @NotNull
        public PlayerData build() {
            return this.playerData;
        }
    }
}
