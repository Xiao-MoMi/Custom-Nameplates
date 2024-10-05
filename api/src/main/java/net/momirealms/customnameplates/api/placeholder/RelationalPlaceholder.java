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

import net.momirealms.customnameplates.api.CNPlayer;

/**
 * Represents a relational placeholder, which provides dynamic content based on the relationship between two players.
 */
public interface RelationalPlaceholder extends Placeholder {

    /**
     * Requests the placeholder content based on the relationship between two players.
     *
     * @param p1 the first player
     * @param p2 the second player
     * @return the placeholder content as a string
     */
    String request(CNPlayer p1, CNPlayer p2);
}
