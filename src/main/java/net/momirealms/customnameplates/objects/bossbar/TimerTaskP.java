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

package net.momirealms.customnameplates.objects.bossbar;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.objects.requirements.PlayerCondition;
import net.momirealms.customnameplates.objects.requirements.Requirement;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class TimerTaskP {

    private final List<BossBarSender> bossBarCache = new ArrayList<>();

    private final BukkitTask conditionTask;

    public TimerTaskP(Player player){

        for (BossBarConfig config : CustomNameplates.plugin.getBossBarManager().getBossBars().values()) {
            BossBarSender bossBarSender = new BossBarSender(player, config);
            bossBarCache.add(bossBarSender);
        }

        this.conditionTask = new BukkitRunnable() {
            @Override
            public void run() {

                PlayerCondition playerCondition = new PlayerCondition(player.getLocation(), player);

                outer:
                    for (BossBarSender bossBarSender : bossBarCache) {
                        for (Requirement requirement : bossBarSender.getConfig().getConditions()) {
                            if (!requirement.isConditionMet(playerCondition)) {
                                if (bossBarSender.getStatus()) {
                                    bossBarSender.hide();
                                }
                                continue outer;
                            }
                        }
                        if (!bossBarSender.getStatus()) {
                            bossBarSender.show();
                        }
                    }

            }
        }.runTaskTimerAsynchronously(CustomNameplates.plugin, 1, 20);
    }

    public void stopTimer(){
        if (this.conditionTask != null) {
            this.conditionTask.cancel();
        }
        for (BossBarSender bossBarSender : bossBarCache) {
            bossBarSender.hide();
        }
        bossBarCache.clear();
    }
}
