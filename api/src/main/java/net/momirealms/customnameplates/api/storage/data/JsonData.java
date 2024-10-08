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

package net.momirealms.customnameplates.api.storage.data;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

/**
 * Represents the JSON data format for storing a player's nameplate and bubble information.
 */
public class JsonData {

    @SerializedName("nameplate")
    private String nameplate;

    @SerializedName("bubble")
    private String bubble;

    @SerializedName("flags")
    private int flags;

    /**
     * Constructs a new {@link JsonData} instance.
     *
     * @param nameplate the nameplate value
     * @param bubble    the bubble value
     */
    public JsonData(String nameplate, String bubble) {
        this.nameplate = nameplate;
        this.bubble = bubble;
        this.flags = 0;
    }

    public JsonData(String nameplate, String bubble, int flags) {
        this.nameplate = nameplate;
        this.bubble = bubble;
        this.flags = flags;
    }

    public JsonData(String nameplate, String bubble, boolean previewState) {
        this.nameplate = nameplate;
        this.bubble = bubble;
        this.flags = encodeFlags(previewState);
    }

    public static int encodeFlags(boolean previewState) {
        return previewState ? 1 : 0;
    }

    public static boolean decodePreviewState(int flags) {
        return (flags & 1) == 1;
    }

    /**
     * Returns the nameplate stored in this JSON data.
     *
     * @return the nameplate
     */
    public String nameplate() {
        return nameplate;
    }

    /**
     * Returns the bubble stored in this JSON data.
     *
     * @return the bubble
     */
    public String bubble() {
        return bubble;
    }

    /**
     * Returns the flags
     *
     * @return flags
     */
    public int getFlags() {
        return flags;
    }

    /**
     * Converts this JSON data back into a {@link PlayerData} instance.
     *
     * @param uuid the UUID of the player
     * @return a {@link PlayerData} instance based on the JSON data
     */
    public PlayerData toPlayerData(UUID uuid) {
        return PlayerData.builder()
                .uuid(uuid)
                .nameplate(nameplate)
                .bubble(bubble)
                .previewTags(decodePreviewState(flags))
                .build();
    }
}

