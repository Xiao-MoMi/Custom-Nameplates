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

package net.momirealms.customnameplates.api.feature.image;

import net.momirealms.customnameplates.api.feature.ConfiguredCharacter;
import org.jetbrains.annotations.Nullable;

/**
 * An interface represents the image
 */
public interface Image {

    /**
     * Returns the unique ID of the image configuration.
     *
     * @return the image ID
     */
    String id();

    /**
     * Checks if the image has a shadow effect.
     *
     * @return true if the image has a shadow, false otherwise
     */
    boolean removeShadow();

    /**
     * Get the animation of the image
     *
     * @return the animated image
     */
    @Nullable
    Animation animation();

    /**
     * Returns the configured character associated with this image.
     *
     * @return the configured character
     */
    ConfiguredCharacter character();

    /**
     * Creates a new builder for constructing an Image configuration.
     *
     * @return a new builder instance
     */
    static Builder builder() {
        return new ImageImpl.BuilderImpl();
    }

    /**
     * Builder for constructing Image configurations.
     */
    interface Builder {

        /**
         * Sets the unique ID for the image configuration.
         *
         * @param id the image ID
         * @return the builder instance
         */
        Builder id(String id);

        /**
         * Sets whether to remove the shadow of the image
         *
         * @param remove true if to remove the shadow
         * @return the builder instance
         */
        Builder removeShadow(boolean remove);

        /**
         * Sets the animation of the image
         *
         * @param animation animation
         * @return the builder instance
         */
        Builder animation(@Nullable Animation animation);

        /**
         * Sets the configured character associated with this image.
         *
         * @param character the configured character
         * @return the builder instance
         */
        Builder character(ConfiguredCharacter character);

        /**
         * Builds and returns the configured Image instance.
         *
         * @return the constructed Image
         */
        Image build();
    }
}
