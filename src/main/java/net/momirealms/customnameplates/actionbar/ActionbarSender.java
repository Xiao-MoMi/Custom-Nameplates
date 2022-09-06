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

import me.clip.placeholderapi.PlaceholderAPI;
import net.momirealms.customnameplates.utils.AdventureUtil;
import net.momirealms.customnameplates.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionbarSender extends BukkitRunnable {

    private int timer;

    public ActionbarSender(){
        this.timer = 0;
    }

    @Override
    public void run() {
        if (timer < ConfigManager.ActionbarConfig.rate){
            timer++;
        }else {
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (ConfigManager.MainConfig.placeholderAPI){
                    AdventureUtil.playerActionbar(player, PlaceholderAPI.setPlaceholders(player, ConfigManager.ActionbarConfig.text));
                }else {
                    AdventureUtil.playerActionbar(player, ConfigManager.ActionbarConfig.text);
                }
            });
            timer = 0;
        }
    }
}
