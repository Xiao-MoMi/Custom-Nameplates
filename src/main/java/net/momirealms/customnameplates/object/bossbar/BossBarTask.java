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

package net.momirealms.customnameplates.object.bossbar;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.object.scheduler.TimerTask;
import org.bukkit.entity.Player;

public class BossBarTask {

    private TimerTask timerTask;
    private final BossBarSender[] bossBarSenders;

    public BossBarTask(Player player, BossBarConfig[] configs) {
        this.bossBarSenders = new BossBarSender[configs.length];
        for (int i = 0; i < configs.length; i++) {
            BossBarConfig bossBarConfig = configs[i];
            this.bossBarSenders[i] = new BossBarSender(
                    bossBarConfig.interval(),
                    bossBarConfig.refreshRate(),
                    bossBarConfig.texts(),
                    bossBarConfig.conditions(),
                    bossBarConfig.overlay(),
                    bossBarConfig.color(),
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
        this.timerTask = CustomNameplates.getInstance().getScheduler().runTaskAsyncTimer(() -> {
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
