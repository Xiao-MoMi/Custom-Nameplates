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

package net.momirealms.customnameplates.bossbar.adventure;

import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class TimerTaskA {

    private HashMap<Integer, BossBarSenderA> bossbarCache = new HashMap<>();

    public TimerTaskA(Player player){
        ConfigManager.bossbarsA.forEach((key, bossbarConfig) -> {
            BossBarSenderA bossbar = new BossBarSenderA(player, bossbarConfig);
            bossbar.showBossbar();
            BukkitTask task = bossbar.runTaskTimerAsynchronously(CustomNameplates.instance, 1,1);
            bossbarCache.put(task.getTaskId(), bossbar);
        });
    }

    public void stopTimer(){
        bossbarCache.forEach((key,value)-> {
            value.hideBossbar();
            Bukkit.getScheduler().cancelTask(key);
        });
        bossbarCache = null;
    }
}