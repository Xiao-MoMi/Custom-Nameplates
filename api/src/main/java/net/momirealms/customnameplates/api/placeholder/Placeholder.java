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

package net.momirealms.customnameplates.api.placeholder;

import java.util.Set;

/**
 * Placeholder
 */
public interface Placeholder {

    /**
     * Adds a child placeholder to this placeholder.
     *
     * @param placeholder the child placeholder to add
     */
    void addChild(Placeholder placeholder);

    /**
     * Adds multiple child placeholders to this placeholder.
     *
     * @param placeholders the set of child placeholders to add
     */
    void addChildren(Set<Placeholder> placeholders);

    /**
     * Adds a parent placeholder to this placeholder.
     *
     * @param placeholder the parent placeholder to add
     */
    void addParent(Placeholder placeholder);

    /**
     * Adds multiple parent placeholders to this placeholder.
     *
     * @param placeholders the set of parent placeholders to add
     */
    void addParents(Set<Placeholder> placeholders);

    /**
     * Returns the set of child placeholders associated with this placeholder.
     *
     * @return the set of child placeholders
     */
    Set<Placeholder> children();

    /**
     * Returns the set of parent placeholders associated with this placeholder.
     *
     * @return the set of parent placeholders
     */
    Set<Placeholder> parents();

    /**
     * Returns the refresh interval of this placeholder.
     *
     * @return the refresh interval, or -1 if no refresh is required
     */
    int refreshInterval();

    /**
     * Returns the unique ID of this placeholder.
     *
     * @return the placeholder ID
     */
    String id();

    /**
     * Returns the unique count ID for this placeholder, used for identification purposes.
     *
     * @return the count ID
     */
    int countId();
}