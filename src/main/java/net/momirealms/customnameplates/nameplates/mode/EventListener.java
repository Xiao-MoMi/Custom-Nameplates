package net.momirealms.customnameplates.nameplates.mode;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public record EventListener(NameplateManager nameplateManager) implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        nameplateManager.onJoin(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        nameplateManager.onQuit(event.getPlayer());
    }

//    @EventHandler
//    public void onAccept(PlayerResourcePackStatusEvent event) {
//        nameplateManager.onRP(event.getPlayer(), event.getStatus());
//    }
}