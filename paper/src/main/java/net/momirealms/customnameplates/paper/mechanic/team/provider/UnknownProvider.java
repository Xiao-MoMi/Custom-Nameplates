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

package net.momirealms.customnameplates.paper.mechanic.team.provider;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class UnknownProvider extends PacketAdapter implements TeamProvider, Listener {

    private final HashMap<UUID, PlayerTeamView> teamViewMap;

    public UnknownProvider() {
        super(CustomNameplatesPlugin.getInstance(), ListenerPriority.HIGHEST, PacketType.Play.Server.SCOREBOARD_TEAM);
        this.teamViewMap = new HashMap<>();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        teamViewMap.put(event.getPlayer().getUniqueId(), new PlayerTeamView());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        teamViewMap.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        Player receiver = event.getPlayer();
        PlayerTeamView view = teamViewMap.get(receiver.getUniqueId());
        if (view == null) {
            return;
        }

        PacketContainer packet = event.getPacket();
        String team = (String) packet.getModifier().read(1);
        switch ((int) packet.getModifier().read(0)) {
            case 0, 3 -> view.addMembers(team, (Collection<String>) packet.getModifier().read(2));
            case 1 -> view.removeTeam(team);
            case 4 -> view.removeMembers(team, (Collection<String>) packet.getModifier().read(2));
            default -> {
                // ignore 2 update team info
            }
        }
    }

    @Override
    public String getTeam(Player player, Player viewer) {
        PlayerTeamView view = teamViewMap.get(viewer.getUniqueId());
        if (view != null) {
            String team = view.getTeam(player);
            if (team != null)
                return team;
        }

        // should not reach here
        CustomNameplatesPlugin.get().debug("Getting real teams for " + player.getName() + " viewer: " + viewer.getName());
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getPlayerTeam(player);
        if (team == null) {
            return null;
        }
        return team.getName();
    }

    public static class PlayerTeamView {

        private final HashMap<String, String> playerToTeamMap;
        private final HashMap<String, String[]> teamToPlayerMap;

        public PlayerTeamView() {
            this.playerToTeamMap = new HashMap<>();
            this.teamToPlayerMap = new HashMap<>();
        }

        public String getTeam(Player player) {
            return playerToTeamMap.get(player.getName());
        }

        public void removeTeam(String team) {
            String[] removedMembers = teamToPlayerMap.remove(team);
            if (removedMembers != null) {
                for (String member : removedMembers) {
                    playerToTeamMap.remove(member);
                }
            }
        }

        public void addMembers(String team, Collection<String> members) {
            String[] oldMembers = teamToPlayerMap.remove(team);
            if (oldMembers != null) {
                String[] newMembers = new String[members.size() + oldMembers.length];
                int i = 0;
                for (String m : oldMembers) {
                    newMembers[i] = m;
                    i++;
                }
                for (String m : members) {
                    newMembers[i] = m;
                    i++;
                }
                teamToPlayerMap.put(team, newMembers);
                for (String member : newMembers) {
                    playerToTeamMap.put(member, team);
                }
            } else {
                teamToPlayerMap.put(team, members.toArray(new String[0]));
                for (String member : members) {
                    playerToTeamMap.put(member, team);
                }
            }
        }

        public void removeMembers(String team, Collection<String> members) {
            String[] oldMembers = teamToPlayerMap.remove(team);
            if (oldMembers != null) {
                ArrayList<String> newMembers = new ArrayList<>(Arrays.asList(oldMembers));
                for (String member : members) {
                    playerToTeamMap.remove(member);
                    newMembers.remove(member);
                }
                teamToPlayerMap.put(team, newMembers.toArray(new String[0]));
            }
        }
    }
}
