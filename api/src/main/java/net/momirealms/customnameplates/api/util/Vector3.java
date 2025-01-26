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

import net.momirealms.customnameplates.api.CustomNameplates;

/**
 * A simple record representing a 3D vector with `x`, `y`, and `z` coordinates.
 * Provides utility methods for vector arithmetic such as addition and scaling.
 */
public record Vector3(double x, double y, double z) {

    /**
     * A constant representing a zero vector (0, 0, 0).
     */
    public static final Vector3 ZERO = new Vector3(0, 0, 0);

    /**
     * Converts the vector into a platform-specific 3D vector representation.
     *
     * @return A platform-specific 3D vector object.
     */
    public Object toVec3() {
        return CustomNameplates.getInstance().getPlatform().vec3(x, y, z);
    }

    /**
     * Adds another vector to this vector and returns a new resulting vector.
     *
     * @param another The vector to be added to this vector.
     * @return A new `Vector3` representing the sum of this vector and the given vector.
     */
    public Vector3 add(Vector3 another) {
        return new Vector3(this.x + another.x, this.y + another.y, this.z + another.z);
    }

    /**
     * Adds the given `x`, `y`, and `z` values to the respective components of this vector
     * and returns a new resulting vector.
     *
     * @param x The value to add to the `x` component.
     * @param y The value to add to the `y` component.
     * @param z The value to add to the `z` component.
     * @return A new `Vector3` representing the sum of this vector and the given values.
     */
    public Vector3 add(double x, double y, double z) {
        return new Vector3(this.x + x, this.y + y, this.z + z);
    }

    /**
     * Scales this vector by a given factor and returns a new resulting vector.
     *
     * @param value The factor to multiply each component of the vector by.
     * @return A new `Vector3` representing the scaled vector.
     */
    public Vector3 multiply(double value) {
        return new Vector3(this.x * value, this.y * value, this.z * value);
    }
}
