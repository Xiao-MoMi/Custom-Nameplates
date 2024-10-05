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

    enum Overlay {
        PROGRESS,
        NOTCHED_6,
        NOTCHED_10,
        NOTCHED_12,
        NOTCHED_20
    }

    enum Color {
        BLUE,
        GREEN,
        PINK,
        PURPLE,
        RED,
        WHITE,
        YELLOW
    }
}
