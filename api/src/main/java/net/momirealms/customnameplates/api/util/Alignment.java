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

package net.momirealms.customnameplates.api.util;

/**
 * Enum representing text alignment options.
 * <p>
 * This enum provides three types of text alignment: CENTER, LEFT, and RIGHT, each associated
 * with a unique ID for easy identification. These values are typically used to define the
 * positioning of text in layouts or UI components.
 * </p>
 */
public enum Alignment {
    /**
     * Represents center-aligned text.
     */
    CENTER(0),
    /**
     * Represents left-aligned text.
     */
    LEFT(1),
    /**
     * Represents right-aligned text.
     */
    RIGHT(2);

    final int id;

    /**
     * Constructs an {@code Alignment} instance with the specified ID.
     *
     * @param id the unique ID representing this alignment type
     */
    Alignment(int id) {
        this.id = id;
    }

    /**
     * Retrieves the ID associated with this alignment.
     *
     * @return the unique ID of this alignment
     */
    public int getId() {
        return id;
    }
}
