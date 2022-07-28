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

package net.momirealms.customnameplates.scoreboard;

import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.hook.TABHook;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public record ScoreBoardManager(CustomNameplates plugin) {

    public static Map<String, NameplatesTeam> teams = new HashMap<>();

    public NameplatesTeam getOrCreateTeam(Player player) {
        if (ConfigManager.MainConfig.tab){
            String tabTeamName = TABHook.getTABTeam(player.getName());
            if (!teams.containsKey(tabTeamName)) {
                teams.put(tabTeamName, new NameplatesTeam(this.plugin, player));
            }
            this.getTeam(tabTeamName).updateNameplates();
            return teams.get(tabTeamName);
        } else {
            if (!teams.containsKey(player.getName())) {
                teams.put(player.getName(), new NameplatesTeam(this.plugin, player));
            }
            this.getTeam(player.getName()).updateNameplates();
            return teams.get(player.getName());
        }
    }

    public void removeTeam(String teamName) {
        teams.remove(teamName);
    }

    public NameplatesTeam getTeam(String teamName) {
        return teams.get(teamName);
    }
}
