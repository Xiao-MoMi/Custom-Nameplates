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

package net.momirealms.customnameplates.api.requirement;

import net.momirealms.customnameplates.api.CNPlayer;

/**
 * Represents a requirement that must be satisfied between two players. Requirements can have different types and refresh intervals.
 */
public interface Requirement {

    /**
     * Checks if the requirement is satisfied between two players.
     *
     * @param p1 the first player
     * @param p2 the second player
     * @return true if the requirement is satisfied, false otherwise
     */
    boolean isSatisfied(CNPlayer p1, CNPlayer p2);

    /**
     * Returns the type of the requirement.
     *
     * @return the type as a string
     */
    String type();

    /**
     * Returns the int id for faster lookup
     *
     * @return count id
     */
    int countId();

    /**
     * Returns the refresh interval of the requirement.
     *
     * @return the refresh interval in ticks
     */
    int refreshInterval();

    /**
     * Returns the hash code for the requirement, ensuring uniqueness.
     *
     * @return the hash code
     */
    @Override
    int hashCode();

    /**
     * Returns an empty requirement that is always satisfied.
     *
     * @return an empty requirement
     */
    static Requirement empty() {
        return EmptyRequirement.instance();
    }
}
