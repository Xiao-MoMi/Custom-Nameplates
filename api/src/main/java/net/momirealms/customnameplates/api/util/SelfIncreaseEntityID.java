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

import java.util.concurrent.ThreadLocalRandom;

/**
 * A utility class for generating self-incrementing entity IDs.
 * The class provides a thread-safe method for generating unique IDs that increase with each call.
 * The IDs are randomly initialized within a specific range to ensure non-repetition across instances.
 */
public class SelfIncreaseEntityID {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private SelfIncreaseEntityID() {}

    /**
     * The current entity ID, initialized with a random value in a specific range.
     * The value is incremented each time {@link #getAndIncrease()} is called.
     */
    private static int id = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE / 4, Integer.MAX_VALUE / 3);

    /**
     * Retrieves the current entity ID and increments it for the next call.
     * This method ensures that each ID is unique and increases incrementally.
     *
     * @return the current entity ID before it is incremented
     */
    public static int getAndIncrease() {
        int i = id;
        id++;
        return i;
    }
}