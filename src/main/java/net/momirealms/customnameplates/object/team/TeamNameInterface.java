package net.momirealms.customnameplates.object.team;

import org.bukkit.entity.Player;

public interface TeamNameInterface {

    String getTeamName(Player player);
    void onJoin(Player player);
    void onQuit(Player player);
    void unload();
    void load();
}
