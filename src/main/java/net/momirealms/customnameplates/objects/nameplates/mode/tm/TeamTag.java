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

package net.momirealms.customnameplates.objects.nameplates.mode.tm;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.listener.SimpleListener;
import net.momirealms.customnameplates.manager.NameplateManager;
import net.momirealms.customnameplates.manager.TeamManager;
import net.momirealms.customnameplates.objects.nameplates.NameplateMode;
import net.momirealms.customnameplates.objects.nameplates.NameplatesTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class TeamTag extends NameplateMode {

    private final SimpleListener listener;

    public TeamTag(TeamManager teamManager) {
        super(teamManager);
        this.listener = new SimpleListener(this);
    }

    @Override
    public void load() {
        super.load();
        Bukkit.getPluginManager().registerEvents(listener, CustomNameplates.plugin);
    }

    @Override
    public void loadToAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            teamManager.sendUpdateToAll(player, true);
            teamManager.sendUpdateToOne(player);
        }
    }

    @Override
    public void arrangeRefreshTask() {
        if (!NameplateManager.update) return;
        refreshTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CustomNameplates.plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                String teamName = teamManager.getTeamName(player);
                NameplatesTeam nameplatesTeam = teamManager.getTeams().get(teamName);
                if (nameplatesTeam != null) {
                    nameplatesTeam.updateNameplates();
                    teamManager.sendUpdateToAll(player, false);
                }
            }
        }, 20, NameplateManager.refresh);
    }

    @Override
    public void unload() {
        super.unload();
        HandlerList.unregisterAll(listener);
    }
}
