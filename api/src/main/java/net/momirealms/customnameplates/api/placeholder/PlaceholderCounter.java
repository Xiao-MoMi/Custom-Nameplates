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

package net.momirealms.customnameplates.api.placeholder;

/**
 * Utility class for managing a global counter for placeholder IDs.
 */
public class PlaceholderCounter {

    private static int id = 1;

    /**
     * Returns the current ID and increments the counter by 1.
     *
     * @return the current ID
     */
    public static int getAndIncrease() {
        int i = id;
        id++;
        return i;
    }

    /**
     * Resets the ID counter back to 1.
     */
    public static void reset() {
        id = 1;
    }
}
