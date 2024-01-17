package net.momirealms.customnameplates.paper.mechanic.team.provider;

import com.Zrips.CMI.CMI;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class CMIProvider implements TeamProvider {

    @Override
    public String getTeam(Player player) {
        Team team = CMI.getInstance().getSB().getPlayerTeam(player);
        if (team == null) return player.getName();
        return team.getName();
    }
}
