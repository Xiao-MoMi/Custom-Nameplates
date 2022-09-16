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

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.hook.TABTeamHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TeamManager {

    private final ConcurrentHashMap<String, NameplatesTeam> teams = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, String> teamNames = new ConcurrentHashMap<>();

    public void createTeam(Player player) {
        String teamName = getTeamName(player);
        if (teamName != null) {
            if (!teams.containsKey(teamName)) teams.put(teamName, new NameplatesTeam(player));
            CustomNameplates.instance.getTeamPacketManager().sendUpdateToAll(player, false);
            CustomNameplates.instance.getTeamPacketManager().sendUpdateToOne(player);
        }
        else {
            if (player == null || !player.isOnline()) return;
            Bukkit.getScheduler().runTaskLater(CustomNameplates.instance, () -> {
                createTeam(player);
            },20);
        }
    }

    public Map<String, NameplatesTeam> getTeams() {
        return teams;
    }

    @Nullable
    public static String getTeamName(Player player) {
        String teamName;
        if (ConfigManager.Main.tab) {
            return TABTeamHook.getTABTeam(player.getName());
        }
        if (ConfigManager.Main.tab_bc) {
            teamName = teamNames.get(player.getName());
            if (teamName == null) {
                sendRequest(player);
                return null;
            }
            else {
                return teamName;
            }
        }
        return player.getName();
    }

    private static void sendRequest(Player player) {
        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
        dataOutput.writeUTF(player.getName());
        player.sendPluginMessage(CustomNameplates.instance, "customnameplates:cnp", dataOutput.toByteArray());
    }
}
