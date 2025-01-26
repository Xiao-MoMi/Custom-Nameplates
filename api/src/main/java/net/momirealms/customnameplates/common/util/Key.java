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

/**
 * Represents a key consisting of a namespace and a value.
 * This class provides methods for creating and manipulating keys.
 */
public record Key(String namespace, String value) {

    /**
     * Creates a new {@link Key} instance with the specified namespace and value.
     *
     * @param namespace the namespace of the key
     * @param value     the value of the key
     * @return a new {@link Key} instance
     */
    public static Key of(String namespace, String value) {
        return new Key(namespace, value);
    }

    /**
     * Creates a new {@link Key} instance from a string in the format "namespace:value".
     *
     * @param key the string representation of the key
     * @return a new {@link Key} instance
     */
    public static Key fromString(String key) {
        String[] split = key.split(":", 2);
        return of(split[0], split[1]);
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Key key)) return false;
        return this.namespace.equals(key.namespace()) && this.value.equals(key.value());
    }

    @Override
    public String toString() {
        return namespace + ":" + value;
    }
}