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

package net.momirealms.customnameplates.api.feature.bossbar;

import net.momirealms.customnameplates.api.feature.CarouselText;
import net.momirealms.customnameplates.api.requirement.Requirement;

/**
 * Interface for BossBarConfig
 */
public interface BossBarConfig {

    /**
     * The default overlay style for the BossBar.
     */
    BossBar.Overlay DEFAULT_OVERLAY = BossBar.Overlay.PROGRESS;

    /**
     * The default color for the BossBar.
     */
    BossBar.Color DEFAULT_COLOR = BossBar.Color.YELLOW;

    /**
     * Returns the unique ID for the BossBar configuration.
     *
     * @return the configuration ID
     */
    String id();

    /**
     * Returns the requirements for the BossBar.
     *
     * @return an array of requirements
     */
    Requirement[] requirements();

    /**
     * Returns the carousel texts used in the BossBar.
     *
     * @return an array of carousel texts
     */
    CarouselText[] carouselTexts();

    /**
     * Returns the overlay style for the BossBar.
     *
     * @return the overlay style
     */
    BossBar.Overlay overlay();

    /**
     * Returns the color of the BossBar.
     *
     * @return the BossBar color
     */
    BossBar.Color color();

    /**
     * Returns the progress value of the BossBar.
     * The value should be between 0.0 (empty) and 1.0 (full).
     *
     * @return the progress value
     */
    float progress();

    /**
     * Creates a new builder for constructing a BossBarConfig.
     *
     * @return a new builder instance
     */
    static BossBarConfig.Builder builder() {
        return new BossBarConfigImpl.BuilderImpl();
    }

    /**
     * Builder for constructing BossBarConfig instances.
     */
    interface Builder {

        /**
         * Sets the unique ID for the BossBar configuration.
         *
         * @param id the configuration ID
         * @return the builder instance
         */
        Builder id(String id);

        /**
         * Sets the requirements for the BossBar.
         *
         * @param requirements an array of requirements
         * @return the builder instance
         */
        Builder requirement(Requirement[] requirements);

        /**
         * Sets the carousel texts for the BossBar.
         *
         * @param carouselTexts an array of carousel texts
         * @return the builder instance
         */
        Builder carouselText(CarouselText[] carouselTexts);

        /**
         * Sets the overlay style for the BossBar.
         *
         * @param overlay the overlay style
         * @return the builder instance
         */
        Builder overlay(BossBar.Overlay overlay);

        /**
         * Sets the color for the BossBar.
         *
         * @param color the BossBar color
         * @return the builder instance
         */
        Builder color(BossBar.Color color);

        /**
         * Sets the progress value for the BossBar.
         *
         * @param progress the progress value
         * @return the builder instance
         */
        Builder progress(float progress);

        /**
         * Builds and returns the configured BossBarConfig instance.
         *
         * @return the constructed BossBarConfig
         */
        BossBarConfig build();
    }
}
