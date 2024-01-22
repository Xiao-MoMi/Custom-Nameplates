/*
 *  Copyright (C) <2022> <XiaoMoMi>
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
