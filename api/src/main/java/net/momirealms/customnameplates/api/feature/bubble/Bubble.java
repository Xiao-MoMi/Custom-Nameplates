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

package net.momirealms.customnameplates.api.feature.bubble;

import net.momirealms.customnameplates.api.feature.AdaptiveImage;
import net.momirealms.customnameplates.api.feature.ConfiguredCharacter;

/**
 * Bubble
 */
public interface Bubble extends AdaptiveImage {

    /**
     * Returns the unique ID of the Bubble configuration.
     *
     * @return the Bubble ID
     */
    String id();

    /**
     * Returns the configured character for the left side of the Bubble.
     *
     * @return the left-side configured character
     */
    ConfiguredCharacter left();

    /**
     * Returns the configured character for the right side of the Bubble.
     *
     * @return the right-side configured character
     */
    ConfiguredCharacter right();

    /**
     * Returns the configured character for the middle section of the Bubble.
     *
     * @return the middle configured character
     */
    ConfiguredCharacter middle();

    /**
     * Returns the configured character for the tail section of the Bubble.
     *
     * @return the tail configured character
     */
    ConfiguredCharacter tail();

    /**
     * Creates a new builder for constructing a Bubble configuration.
     *
     * @return a new builder instance
     */
    static Builder builder() {
        return new BubbleImpl.BuilderImpl();
    }

    /**
     * Builder for constructing Bubble configurations.
     */
    interface Builder {

        /**
         * Sets the unique ID for the Bubble configuration.
         *
         * @param id the Bubble ID
         * @return the builder instance
         */
        Builder id(String id);

        /**
         * Sets the configured character for the left side of the Bubble.
         *
         * @param left the left-side configured character
         * @return the builder instance
         */
        Builder left(ConfiguredCharacter left);

        /**
         * Sets the configured character for the right side of the Bubble.
         *
         * @param right the right-side configured character
         * @return the builder instance
         */
        Builder right(ConfiguredCharacter right);

        /**
         * Sets the configured character for the middle section of the Bubble.
         *
         * @param middle the middle configured character
         * @return the builder instance
         */
        Builder middle(ConfiguredCharacter middle);

        /**
         * Sets the configured character for the tail section of the Bubble.
         *
         * @param tail the tail configured character
         * @return the builder instance
         */
        Builder tail(ConfiguredCharacter tail);

        /**
         * Builds and returns the constructed Bubble instance.
         *
         * @return the constructed Bubble
         */
        Bubble build();
    }
}
