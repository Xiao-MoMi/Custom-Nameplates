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

package net.momirealms.customnameplates.paper.mechanic.actionbar;

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.requirement.Condition;
import net.momirealms.customnameplates.api.scheduler.CancellableTask;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.paper.adventure.AdventureManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.misc.DisplayController;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class ActionBarReceiver {

    private final CustomNameplatesPlugin plugin;
    private final Player player;
    private CancellableTask actionBarTask;
    private final DisplayController controller;
    private long lastUpdateTime;
    private String otherPluginText;
    private long expireTime;

    public ActionBarReceiver(CustomNameplatesPlugin plugin, Player player, DisplayController controller) {
        this.plugin = plugin;
        this.player = player;
        this.controller = controller;
        this.otherPluginText = "";
    }

    public void timer() {
        if (System.currentTimeMillis() > expireTime) {
            this.otherPluginText = "";
        }
        Condition condition = new Condition(player);
        switch (controller.stateCheck(condition)) {
            case KEEP -> {
                if (!controller.isShown()) {
                    return;
                }
                long current = System.currentTimeMillis();
                if (controller.updateText(condition) || shouldSendBeatPack(current)) {
                    AdventureManagerImpl.getInstance().sendActionbar(player, controller.getLatestContent());
                    lastUpdateTime = current;
                }
            }
            case UPDATE -> {
                if (controller.isShown()) {
                    controller.initialize();
                    AdventureManagerImpl.getInstance().sendActionbar(player, controller.getLatestContent());
                } else {
                    AdventureManagerImpl.getInstance().sendActionbar(player, "");
                }
            }
        }
    }

    public void arrangeTask() {
        if (this.actionBarTask != null && !this.actionBarTask.isCancelled()) {
            LogUtils.warn("There's already an ActionBar task running");
            return;
        }
        this.actionBarTask = this.plugin.getScheduler().runTaskAsyncTimer(() -> {
            try {
                timer();
            } catch (Exception e) {
                LogUtils.severe(
                        "Error occurred when sending ActionBars. " +
                        "This might not be a bug in CustomNameplates. Please report " +
                        "to the Plugin on the top of the following " +
                        "stack trace."
                );
                e.printStackTrace();
            }
        }, 50, 50, TimeUnit.MILLISECONDS);
    }

    public void cancelTask() {
        if (this.actionBarTask == null) {
            LogUtils.warn("ActionBar task has been already cancelled");
            return;
        }
        this.actionBarTask.cancel();
        this.actionBarTask = null;
    }

    public boolean shouldSendBeatPack(long current) {
        return current - lastUpdateTime > 1700;
    }

    public void setOtherPluginText(String text, long current) {
        this.otherPluginText = text;
        this.expireTime = current + 3000;
    }

    public String getOtherPluginText() {
        return otherPluginText;
    }

    public void destroy() {
        AdventureManagerImpl.getInstance().sendActionbar(player, "");
    }
}
