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

package net.momirealms.customnameplates.object.nameplate.mode;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.listener.JoinQuitListener;
import net.momirealms.customnameplates.manager.TeamManager;
import net.momirealms.customnameplates.object.Function;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitTask;

public abstract class AbstractNameplateTag extends Function {

    protected BukkitTask refreshTask;
    protected CustomNameplates plugin;
    protected JoinQuitListener joinQuitListener;

    public AbstractNameplateTag(CustomNameplates plugin) {
        this.plugin = plugin;
        this.joinQuitListener = new JoinQuitListener(this);
    }

    @Override
    public void load() {
        loadToAllPlayers();
        arrangeRefreshTask();
        Bukkit.getPluginManager().registerEvents(joinQuitListener, plugin);
    }

    @Override
    public void unload() {
        if (refreshTask != null) refreshTask.cancel();
        HandlerList.unregisterAll(joinQuitListener);
    }

    @Override
    public void onJoin(Player player) {

    }

    @Override
    public void onQuit(Player player) {

    }

    public void arrangeRefreshTask() {
        //child
    }

    public void loadToAllPlayers() {
        TeamManager teamManager = plugin.getTeamManager();
        for (Player player : Bukkit.getOnlinePlayers()) {
            teamManager.sendUpdateToAll(player, true);
            teamManager.sendUpdateToOne(player);
        }
    }
}
