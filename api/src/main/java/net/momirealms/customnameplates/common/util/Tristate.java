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

package net.momirealms.customnameplates.common.util;

import org.jetbrains.annotations.NotNull;

/**
 * Tristate
 */
public enum Tristate {

    /**
     * A value indicating a positive setting
     */
    TRUE(true),

    /**
     * A value indicating a negative (negated) setting
     */
    FALSE(false),

    /**
     * A value indicating a non-existent setting
     */
    UNDEFINED(false);

    /**
     * Returns a {@link Tristate} from a boolean
     *
     * @param val the boolean value
     * @return {@link #TRUE} or {@link #FALSE}, if the value is <code>true</code> or <code>false</code>, respectively.
     */
    public static @NotNull Tristate of(boolean val) {
        return val ? TRUE : FALSE;
    }

    /**
     * Returns a {@link Tristate} from a nullable boolean.
     *
     * <p>Unlike {@link #of(boolean)}, this method returns {@link #UNDEFINED}
     * if the value is null.</p>
     *
     * @param val the boolean value
     * @return {@link #UNDEFINED}, {@link #TRUE} or {@link #FALSE}, if the value
     *         is <code>null</code>, <code>true</code> or <code>false</code>, respectively.
     */
    public static @NotNull Tristate of(Boolean val) {
        return val == null ? UNDEFINED : val ? TRUE : FALSE;
    }

    private final boolean booleanValue;

    Tristate(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public boolean or(boolean val) {
        if (this == UNDEFINED) {
            return val;
        } else {
            return this.booleanValue;
        }
    }

    public static Tristate fromBoolean(Boolean val) {
        if (val == null) {
            return UNDEFINED;
        }
        return val ? TRUE : FALSE;
    }

    /**
     * Returns the value of the Tristate as a boolean.
     *
     * <p>A value of {@link #UNDEFINED} converts to false.</p>
     *
     * @return a boolean representation of the Tristate.
     */
    public boolean asBoolean() {
        return this.booleanValue;
    }
}
