package net.momirealms.customnameplates.object.actionbar;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.object.requirements.PlayerCondition;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class ActionBarTask {

    private BukkitTask timerTask;
    private final ActionBarSender[] actionBarSenders;
    private String otherText;
    private long expireTime;
    private final Player player;

    public ActionBarTask(Player player, ActionBarConfig[] configs) {
        this.player = player;
        this.actionBarSenders = new ActionBarSender[configs.length];
        this.otherText = "";
        for (int i = 0; i < configs.length; i++) {
            ActionBarConfig actionBarConfig = configs[i];
            this.actionBarSenders[i] = new ActionBarSender(
                    actionBarConfig.getInterval(),
                    actionBarConfig.getTexts(),
                    actionBarConfig.getConditions(),
                    player,
                     this
            );
        }
    }

    public void stop() {
        if (this.timerTask != null && !this.timerTask.isCancelled()) this.timerTask.cancel();
    }

    public void start() {
        this.timerTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CustomNameplates.getInstance(), () -> {
            if (System.currentTimeMillis() > getExpireTime()) {
                this.otherText = "";
            }
            PlayerCondition playerCondition = new PlayerCondition(player);
            for (ActionBarSender actionBarSender : actionBarSenders) {
                if (actionBarSender.canSend(playerCondition)) {
                    actionBarSender.send();
                    return;
                }
            }
        }, 1, 1);
    }

    public String getOtherText() {
        return otherText;
    }

    public void setOtherText(String otherText, long time) {
        this.otherText = otherText;
        this.expireTime = time + 4000;
    }

    public long getExpireTime() {
        return expireTime;
    }
}
