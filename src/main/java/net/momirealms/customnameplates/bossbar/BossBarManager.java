package net.momirealms.customnameplates.bossbar;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.Function;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.HashMap;

public class BossBarManager extends Function {

    private final SimpleListener simpleListener;

    private final HashMap<Player, TimerTaskP> taskCache = new HashMap<>();

    public BossBarManager(String name) {
        super(name);
        this.simpleListener = new SimpleListener(this);
    }

    @Override
    public void load() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            taskCache.put(player, new TimerTaskP(player));
        }
        Bukkit.getPluginManager().registerEvents(simpleListener, CustomNameplates.instance);
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(simpleListener);
        for (TimerTaskP timerTask : taskCache.values()) {
            timerTask.stopTimer();
        }
        taskCache.clear();
    }

    public void onJoin(Player player) {
        taskCache.put(player, new TimerTaskP(player));
    }

    public void onQuit(Player player) {
        TimerTaskP timerTask = taskCache.remove(player);
        if (timerTask != null){
            timerTask.stopTimer();
        }
    }
}
