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

package net.momirealms.customnameplates.listener;

import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.data.DataManager;
import net.momirealms.customnameplates.data.PlayerData;
import net.momirealms.customnameplates.data.SqlHandler;
import net.momirealms.customnameplates.hook.TABHook;
import net.momirealms.customnameplates.scoreboard.ScoreBoardManager;
import net.momirealms.customnameplates.utils.ArmorStandPacketUtil;
import net.momirealms.customnameplates.utils.TeamPacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;

public class PlayerListener implements Listener {

    public static HashMap<Player, BukkitTask> taskCache = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        CustomNameplates.instance.getDataManager().loadData(event.getPlayer());

        if (ConfigManager.Nameplate.update && ConfigManager.Nameplate.mode_team) {
            Player player = event.getPlayer();
            BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(CustomNameplates.instance, () -> {
                String teamName = player.getName();
                if (ConfigManager.MainConfig.tab) teamName = TABHook.getTABTeam(teamName);
                ScoreBoardManager.teams.get(teamName).updateNameplates();
                TeamPacketUtil.sendUpdateToAll(player);
            }, 20, ConfigManager.Nameplate.refresh);
            taskCache.put(player, task);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        CustomNameplates.instance.getDataManager().unloadPlayer(event.getPlayer().getUniqueId());
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Player player = event.getPlayer();
        String teamName = player.getName();
        if (ConfigManager.MainConfig.tab) teamName = TABHook.getTABTeam(teamName);
        Team team = scoreboard.getTeam(teamName);
        if (team != null) team.unregister();
        ScoreBoardManager.teams.remove(teamName);
        if (ConfigManager.Nameplate.update) {
            if (ConfigManager.Nameplate.mode_team) {
                TeamPacketUtil.teamInfoCache.remove(player);
                BukkitTask bukkitTask = taskCache.remove(player);
                if (bukkitTask != null) {
                    bukkitTask.cancel();
                }
            }
            else {
                int id = event.getPlayer().getEntityId();
                HashMap<Integer, BukkitTask> taskMap = ArmorStandPacketUtil.taskCache.remove(id);
                if (taskMap != null) {
                    for (int otherID : taskMap.keySet()) {
                        HashMap<Integer, BukkitTask> otherTaskMap = ArmorStandPacketUtil.taskCache.get(otherID);
                        if (otherTaskMap != null) {
                            BukkitTask task = otherTaskMap.remove(id);
                            if (task != null) {
                                task.cancel();
                            }
                        }
                    }
                    taskMap.values().forEach(BukkitTask::cancel);
                }
            }
        }
    }

    @EventHandler
    public void onAccept(PlayerResourcePackStatusEvent event) {
        if (!ConfigManager.Nameplate.show_after) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                PlayerData playerData = DataManager.cache.get(event.getPlayer().getUniqueId());
                if (playerData == null) {
                    return;
                }
                if (event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
                    playerData.setAccepted(1);
                    Player player = event.getPlayer();
                    SqlHandler.save(playerData, player.getUniqueId());
                    TeamPacketUtil.sendUpdateToOne(player);
                    if (!ConfigManager.Nameplate.mode_team) {
                        HashMap<Integer, BukkitTask> tasks = ArmorStandPacketUtil.taskCache.get(player.getEntityId());
                        if (tasks == null) return;
                        for (Integer id : tasks.keySet()){
                            ArmorStandPacketUtil.forceUpdateOneToOne(id, player);
                        }
                    }
                }
                else if (event.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED || event.getStatus() == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
                    playerData.setAccepted(0);
                    SqlHandler.save(playerData, event.getPlayer().getUniqueId());
                    Player player = event.getPlayer();
                    TeamPacketUtil.sendUpdateToOne(player);
                    if (!ConfigManager.Nameplate.mode_team) {
                        HashMap<Integer, BukkitTask> tasks = ArmorStandPacketUtil.taskCache.get(player.getEntityId());
                        if (tasks == null) return;
                        for (Integer id : tasks.keySet()){
                            ArmorStandPacketUtil.forceUpdateOneToOne(id, player);
                        }
                    }
                }
            }
        }.runTaskAsynchronously(CustomNameplates.instance);
    }
}