package net.momirealms.customnameplates.nameplates;

import org.bukkit.entity.Player;

public interface TeamPacketManager {

    void sendUpdateToOne(Player player);
    void sendUpdateToAll(Player player);

}
