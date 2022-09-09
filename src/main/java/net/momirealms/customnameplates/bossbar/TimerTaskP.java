package net.momirealms.customnameplates.bossbar;

import net.momirealms.customnameplates.ConfigManager;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class TimerTaskP {

    private final HashSet<Sender> bossBarCache = new HashSet<>();

    public TimerTaskP(Player player){
        for (BossBarConfig config : ConfigManager.bossBars.values()) {
            Sender sender = new Sender(player, config);
            bossBarCache.add(sender);
        }
    }

    public void stopTimer(){
        bossBarCache.forEach(Sender::hide);
        bossBarCache.clear();
    }
}
