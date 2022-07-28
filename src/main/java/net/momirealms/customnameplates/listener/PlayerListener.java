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
import net.momirealms.customnameplates.hook.TABHook;
import net.momirealms.customnameplates.scoreboard.ScoreBoardManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public record PlayerListener(CustomNameplates plugin) implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.plugin.getDataManager().loadData(event.getPlayer());
        Bukkit.getScheduler().runTaskLaterAsynchronously(CustomNameplates.instance, ()-> {
            if (ConfigManager.MainConfig.tab){
                Bukkit.getOnlinePlayers().forEach(player -> this.plugin.getScoreBoardManager().getTeam(TABHook.getTABTeam(player.getName())).updateNameplates());
            }else {
                Bukkit.getOnlinePlayers().forEach(player -> this.plugin.getScoreBoardManager().getTeam(player.getName()).updateNameplates());
            }
        }, 50);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.plugin.getDataManager().unloadPlayer(event.getPlayer().getUniqueId());
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team;
        String teamName;
        if (ConfigManager.MainConfig.tab){
            teamName = TABHook.getTABTeam(event.getPlayer().getName());
        }else {
            teamName = event.getPlayer().getName();
        }
        team = scoreboard.getTeam(teamName);
        ScoreBoardManager.teams.remove(teamName);
        if (team != null){
            team.unregister();
        }
    }

    @EventHandler
    public void onAccept(PlayerResourcePackStatusEvent event) {
        PlayerData playerData = DataManager.cache.get(event.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTaskLaterAsynchronously(CustomNameplates.instance, ()-> {
            if (playerData == null) {
                return;
            }
            if (event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
                playerData.setAccepted(1);
                if (ConfigManager.MainConfig.tab){
                    Bukkit.getOnlinePlayers().forEach(player -> this.plugin.getScoreBoardManager().getTeam(TABHook.getTABTeam(player.getName())).updateNameplates());
                }else {
                    Bukkit.getOnlinePlayers().forEach(player -> this.plugin.getScoreBoardManager().getTeam(player.getName()).updateNameplates());
                }
            } else if(event.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED || event.getStatus() == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
                playerData.setAccepted(0);
                if (ConfigManager.MainConfig.tab){
                    Bukkit.getOnlinePlayers().forEach(player -> this.plugin.getScoreBoardManager().getTeam(TABHook.getTABTeam(player.getName())).updateNameplates());
                }else {
                    Bukkit.getOnlinePlayers().forEach(player -> this.plugin.getScoreBoardManager().getTeam(player.getName()).updateNameplates());
                }
            }
        }, 20);
    }
}
