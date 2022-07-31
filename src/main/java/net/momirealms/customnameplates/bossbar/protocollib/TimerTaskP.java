package net.momirealms.customnameplates.bossbar.protocollib;

import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class TimerTaskP {

    private HashMap<Integer, BossBarSenderP> bossbarCache = new HashMap<>();

    public TimerTaskP(Player player){
        ConfigManager.bossbarsP.forEach((key, bossbarConfig) -> {
            BossBarSenderP bossbar = new BossBarSenderP(player, bossbarConfig);
            bossbar.showBossbar();
            BukkitTask task = bossbar.runTaskTimerAsynchronously(CustomNameplates.instance, 1,1);
            bossbarCache.put(task.getTaskId(), bossbar);

        });
    }

    public void stopTimer(){
        bossbarCache.forEach((key,value)-> {
            Bukkit.getScheduler().cancelTask(key);
        });
        bossbarCache = null;
    }
}
