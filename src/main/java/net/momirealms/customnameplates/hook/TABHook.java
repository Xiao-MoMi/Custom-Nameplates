package net.momirealms.customnameplates.hook;

import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.shared.TAB;

public class TABHook {

    public static String getTABTeam(String playerName){
        TAB tab = TAB.getInstance();
        TabPlayer tabPlayer = tab.getPlayer(playerName);
        String name = tabPlayer.getTeamName();

        if (name == null) {
            //System.out.println("TAB没有" + playerName);
            return playerName;
        }
        //System.out.println("太好啦，有team" + name);
        return name;
    }
}
