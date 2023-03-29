package net.momirealms.customnameplates.object.bossbar;

import net.momirealms.customnameplates.CustomNameplates;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class BossBarTask {

    private BukkitTask timerTask;
    private final BossBarSender[] bossBarSenders;
    private final Player player;

    public BossBarTask(Player player, BossBarConfig[] configs) {
        this.bossBarSenders = new BossBarSender[configs.length];
        this.player = player;
        for (int i = 0; i < configs.length; i++) {
            BossBarConfig bossBarConfig = configs[i];
            this.bossBarSenders[i] = new BossBarSender(
                    bossBarConfig.getInterval(),
                    bossBarConfig.getTexts(),
                    bossBarConfig.getConditions(),
                    bossBarConfig.getOverlay(),
                    bossBarConfig.getColor(),
                    player
            );
        }
    }

    public void stop() {
        if (this.timerTask != null && !this.timerTask.isCancelled()) this.timerTask.cancel();
        for (BossBarSender bossBarSender : bossBarSenders) {
            bossBarSender.hide();
        }
    }

    public void start() {
        this.timerTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CustomNameplates.getInstance(), () -> {
            for (BossBarSender bossBarSender : bossBarSenders) {
                if (!bossBarSender.canSend()) {
                    if (bossBarSender.isShown()) {
                        bossBarSender.hide();
                        return;
                    }
                }
                else {
                    if (bossBarSender.isShown()) {
                        bossBarSender.update();
                    }
                    else {
                        bossBarSender.show();
                    }
                }
            }
        }, 1, 1);
    }
}
