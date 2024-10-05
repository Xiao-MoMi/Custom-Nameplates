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

public class TickStampData<D> {

    private D data;
    private int packedTicks;

    public TickStampData(D data, int ticks, boolean changed) {
        this.data = data;
        this.packedTicks = packBooleanAndTicks(changed, ticks);
    }

    public void data(D data) {
        this.data = data;
    }

    public void updateTicks(boolean changed) {
        this.packedTicks = packBooleanAndTicks(changed, MainTask.getTicks());
    }

    public D data() {
        return data;
    }

    public boolean hasValueChanged() {
        return unpackBoolean(packedTicks);
    }

    public int ticks() {
        return unpackTicks(packedTicks);
    }

    public static int packBooleanAndTicks(boolean hasValueChanged, int ticks) {
        int numberMasked = ticks & 0x7FFFFFFF;
        int booleanBit = hasValueChanged ? 1 << 31 : 0;
        return booleanBit | numberMasked;
    }

    public static int unpackTicks(int packed) {
        return packed & 0x7FFFFFFF;
    }

    public static boolean unpackBoolean(int packed) {
        return (packed & 0x80000000) != 0;
    }
}
