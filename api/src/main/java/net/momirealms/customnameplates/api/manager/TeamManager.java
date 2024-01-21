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
     * @return team name
     */
    String getTeamName(Player player);
}
