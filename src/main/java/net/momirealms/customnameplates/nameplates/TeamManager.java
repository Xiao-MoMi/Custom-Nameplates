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

package net.momirealms.customnameplates.nameplates;

import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.hook.TABTeamHook;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TeamManager {

    private final ConcurrentHashMap<String, NameplatesTeam> teams = new ConcurrentHashMap<>();

    public void createTeam(Player player) {
        String teamName = player.getName();
        if (ConfigManager.Main.tab) teamName = TABTeamHook.getTABTeam(teamName);
        if (!teams.containsKey(teamName)) teams.put(teamName, new NameplatesTeam(player));
        teams.get(teamName);
    }

    public Map<String, NameplatesTeam> getTeams() {
        return teams;
    }

    public static String getTeamName(Player player) {
        String teamName = player.getName();
        if (ConfigManager.Main.tab) teamName = TABTeamHook.getTABTeam(teamName);
        return teamName;
    }
}
