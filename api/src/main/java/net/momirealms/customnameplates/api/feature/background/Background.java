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

package net.momirealms.customnameplates.api.feature.background;

import net.momirealms.customnameplates.api.feature.AdaptiveImage;
import net.momirealms.customnameplates.api.feature.ConfiguredCharacter;

/**
 * Represents a background configuration
 */
public interface Background extends AdaptiveImage {

    /**
     * Returns the unique ID of the background configuration.
     *
     * @return the background ID
     */
    String id();

    /**
     * Returns the configured character for the left side of the background.
     *
     * @return the left-side configured character
     */
    ConfiguredCharacter left();

    /**
     * Returns the configured character for width 1.
     *
     * @return the configured character for width 1
     */
    ConfiguredCharacter width_1();

    /**
     * Returns the configured character for width 2.
     *
     * @return the configured character for width 2
     */
    ConfiguredCharacter width_2();

    /**
     * Returns the configured character for width 4.
     *
     * @return the configured character for width 4
     */
    ConfiguredCharacter width_4();

    /**
     * Returns the configured character for width 8.
     *
     * @return the configured character for width 8
     */
    ConfiguredCharacter width_8();

    /**
     * Returns the configured character for width 16.
     *
     * @return the configured character for width 16
     */
    ConfiguredCharacter width_16();

    /**
     * Returns the configured character for width 32.
     *
     * @return the configured character for width 32
     */
    ConfiguredCharacter width_32();

    /**
     * Returns the configured character for width 64.
     *
     * @return the configured character for width 64
     */
    ConfiguredCharacter width_64();

    /**
     * Returns the configured character for width 128.
     *
     * @return the configured character for width 128
     */
    ConfiguredCharacter width_128();

    /**
     * Returns the configured character for the right side of the background.
     *
     * @return the right-side configured character
     */
    ConfiguredCharacter right();

    /**
     * Creates a new builder for constructing a Background configuration.
     *
     * @return a new builder instance
     */
    static Builder builder() {
        return new BackgroundImpl.BuilderImpl();
    }

    /**
     * Builder for constructing Background configurations.
     */
    interface Builder {

        /**
         * Sets the unique ID for the background.
         *
         * @param id the background ID
         * @return the builder instance
         */
        Builder id(String id);

        /**
         * Sets the configured character for the left side of the background.
         *
         * @param character the configured character for the left side
         * @return the builder instance
         */
        Builder left(ConfiguredCharacter character);

        /**
         * Sets the configured character for width 1.
         *
         * @param character the configured character for width 1
         * @return the builder instance
         */
        Builder width_1(ConfiguredCharacter character);

        /**
         * Sets the configured character for width 2.
         *
         * @param character the configured character for width 2
         * @return the builder instance
         */
        Builder width_2(ConfiguredCharacter character);

        /**
         * Sets the configured character for width 4.
         *
         * @param character the configured character for width 4
         * @return the builder instance
         */
        Builder width_4(ConfiguredCharacter character);

        /**
         * Sets the configured character for width 8.
         *
         * @param character the configured character for width 8
         * @return the builder instance
         */
        Builder width_8(ConfiguredCharacter character);

        /**
         * Sets the configured character for width 16.
         *
         * @param character the configured character for width 16
         * @return the builder instance
         */
        Builder width_16(ConfiguredCharacter character);

        /**
         * Sets the configured character for width 32.
         *
         * @param character the configured character for width 32
         * @return the builder instance
         */
        Builder width_32(ConfiguredCharacter character);

        /**
         * Sets the configured character for width 64.
         *
         * @param character the configured character for width 64
         * @return the builder instance
         */
        Builder width_64(ConfiguredCharacter character);

        /**
         * Sets the configured character for width 128.
         *
         * @param character the configured character for width 128
         * @return the builder instance
         */
        Builder width_128(ConfiguredCharacter character);

        /**
         * Sets the configured character for the right side of the background.
         *
         * @param character the configured character for the right side
         * @return the builder instance
         */
        Builder right(ConfiguredCharacter character);

        /**
         * Builds and returns the configured Background instance.
         *
         * @return the constructed Background
         */
        Background build();
    }
}
