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
import net.momirealms.customnameplates.api.CustomNameplates;

/**
 * A requirement that is always satisfied, representing an "empty" or default requirement.
 */
public class EmptyRequirement implements Requirement {
    /**
     * A requirement that is always satisfied, representing an "empty" or default requirement.
     */
    public static final EmptyRequirement INSTANCE = new EmptyRequirement();

    /**
     * Returns the singleton instance of the EmptyRequirement.
     *
     * @return the singleton instance of EmptyRequirement
     */
    public static Requirement instance() {
        return INSTANCE;
    }

    /**
     * Always returns true, as this requirement is always satisfied.
     *
     * @param p1 the first player
     * @param p2 the second player
     * @return true (requirement is always satisfied)
     */
    @Override
    public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
        return true;
    }

    /**
     * Returns the type of this requirement.
     *
     * @return "empty" as the type
     */
    @Override
    public String type() {
        return "empty";
    }

    @Override
    public int countId() {
        return CustomNameplates.getInstance().getRequirementManager().countId(this);
    }

    /**
     * Returns -1, indicating that this requirement does not have a refresh interval.
     *
     * @return -1 (no refresh interval)
     */
    @Override
    public int refreshInterval() {
        return -1;
    }

    /**
     * Returns the hash code based on the requirement type.
     *
     * @return the hash code of the requirement type
     */
    @Override
    public int hashCode() {
        return type().hashCode();
    }
}
