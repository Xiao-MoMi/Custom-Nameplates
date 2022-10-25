package net.momirealms.customnameplates.objects.team;

import org.bukkit.entity.Player;

public interface TeamNameInterface {

    String getTeamName(Player player);
    void unload();
}
