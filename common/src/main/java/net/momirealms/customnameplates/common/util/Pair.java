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

import java.util.Objects;

/**
 * A generic class representing a pair of values.
 * This class provides methods to create and access pairs of values.
 *
 * @param <L> the type of the left value
 * @param <R> the type of the right value
 */
public record Pair<L, R>(L left, R right) {

    /**
     * Creates a new {@link Pair} with the specified left and right values.
     *
     * @param left  the left value
     * @param right the right value
     * @param <L>   the type of the left value
     * @param <R>   the type of the right value
     * @return a new {@link Pair} with the specified values
     */
    public static <L, R> Pair<L, R> of(final L left, final R right) {
        return new Pair<>(left, right);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) object;
        return Objects.equals(left, pair.left) && Objects.equals(right, pair.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.left, this.right);
    }
}