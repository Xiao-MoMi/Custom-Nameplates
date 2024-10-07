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

package net.momirealms.customnameplates.api.feature;

import net.momirealms.customnameplates.api.MainTask;

/**
 * Represents a data structure that stores an object and tracks when it was last updated,
 * using packed ticks and a boolean flag to indicate changes.
 *
 * @param <D> the type of data being stored
 */
public class TimeStampData<D> {

    private D data;
    private int packedTicks;

    /**
     * Constructs a new TickStampData instance.
     *
     * @param data    the data to store
     * @param ticks   the number of ticks to store
     * @param changed whether the value has changed
     */
    public TimeStampData(D data, int ticks, boolean changed) {
        this.data = data;
        this.packedTicks = packBooleanAndTicks(changed, ticks);
    }

    /**
     * Updates the stored data.
     *
     * @param data the new data
     */
    public void data(D data) {
        this.data = data;
    }

    /**
     * Updates the packed ticks and change status.
     *
     * @param changed whether the value has changed
     */
    public void updateTicks(boolean changed) {
        this.packedTicks = packBooleanAndTicks(changed, MainTask.getTicks());
    }

    /**
     * Consume the flag
     */
    public void resetChangedFlag() {
        this.packedTicks = packedTicks & 0x7FFFFFFF;;
    }

    /**
     * Returns the stored data.
     *
     * @return the data
     */
    public D data() {
        return data;
    }

    /**
     * Checks if the value has changed since the last update.
     *
     * @return true if the value has changed, false otherwise
     */
    public boolean hasValueChanged() {
        return unpackBoolean(packedTicks);
    }

    /**
     * Returns the stored number of ticks.
     *
     * @return the number of ticks
     */
    public int ticks() {
        return unpackTicks(packedTicks);
    }

    /**
     * Packs the boolean change flag and ticks into a single integer.
     *
     * @param hasValueChanged whether the value has changed
     * @param ticks           the number of ticks
     * @return the packed integer
     */
    public static int packBooleanAndTicks(boolean hasValueChanged, int ticks) {
        int numberMasked = ticks & 0x7FFFFFFF; // Mask for 31 bits
        int booleanBit = hasValueChanged ? 1 << 31 : 0; // Set the highest bit for the boolean flag
        return booleanBit | numberMasked;
    }

    /**
     * Unpacks the number of ticks from the packed integer.
     *
     * @param packed the packed integer
     * @return the number of ticks
     */
    public static int unpackTicks(int packed) {
        return packed & 0x7FFFFFFF; // Extract the lower 31 bits for ticks
    }

    /**
     * Unpacks the boolean change flag from the packed integer.
     *
     * @param packed the packed integer
     * @return true if the value has changed, false otherwise
     */
    public static boolean unpackBoolean(int packed) {
        return (packed & 0x80000000) != 0; // Check the highest bit for the boolean flag
    }
}