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

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.placeholder.Placeholder;

import java.util.Set;

/**
 * Represents a feature that can utilize placeholders and manage updates based on player interaction.
 */
public interface Feature {

    /**
     * Returns the name of the feature.
     *
     * @return the feature name
     */
    String name();

    /**
     * Returns the set of placeholders that are currently active within this feature.
     *
     * @return a set of active placeholders
     */
    Set<Placeholder> activePlaceholders();

    /**
     * Returns the set of all placeholders that this feature can use.
     *
     * @return a set of all possible placeholders
     */
    Set<Placeholder> allPlaceholders();

    /**
     * Notifies the feature that placeholders for the specified player have been updated.
     *
     * @param p1    the player whose placeholders have been updated
     * @param force if true, forces an update even if no changes are detected
     */
    void notifyPlaceholderUpdates(CNPlayer p1, boolean force);
}