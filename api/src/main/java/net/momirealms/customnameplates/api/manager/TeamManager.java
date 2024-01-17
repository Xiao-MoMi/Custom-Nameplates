package net.momirealms.customnameplates.api.manager;

import net.kyori.adventure.text.Component;
import net.momirealms.customnameplates.api.mechanic.team.TeamColor;
import net.momirealms.customnameplates.api.mechanic.team.TeamTagVisibility;
import org.bukkit.entity.Player;

public interface TeamManager {

    /**
     * Create team for a player
     *
     * @param player player
     */
    void createTeam(Player player);

    /**
     * Create a team for a player on proxy
     *
     * @param player player
     */
    void createProxyTeam(Player player);

    /**
     * Remove a team for a player
     *
     * @param player player
     */
    void removeTeam(Player player);

    /**
     * Remove a team for a player on proxy
     *
     * @param player player
     */
    void removeProxyTeam(Player player);

    void updateTeam(Player owner, Player viewer, Component prefix, Component suffix, TeamColor color, TeamTagVisibility visibility);

    /**
     * Get the team player in
     *
     * @param player player
     * @return team name
     */
    String getTeamName(Player player);
}
