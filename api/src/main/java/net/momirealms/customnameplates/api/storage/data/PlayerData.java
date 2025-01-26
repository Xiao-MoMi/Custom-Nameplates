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

import java.util.UUID;

/**
 * Represents player-specific data, including the player's nameplate, bubble, and UUID.
 */
public interface PlayerData {
    /**
     * Default nameplate
     */
    String DEFAULT_NAMEPLATE = "none";
    /**
     * Default bubble
     */
    String DEFAULT_BUBBLE = "none";

    /**
     * Returns the nameplate associated with the player.
     *
     * @return the player's nameplate
     */
    String nameplate();

    /**
     * Returns the bubble associated with the player.
     *
     * @return the player's bubble
     */
    String bubble();

    /**
     * Returns if the tag should always be shown to the owner
     *
     * @return if the tag should always be shown to the owner
     */
    boolean previewTags();

    /**
     * Returns the UUID of the player.
     *
     * @return the player's UUID
     */
    UUID uuid();

    /**
     * Returns a builder for constructing {@link PlayerData} instances.
     *
     * @return a {@link PlayerData.Builder} instance
     */
    static Builder builder() {
        return new PlayerDataImpl.BuilderImpl();
    }

    /**
     * Returns an empty {@link PlayerData} instance with default values for the given UUID.
     *
     * @param uuid the UUID of the player
     * @return an empty {@link PlayerData} instance
     */
    static PlayerData empty(UUID uuid) {
        return builder()
                .uuid(uuid)
                .nameplate(DEFAULT_NAMEPLATE)
                .bubble(DEFAULT_BUBBLE)
                .build();
    }

    /**
     * Builder interface for constructing {@link PlayerData} instances.
     */
    interface Builder {

        /**
         * Sets the nameplate for the {@link PlayerData}.
         *
         * @param nameplate the nameplate value
         * @return the builder instance
         */
        Builder nameplate(String nameplate);

        /**
         * Sets the bubble for the {@link PlayerData}.
         *
         * @param bubble the bubble value
         * @return the builder instance
         */
        Builder bubble(String bubble);

        /**
         * Sets the UUID for the {@link PlayerData}.
         *
         * @param uuid the UUID value
         * @return the builder instance
         */
        Builder uuid(UUID uuid);

        /**
         * Sets whether to preview state for the {@link PlayerData}.
         *
         * @param previewTags the state
         * @return the builder instance
         */
        Builder previewTags(boolean previewTags);

        /**
         * Builds and returns the {@link PlayerData} instance.
         *
         * @return the constructed {@link PlayerData} instance
         */
        PlayerData build();
    }

    /**
     * Converts the player data into a {@link JsonData} object for serialization.
     *
     * @return a {@link JsonData} object containing the nameplate and bubble data
     */
    default JsonData toJsonData() {
        return new JsonData(nameplate(), bubble(), previewTags());
    }
}
