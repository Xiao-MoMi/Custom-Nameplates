package net.momirealms.customnameplates.bossbar;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public record SimpleListener(BossBarManager function) implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        function.onJoin(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        function.onQuit(event.getPlayer());
    }
}
