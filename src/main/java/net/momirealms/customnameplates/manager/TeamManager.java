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

package net.momirealms.customnameplates.manager;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.objects.Function;
import net.momirealms.customnameplates.objects.nameplates.NameplatesTeam;
import net.momirealms.customnameplates.objects.team.*;
import net.momirealms.customnameplates.utils.TeamManagePacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TeamManager extends Function {

    private final ConcurrentHashMap<String, NameplatesTeam> teams ;
    private TeamPacketInterface teamPacketInterface;
    private TeamNameInterface teamNameInterface;
    private final TeamManagePacketUtil teamManagePacketUtil;

    public TeamManager() {
        teams = new ConcurrentHashMap<>();
        this.teamManagePacketUtil = new TeamManagePacketUtil(this);
    }

    @Override
    public void load() {
        if (NameplateManager.mode.equalsIgnoreCase("Team")) {
            teamPacketInterface = new TeamPrefixSuffix(this);
        }
        if (NameplateManager.mode.equalsIgnoreCase("Teleporting") || NameplateManager.mode.equalsIgnoreCase("Riding")) {
            teamPacketInterface = new SimpleTeamVisibility(this);
        }

        if (ConfigManager.tab_BC_hook) {
            teamNameInterface = new TABbcHook();
        }
        else if (ConfigManager.tab_hook) {
            teamNameInterface = new TABTeamHook();
        }
        else {
            teamNameInterface = new SimpleTeam();
        }
    }

    @Override
    public void unload() {
        if (teamNameInterface != null) teamNameInterface.unload();
    }

    public void sendUpdateToOne(Player player) {
        if (teamPacketInterface != null) teamPacketInterface.sendUpdateToOne(player);
    }

    public void sendUpdateToAll(Player player, boolean force) {
        if (teamPacketInterface != null) teamPacketInterface.sendUpdateToAll(player, force);
    }

    public void createTeam(Player player) {
        String teamName = getTeamName(player);
        if (teamName != null) {
            if (!teams.containsKey(teamName)) teams.put(teamName, new NameplatesTeam(player, this));
            teamPacketInterface.sendUpdateToAll(player, false);
            teamPacketInterface.sendUpdateToOne(player);
        }
        else {
            if (player == null || !player.isOnline()) return;
            Bukkit.getScheduler().runTaskLater(CustomNameplates.plugin, () -> {
                createTeam(player);
            },20);
        }
    }

    public Map<String, NameplatesTeam> getTeams() {
        return teams;
    }

    public String getTeamName(Player player) {
        return teamNameInterface.getTeamName(player);
    }

    public void removePlayerFromTeamCache(Player player) {
        teams.remove(getTeamName(player));
    }

    @Nullable
    public NameplatesTeam getNameplatesTeam(Player player) {
        return teams.get(getTeamName(player));
    }

    public TeamManagePacketUtil getTeamManagePacketUtil() {
        return teamManagePacketUtil;
    }
}
