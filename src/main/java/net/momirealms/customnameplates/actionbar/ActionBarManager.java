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

package net.momirealms.customnameplates.actionbar;

import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.Function;
import net.momirealms.customnameplates.hook.PlaceholderManager;
import net.momirealms.customnameplates.utils.AdventureUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ActionBarManager extends Function {

    private BukkitTask bukkitTask;

    private int timer;

    public ActionBarManager(String name) {
        super(name);
    }

    @Override
    public void load() {

        PlaceholderManager placeholderManager = CustomNameplates.instance.getPlaceholderManager();

        this.bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (timer < ConfigManager.ActionbarConfig.rate){
                    timer++;
                }
                else {
                    Bukkit.getOnlinePlayers().forEach(player -> AdventureUtil.playerActionbar(player, ConfigManager.Main.placeholderAPI ? placeholderManager.parsePlaceholders(player, ConfigManager.ActionbarConfig.text) : ConfigManager.ActionbarConfig.text));
                    timer = 0;
                }
            }
        }.runTaskTimerAsynchronously(CustomNameplates.instance, 1, 1);
    }

    @Override
    public void unload() {
        this.bukkitTask.cancel();
    }
}
