package net.momirealms.customnameplates.objects.team;

import org.bukkit.entity.Player;

public class SimpleTeam implements TeamNameInterface {

    @Override
    public String getTeamName(Player player) {
        return player.getName();
    }

    @Override
    public void unload() {

    }
}
