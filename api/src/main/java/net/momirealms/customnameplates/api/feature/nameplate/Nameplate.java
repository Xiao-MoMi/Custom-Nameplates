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

package net.momirealms.customnameplates.api.feature.nameplate;

import net.momirealms.customnameplates.api.feature.AdaptiveImage;
import net.momirealms.customnameplates.api.feature.ConfiguredCharacter;

/**
 * Nameplate
 */
public interface Nameplate extends AdaptiveImage {

    /**
     * Returns the unique ID of the nameplate configuration.
     *
     * @return the nameplate ID
     */
    String id();

    /**
     * Returns the display name of the nameplate.
     *
     * @return the display name of the nameplate
     */
    String displayName();

    /**
     * Returns the configured character for the left side of the nameplate.
     *
     * @return the left-side configured character
     */
    ConfiguredCharacter left();

    /**
     * Returns the configured character for the middle section of the nameplate.
     *
     * @return the middle configured character
     */
    ConfiguredCharacter middle();

    /**
     * Returns the configured character for the right side of the nameplate.
     *
     * @return the right-side configured character
     */
    ConfiguredCharacter right();

    /**
     * Gets the min width of the nameplate
     *
     * @return the min width of the nameplate
     */
    int minWidth();

    /**
     * Creates a new builder for constructing a Nameplate configuration.
     *
     * @return a new builder instance
     */
    static Builder builder() {
        return new NameplateImpl.BuilderImpl();
    }

    /**
     * Builder for constructing Nameplate configurations.
     */
    interface Builder {

        /**
         * Sets the unique ID for the nameplate.
         *
         * @param id the nameplate ID
         * @return the builder instance
         */
        Builder id(String id);

        /**
         * Sets the display name for the nameplate.
         *
         * @param displayName the display name
         * @return the builder instance
         */
        Builder displayName(String displayName);

        /**
         * Sets the min width for the nameplate
         *
         * @param minWidth min width
         * @return the builder instance
         */
        Builder minWidth(int minWidth);

        /**
         * Sets the configured character for the left side of the nameplate.
         *
         * @param left the left-side configured character
         * @return the builder instance
         */
        Builder left(ConfiguredCharacter left);

        /**
         * Sets the configured character for the middle section of the nameplate.
         *
         * @param middle the middle configured character
         * @return the builder instance
         */
        Builder middle(ConfiguredCharacter middle);

        /**
         * Sets the configured character for the right side of the nameplate.
         *
         * @param right the right-side configured character
         * @return the builder instance
         */
        Builder right(ConfiguredCharacter right);

        /**
         * Builds and returns the configured Nameplate instance.
         *
         * @return the constructed Nameplate
         */
        Nameplate build();
    }
}