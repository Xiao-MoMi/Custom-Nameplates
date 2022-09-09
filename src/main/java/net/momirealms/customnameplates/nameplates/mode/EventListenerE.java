package net.momirealms.customnameplates.nameplates.mode;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public record EventListenerE(EntityTag entityTag) implements Listener {

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        entityTag.onSneak(event.getPlayer(), event.isSneaking());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        entityTag.onRespawn(event.getPlayer());
    }
}