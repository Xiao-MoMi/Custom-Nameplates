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

package net.momirealms.customnameplates.objects.actionbar;

import me.clip.placeholderapi.PlaceholderAPI;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.objects.requirements.PlayerCondition;
import net.momirealms.customnameplates.objects.requirements.Requirement;
import net.momirealms.customnameplates.utils.AdventureUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ActionBarTask {

    private final ActionBarConfig config;
    private int timer_1;
    private int timer_2;
    private int counter;
    private String text;
    private final int size;
    private BukkitTask task;

    public ActionBarTask(ActionBarConfig config) {
        this.config = config;
        size = config.getText().length;
        text = config.getText()[0];
        start();
    }

    public void setText(int position) {
        this.text = config.getText()[position];
    }

    private void start() {

        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                if (size != 1) {
                    timer_2++;
                    if (timer_2 > config.getInterval()) {
                        timer_2 = 0;
                        counter++;
                        if (counter == size) {
                            counter = 0;
                        }
                        setText(counter);
                    }
                }
                if (timer_1 < config.getRate()){
                    timer_1++;
                }
                else {
                    outer:
                    for (Player player : Bukkit.getOnlinePlayers()) {

                        PlayerCondition condition = new PlayerCondition(player.getLocation(), player);

                        for (Requirement requirement : config.getConditions()) {
                            if (!requirement.isConditionMet(condition)) {
                                continue outer;
                            }
                        }

                        AdventureUtil.playerActionbar(player, AdventureUtil.replaceLegacy(PlaceholderAPI.setPlaceholders(player, text)));
                    }
                }
            }
        }.runTaskTimerAsynchronously(CustomNameplates.plugin, 1, 1);
    }

    public void stop() {
        if (this.task != null) {
            task.cancel();
        }
    }
}
