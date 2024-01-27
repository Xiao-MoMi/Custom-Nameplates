package net.momirealms.customnameplates.bungeecord.team;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.netty.PacketHandler;
import net.md_5.bungee.protocol.packet.Team;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UnknownProvider extends PacketHandler implements Listener, TeamProvider {

    private final ConcurrentHashMap<UUID, PlayerTeamView> teamViewMap;

    public UnknownProvider() {
        this.teamViewMap = new ConcurrentHashMap<>();
    }

    @Override
    public void handle(Team team) {
        switch (team.getMode()) {
            case 0, 3 -> {
            }
            case 1 -> {

            }
        }
    }

    @Override
    public String toString() {
        return null;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        teamViewMap.put(event.getPlayer().getUniqueId(), new PlayerTeamView());
    }

    @EventHandler
    public void onDisConnect(PlayerDisconnectEvent event) {
        teamViewMap.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public String getTeam(ProxiedPlayer player, ProxiedPlayer viewer) {


        return null;
    }

    public static class PlayerTeamView {

        private final HashMap<String, String> playerToTeamMap;
        private final HashMap<String, String[]> teamToPlayerMap;

        public PlayerTeamView() {
            this.playerToTeamMap = new HashMap<>();
            this.teamToPlayerMap = new HashMap<>();
        }

        public String getTeam(ProxiedPlayer player) {
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
