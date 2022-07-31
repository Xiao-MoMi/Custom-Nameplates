package net.momirealms.customnameplates.hook;

import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.shared.TAB;

public class TABHook {

    public static String getTABTeam(String playerName){
        TAB tab = TAB.getInstance();
        TabPlayer tabPlayer = tab.getPlayer(playerName);
        String name = tabPlayer.getTeamName();
        if (name == null) {
            return playerName;
        }
        return name;
    }
}
