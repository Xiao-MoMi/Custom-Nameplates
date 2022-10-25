package net.momirealms.customnameplates.objects.nameplates;

import net.momirealms.customnameplates.manager.ConfigManager;
import net.momirealms.customnameplates.manager.NameplateManager;
import net.momirealms.customnameplates.manager.TeamManager;
import net.momirealms.customnameplates.objects.Function;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public abstract class NameplateMode extends Function {

    protected BukkitTask refreshTask;
    protected TeamManager teamManager;

    public NameplateMode(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public void load() {
        loadToAllPlayers();
        arrangeRefreshTask();
    }

    @Override
    public void unload() {
        if (refreshTask != null) refreshTask.cancel();
    }

    public void onJoin(Player player) {

    }

    public void onQuit(Player player) {
        if (teamManager != null) {
            teamManager.removePlayerFromTeamCache(player);
            if (NameplateManager.fakeTeam) {
                if (ConfigManager.tab_BC_hook || ConfigManager.tab_hook) return;
                teamManager.getTeamManagePacketUtil().destroyTeamToAll(player);
            }
            else {
                Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
                Team team = scoreboard.getTeam(player.getName());
                if (team != null) team.unregister();
            }
        }
    }

    public void arrangeRefreshTask() {
        //empty
    }

    public void loadToAllPlayers() {
        //empty
    }
}
