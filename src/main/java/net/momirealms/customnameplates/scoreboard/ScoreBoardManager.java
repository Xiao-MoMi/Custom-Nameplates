package net.momirealms.customnameplates.scoreboard;

import net.momirealms.customnameplates.CustomNameplates;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ScoreBoardManager {

    private final CustomNameplates plugin;
    /*
    虽然会内存占用会随着玩家数量持续增加，但是这点内存根本不值一提
     */
    private final Map<String, NameplatesTeam> teams;

    /*
    该类存在的意义是判断玩家是否已经
     */
    public ScoreBoardManager(CustomNameplates plugin) {
        this.teams = new HashMap<>();
        this.plugin = plugin;
    }

    public NameplatesTeam getOrCreateTeam(Player player) {
        if (!this.teams.containsKey(player.getName())) {
            this.teams.put(player.getName(), new NameplatesTeam(this.plugin, player));
        }
        //延后一秒确保数据库完成请求
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
