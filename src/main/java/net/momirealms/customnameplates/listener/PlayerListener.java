package net.momirealms.customnameplates.listener;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.data.DataManager;
import net.momirealms.customnameplates.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public record PlayerListener(CustomNameplates plugin) implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.plugin.getDataManager().loadData(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.plugin.getDataManager().unloadPlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onAccept(PlayerResourcePackStatusEvent event) {
        PlayerData playerData = DataManager.cache.get(event.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTaskLaterAsynchronously(CustomNameplates.instance, ()-> {
            if (playerData == null) {
                return;
            }
            if (event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
                playerData.setAccepted(1);
                Bukkit.getOnlinePlayers().forEach(player -> this.plugin.getScoreBoardManager().getTeam(player.getName()).updateNameplates());
            } else if(event.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED || event.getStatus() == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
                playerData.setAccepted(0);
                Bukkit.getOnlinePlayers().forEach(player -> this.plugin.getScoreBoardManager().getTeam(player.getName()).updateNameplates());
            }
        }, 20);
    }
}
