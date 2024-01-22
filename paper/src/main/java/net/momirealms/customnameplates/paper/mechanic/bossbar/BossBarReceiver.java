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

package net.momirealms.customnameplates.paper.mechanic.bossbar;

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.requirement.Condition;
import net.momirealms.customnameplates.api.scheduler.CancellableTask;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.common.Pair;
import net.momirealms.customnameplates.paper.mechanic.misc.DisplayController;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class BossBarReceiver {

    private final CustomNameplatesPlugin plugin;
    private final Player player;
    private final int bossBarSize;
    private final Pair<DisplayController, BossBar>[] bossBars;
    private CancellableTask bossBarTask;

    public BossBarReceiver(CustomNameplatesPlugin plugin, Player player, Pair<DisplayController, BossBar>[] bossBars) {
        this.player = player;
        this.plugin = plugin;
        this.bossBars = bossBars;
        this.bossBarSize = bossBars.length;
    }

    public void arrangeTask() {
        if (this.bossBarTask != null && !this.bossBarTask.isCancelled()) {
            LogUtils.warn("There's already a BossBar task running");
            return;
        }
        this.bossBarTask = this.plugin.getScheduler().runTaskAsyncTimer(() -> {
            try {
                timer();
            } catch (Exception e) {
                LogUtils.severe(
                        "Error occurred when sending BossBars. " +
                        "This might not be a bug in CustomNameplates. Please report " +
                        "to the Plugin on the top of the following " +
                        "stack trace."
                );
                e.printStackTrace();
            }
        }, 50, 50, TimeUnit.MILLISECONDS);
    }

    public void cancelTask() {
        if (this.bossBarTask == null) {
            LogUtils.warn("BossBar task has been already cancelled");
            return;
        }
        this.bossBarTask.cancel();
        this.bossBarTask = null;
    }

    public void destroy() {
        for (Pair<DisplayController, BossBar> pair : bossBars) {
            pair.right().hide();
        }
    }

    public void timer() {
        Condition condition = new Condition(this.player);
        for (int i = 0; i < this.bossBarSize; i++) {
            Pair<DisplayController, BossBar> pair = this.bossBars[i];
            switch (pair.left().stateCheck(condition)) {
                case KEEP -> {
                    if (!pair.left().isShown()) {
                        continue;
                    }
                    if (pair.left().updateText(condition)) {
                        pair.right().setMiniMessageText(pair.left().getLatestContent());
                        pair.right().update();
                    }
                }
                case UPDATE -> {
                    var controller = pair.left();
                    if (controller.isShown()) {
                        for (int j = i + 1; j < this.bossBarSize; j++)
                            if (this.bossBars[j].left().isShown())
                                this.bossBars[j].right().hide();

                        controller.initialize();
                        pair.right().setMiniMessageText(pair.left().getLatestContent());
                        pair.right().show();

                        for (int j = i + 1; j < this.bossBarSize; j++)
                            if (this.bossBars[j].left().isShown())
                                this.bossBars[j].right().show();
                    } else {
                        pair.right().hide();
                    }
                }
            }
        }
    }
}