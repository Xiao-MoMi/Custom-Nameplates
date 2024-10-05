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

/**
 * Represents a feature that involves relationships between two players and supports placeholder updates for both players.
 */
public interface RelationalFeature extends Feature {

    /**
     * Notifies the feature that placeholders for the relationship between two players have been updated.
     *
     * @param p1    the first player
     * @param p2    the second player
     * @param force if true, forces an update even if no changes are detected
     */
    void notifyPlaceholderUpdates(CNPlayer p1, CNPlayer p2, boolean force);
}
