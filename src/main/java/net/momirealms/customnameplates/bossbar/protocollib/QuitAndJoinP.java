package net.momirealms.customnameplates.bossbar.protocollib;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class QuitAndJoinP implements Listener {

    public static HashMap<Player, TimerTaskP> cache = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        TimerTaskP timerTask = new TimerTaskP(player);
        cache.put(player, timerTask);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        TimerTaskP timerTask = cache.get(player);
        if (timerTask != null){
            timerTask.stopTimer();
        }
        cache.remove(player);
    }
}
