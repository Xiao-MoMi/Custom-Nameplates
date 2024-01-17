package net.momirealms.customnameplates.paper.mechanic.team.provider;

import org.bukkit.entity.Player;

public class DefaultProvider implements TeamProvider {

    @Override
    public String getTeam(Player player) {
        return player.getName();
    }
}
