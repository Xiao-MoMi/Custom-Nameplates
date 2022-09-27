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

package net.momirealms.customnameplates.nameplates.mode;

import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.Function;
import net.momirealms.customnameplates.nameplates.TeamManager;
import net.momirealms.customnameplates.nameplates.mode.tmpackets.TeamPacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public abstract class NameplateManager extends Function {

    protected NameplateManager(String name) {
        super(name);
    }

    @Override
    public void load() {
    }

    @Override
    public void unload(){
    }

    public void onJoin(Player player) {
        CustomNameplates.instance.getDataManager().loadData(player);
    }

    public void onQuit(Player player) {
        String teamName;
        CustomNameplates.instance.getDataManager().unloadPlayer(player.getUniqueId());
        if (ConfigManager.Main.tab_bc) {
            teamName = TeamManager.getTeamName(player);
            CustomNameplates.instance.getTeamManager().getTeams().remove(teamName);
            TeamManager.teamNames.remove(player.getName());
            return;
        }
        if (ConfigManager.Main.tab) {
            teamName = TeamManager.getTeamName(player);
            CustomNameplates.instance.getTeamManager().getTeams().remove(teamName);
            return;
        }
        if (ConfigManager.Nameplate.fakeTeam) {
            TeamPacketUtil.destroyTeamToAll(player);
            teamName = TeamManager.getTeamName(player);
        }
        else {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            teamName = player.getName();
            Team team = scoreboard.getTeam(teamName);
            if (team != null) team.unregister();
        }
        CustomNameplates.instance.getTeamManager().getTeams().remove(teamName);
    }
}