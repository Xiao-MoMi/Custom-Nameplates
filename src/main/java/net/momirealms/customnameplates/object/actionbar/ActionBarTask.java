package net.momirealms.customnameplates.object.actionbar;

import net.momirealms.customnameplates.CustomNameplates;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class ActionBarTask {

    private BukkitTask timerTask;
    private final ActionBarSender[] actionBarSenders;

    public ActionBarTask(Player player, ActionBarConfig[] configs) {
        this.actionBarSenders = new ActionBarSender[configs.length];
        for (int i = 0; i < configs.length; i++) {
            ActionBarConfig actionBarConfig = configs[i];
            this.actionBarSenders[i] = new ActionBarSender(
                    actionBarConfig.getInterval(),
                    actionBarConfig.getTexts(),
                    actionBarConfig.getConditions(),
                    player
            );
        }
    }

    public void stop() {
        if (this.timerTask != null && !this.timerTask.isCancelled()) this.timerTask.cancel();
    }

    public void start() {
        this.timerTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CustomNameplates.getInstance(), () -> {
            for (ActionBarSender actionBarSender : actionBarSenders) {
                if (actionBarSender.canSend()) {
                    actionBarSender.send();
                    return;
                }
            }
        }, 1, 1);
    }
}
