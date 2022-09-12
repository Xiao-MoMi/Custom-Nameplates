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

package net.momirealms.customnameplates.nameplates.mode.tm;

import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.nameplates.NameplatesTeam;
import net.momirealms.customnameplates.nameplates.TeamManager;
import net.momirealms.customnameplates.nameplates.mode.EventListener;
import net.momirealms.customnameplates.nameplates.mode.NameplateManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class TeamTag extends NameplateManager {

    private EventListener listener;

    private BukkitTask task;

    private final TeamManager teamManager;

    private final HashMap<Player, BukkitTask> taskCache = new HashMap<>();

    public TeamTag(String name, TeamManager teamManager) {
        super(name);
        this.teamManager = teamManager;
    }

    @Override
    public void load() {
        listener = new EventListener(this);

        Bukkit.getPluginManager().registerEvents(listener, CustomNameplates.instance);

        for (Player player : Bukkit.getOnlinePlayers()) {
            CustomNameplates.instance.getTeamPacketManager().sendUpdateToAll(player);
            CustomNameplates.instance.getTeamPacketManager().sendUpdateToOne(player);
        }

        if (!ConfigManager.Nameplate.update) return;

        task = Bukkit.getScheduler().runTaskTimerAsynchronously(CustomNameplates.instance, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                String teamName = TeamManager.getTeamName(player);
                NameplatesTeam nameplatesTeam = teamManager.getTeams().get(teamName);
                if (nameplatesTeam != null) {
                    nameplatesTeam.updateNameplates();
                    CustomNameplates.instance.getTeamPacketManager().sendUpdateToAll(player);
                }
            }
        }, 20, ConfigManager.Nameplate.refresh);
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(listener);
        taskCache.clear();
        task.cancel();
    }

    @Override
    public void onJoin(Player player) {
        super.onJoin(player);
    }

    @Override
    public void onQuit(Player player) {
        BukkitTask bukkitTask = taskCache.remove(player);
        if (bukkitTask != null) bukkitTask.cancel();
        super.onQuit(player);
    }

//    @Override
//    public void onRP(Player player, PlayerResourcePackStatusEvent.Status status) {
//        super.onRP(player, status);
//    }
}
