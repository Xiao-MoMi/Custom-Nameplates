package net.momirealms.customnameplates.nameplates.bubbles;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public record ChatListener(
        BBPacketsHandle bbPacketsHandle) implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        bbPacketsHandle.onChat(event.getPlayer(), event.getMessage());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        bbPacketsHandle.onJoin(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        bbPacketsHandle.onQuit(event.getPlayer());
    }
}
