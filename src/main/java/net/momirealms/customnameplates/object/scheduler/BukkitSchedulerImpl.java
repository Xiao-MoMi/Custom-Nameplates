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

package net.momirealms.customnameplates.object.scheduler;

import net.momirealms.customnameplates.CustomNameplates;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class BukkitSchedulerImpl implements SchedulerPlatform {

    private final CustomNameplates plugin;

    public BukkitSchedulerImpl(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    @Override
    public <T> Future<T> callSyncMethod(@NotNull Callable<T> task) {
        return Bukkit.getScheduler().callSyncMethod(plugin, task);
    }

    @Override
    public void runTask(Runnable runnable, Location location) {
        Bukkit.getScheduler().runTask(plugin, runnable);
    }

    @Override
    public void runTaskLater(Runnable runnable, Location location, long delay) {
        Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
    }

    @Override
    public void runTaskAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    @Override
    public void runTaskAsyncLater(Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
    }

    @Override
    public TimerTask runTaskAsyncTimer(Runnable runnable, long delay, long interval) {
        return new BukkitTimerTask(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, interval));
    }

    @Override
    public TimerTask runTaskTimer(Runnable runnable, Location location, long delay, long interval) {
        return new BukkitTimerTask(Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, interval));
    }
}
