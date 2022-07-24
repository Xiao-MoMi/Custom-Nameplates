package net.momirealms.customnameplates.scoreboard;

import net.momirealms.customnameplates.CustomNameplates;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ScoreBoardManager {

    private final CustomNameplates plugin;
    public static Map<String, NameplatesTeam> teams = new HashMap<>();

    public ScoreBoardManager(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    public NameplatesTeam getOrCreateTeam(Player player) {
        if (!teams.containsKey(player.getName())) {
            teams.put(player.getName(), new NameplatesTeam(this.plugin, player));
        }
        this.getTeam(player.getName()).updateNameplates();
        return teams.get(player.getName());
    }

    public void removeTeam(String playerName) {
        teams.remove(playerName);
    }

    public NameplatesTeam getTeam(String team) {
        return teams.get(team);
    }

    public boolean doesTeamExist(String playerName) {
        return teams.containsKey(playerName);
    }
}
