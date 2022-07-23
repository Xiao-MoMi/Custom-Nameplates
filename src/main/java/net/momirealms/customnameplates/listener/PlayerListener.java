package net.momirealms.customnameplates.listener;

import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.data.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public record PlayerListener(CustomNameplates plugin) implements Listener {

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        this.plugin.getDataManager().getOrCreate(event.getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.plugin.getScoreBoardManager().getOrCreateTeam(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.plugin.getDataManager().unloadPlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onAccept(PlayerResourcePackStatusEvent event) {
        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
            DataManager.cache.get(event.getPlayer().getUniqueId()).setAccepted(1);
            Bukkit.getOnlinePlayers().forEach(player -> this.plugin.getScoreBoardManager().getTeam(player.getName()).updateNameplates());
        } else {
            DataManager.cache.get(event.getPlayer().getUniqueId()).setAccepted(0);
            Bukkit.getOnlinePlayers().forEach(player -> this.plugin.getScoreBoardManager().getTeam(player.getName()).updateNameplates());
        }
    }
}
