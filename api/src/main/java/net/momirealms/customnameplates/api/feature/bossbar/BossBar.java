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

import java.util.UUID;

/**
 * Represents a BossBar
 */
public interface BossBar {

    /**
     * Returns the unique UUID of the BossBar.
     *
     * @return the UUID of the BossBar
     */
    UUID uuid();

    /**
     * Returns the progress value of the BossBar.
     * The value should be between 0.0 (empty) and 1.0 (full).
     *
     * @return the progress value of the BossBar
     */
    float progress();

    /**
     * Returns the color of the BossBar.
     *
     * @return the color of the BossBar
     */
    Color color();

    /**
     * Returns the overlay style of the BossBar, defining how the bar is divided.
     *
     * @return the overlay style of the BossBar
     */
    Overlay overlay();

    /**
     * Enum representing the different overlay styles for the BossBar.
     * The overlay style determines how the bar is divided, either as a simple progress bar or as notches.
     */
    enum Overlay {
        /**
         * The bar is displayed as a simple progress bar.
         */
        PROGRESS,

        /**
         * The bar is divided into 6 notches.
         */
        NOTCHED_6,

        /**
         * The bar is divided into 10 notches.
         */
        NOTCHED_10,

        /**
         * The bar is divided into 12 notches.
         */
        NOTCHED_12,

        /**
         * The bar is divided into 20 notches.
         */
        NOTCHED_20
    }

    /**
     * Enum representing the different colors for the BossBar.
     */
    enum Color {
        /**
         * Blue color for the BossBar.
         */
        BLUE,

        /**
         * Green color for the BossBar.
         */
        GREEN,

        /**
         * Pink color for the BossBar.
         */
        PINK,

        /**
         * Purple color for the BossBar.
         */
        PURPLE,

        /**
         * Red color for the BossBar.
         */
        RED,

        /**
         * White color for the BossBar.
         */
        WHITE,

        /**
         * Yellow color for the BossBar.
         */
        YELLOW
    }
}
