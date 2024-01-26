/*
 *  Copyright (C) <2022> <XiaoMoMi>
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

package net.momirealms.customnameplates.api.manager;

import net.momirealms.customnameplates.common.team.TeamColor;
import net.momirealms.customnameplates.common.team.TeamTagVisibility;
import org.bukkit.entity.Player;

public interface TeamManager {

    /**
     * Create team for a player
     *
     * @param player player
     */
    void createTeam(Player player);

    /**
     * Remove a team for a player
     *
     * @param player player
     */
    void removeTeam(Player player);

    /**
     * Update a player's team for a viewer
     */
    void updateTeam(Player owner, Player viewer, String prefix, String suffix, TeamColor color, TeamTagVisibility visibility);

    /**
     * Get the team player in
     *
     * @param player player
     * @param viewer
     * @return team name
     */
    String getTeamName(Player player, Player viewer);
}
