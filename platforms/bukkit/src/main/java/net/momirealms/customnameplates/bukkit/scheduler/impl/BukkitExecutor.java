/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.momirealms.customnameplates.bukkit.scheduler.impl;

import net.momirealms.customnameplates.common.plugin.scheduler.RegionExecutor;
import net.momirealms.customnameplates.common.plugin.scheduler.SchedulerTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

public class BukkitExecutor implements RegionExecutor<Location> {

    private final Plugin plugin;

    public BukkitExecutor(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run(Runnable r, Location l) {
        if (Bukkit.isPrimaryThread()) {
            r.run();
            return;
        }
        Bukkit.getScheduler().runTask(plugin, r);
    }

    @Override
    public SchedulerTask runLater(Runnable r, long delayTicks, Location l) {
        if (delayTicks == 0) {
            return new BukkitTask(Bukkit.getScheduler().runTask(plugin, r));
        }
        return new BukkitTask(Bukkit.getScheduler().runTaskLater(plugin, r, delayTicks));
    }

    @Override
    public SchedulerTask runRepeating(Runnable r, long delayTicks, long period, Location l) {
        return new BukkitTask(Bukkit.getScheduler().runTaskTimer(plugin, r, delayTicks, period));
    }
}
