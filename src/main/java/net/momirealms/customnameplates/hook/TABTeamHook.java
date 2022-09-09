package net.momirealms.customnameplates.hook;

import me.neznamy.tab.shared.TAB;

public class TABTeamHook {

    public static String getTABTeam(String playerName){
        String teamName = TAB.getInstance().getPlayer(playerName).getTeamName();
        return teamName == null ? playerName : teamName;
    }
}