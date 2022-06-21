package net.momirealms.customnameplates.scoreboard;

import net.momirealms.customnameplates.CustomNameplates;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ScoreBoardManager {

    private final CustomNameplates plugin;
    private final Map<String, NameplatesTeam> teams;

    public ScoreBoardManager(CustomNameplates plugin) {
        this.teams = new HashMap<>();
        this.plugin = plugin;
    }

    public NameplatesTeam getOrCreateTeam(Player player) {
        if (!this.teams.containsKey(player.getName())) {
            this.teams.put(player.getName(), new NameplatesTeam(this.plugin, player));
        }
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.getTeam(player.getName()).updateNameplates(), 20);
        return this.teams.get(player.getName());
    }

    public void removeTeam(String playerName) {
        this.teams.remove(playerName);
    }

    public NameplatesTeam getTeam(String team) {
        return this.teams.get(team);
    }

    public boolean doesTeamExist(String playerName) {
        return this.teams.containsKey(playerName);
    }
}
