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

package net.momirealms.customnameplates.bossbar;

import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.requirements.PlayerCondition;
import net.momirealms.customnameplates.requirements.Requirement;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class TimerTaskP {

    private final List<Sender> bossBarCache = new ArrayList<>();

    private final BukkitTask conditionTask;

    public TimerTaskP(Player player){

        for (BossBarConfig config : ConfigManager.bossBars.values()) {
            Sender sender = new Sender(player, config);
            bossBarCache.add(sender);
        }

        this.conditionTask = new BukkitRunnable() {
            @Override
            public void run() {

                PlayerCondition playerCondition = new PlayerCondition(player.getLocation(), player);

                outer:
                    for (Sender sender : bossBarCache) {
                        for (Requirement requirement : sender.getConfig().getConditions()) {
                            if (!requirement.isConditionMet(playerCondition)) {
                                if (sender.getStatus()) {
                                    sender.hide();
                                }
                                continue outer;
                            }
                        }
                        if (!sender.getStatus()) {
                            sender.show();
                        }
                    }

            }
        }.runTaskTimerAsynchronously(CustomNameplates.instance, 1, 20);
    }

    public void stopTimer(){
        if (this.conditionTask != null) {
            this.conditionTask.cancel();
        }
        bossBarCache.forEach(Sender::hide);
        bossBarCache.clear();
    }
}
