package net.momirealms.customnameplates.nameplates.mode.listener;

import net.momirealms.customnameplates.nameplates.mode.PacketsHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public record BukkitListener(
        PacketsHandler handler) implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        handler.onJoin(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        handler.onQuit(event.getPlayer());
    }

//    @EventHandler
//    public void onMove(PlayerMoveEvent event) {
//        handler.onPlayerMove(event.getPlayer());
//    }
}
