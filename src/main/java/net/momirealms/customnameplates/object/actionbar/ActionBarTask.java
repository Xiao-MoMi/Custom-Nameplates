/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customnameplates.object.actionbar;

import net.momirealms.customnameplates.CustomNameplates;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class ActionBarTask {

    private BukkitTask timerTask;
    private final ActionBarSender[] actionBarSenders;
    private String otherText;
    private long expireTime;

    public ActionBarTask(Player player, ActionBarConfig[] configs) {
        this.actionBarSenders = new ActionBarSender[configs.length];
        this.otherText = "";
        for (int i = 0; i < configs.length; i++) {
            ActionBarConfig actionBarConfig = configs[i];
            this.actionBarSenders[i] = new ActionBarSender(
                    actionBarConfig.interval(),
                    actionBarConfig.texts(),
                    actionBarConfig.conditions(),
                    player
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
            for (ActionBarSender actionBarSender : actionBarSenders) {
                if (actionBarSender.canSend()) {
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
