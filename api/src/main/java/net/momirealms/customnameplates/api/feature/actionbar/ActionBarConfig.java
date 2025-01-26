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

package net.momirealms.customnameplates.api.feature.actionbar;

import net.momirealms.customnameplates.api.feature.CarouselText;
import net.momirealms.customnameplates.api.requirement.Requirement;

/**
 * ActionBar Config
 */
public interface ActionBarConfig {

    /**
     * Returns the unique ID of the ActionBar configuration.
     *
     * @return the configuration ID
     */
    String id();

    /**
     * Returns the list of requirements for the ActionBar.
     *
     * @return an array of requirements
     */
    Requirement[] requirements();

    /**
     * Returns the carousel texts used in the ActionBar.
     *
     * @return an array of carousel texts
     */
    CarouselText[] carouselTexts();

    /**
     * Creates a new builder for constructing an ActionBarConfig.
     *
     * @return a new builder instance
     */
    static Builder builder() {
        return new ActionBarConfigImpl.BuilderImpl();
    }

    /**
     * Builder for creating an ActionBarConfig.
     */
    interface Builder {

        /**
         * Sets the unique ID for the ActionBar configuration.
         *
         * @param id the configuration ID
         * @return the builder instance
         */
        Builder id(String id);

        /**
         * Sets the requirements for the ActionBar.
         *
         * @param requirements an array of requirements
         * @return the builder instance
         */
        Builder requirement(Requirement[] requirements);

        /**
         * Sets the carousel texts for the ActionBar.
         *
         * @param carouselTexts an array of carousel texts
         * @return the builder instance
         */
        Builder carouselText(CarouselText[] carouselTexts);

        /**
         * Builds and returns the ActionBarConfig.
         *
         * @return the constructed ActionBarConfig
         */
        ActionBarConfig build();
    }
}
