package net.momirealms.customnameplates.nameplates.mode.tm;

import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.nameplates.TeamManager;
import net.momirealms.customnameplates.nameplates.mode.NameplateManager;
import net.momirealms.customnameplates.nameplates.NameplatesTeam;
import net.momirealms.customnameplates.nameplates.mode.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class TeamTag extends NameplateManager {

    private EventListener listener;

    private final HashMap<Player, BukkitTask> taskCache;

    public TeamTag(String name) {
        super(name);
        this.taskCache = new HashMap<>();
    }

    @Override
    public void load() {
        listener = new EventListener(this);
        Bukkit.getPluginManager().registerEvents(listener, CustomNameplates.instance);
        for (Player player : Bukkit.getOnlinePlayers()) {
            CustomNameplates.instance.getTeamPacketManager().sendUpdateToAll(player);
            CustomNameplates.instance.getTeamPacketManager().sendUpdateToOne(player);
        }
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(listener);
        taskCache.clear();
        listener = null;
    }

    @Override
    public void onJoin(Player player) {

        super.onJoin(player);

        if (!ConfigManager.Nameplate.update) return;

        startRefresh(player);
    }

    private void startRefresh(Player player) {

        if (!player.isOnline()) return;

        String teamName = TeamManager.getTeamName(player);
        NameplatesTeam nameplatesTeam = CustomNameplates.instance.getTeamManager().getTeams().get(teamName);

        if (nameplatesTeam != null) {
            BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(CustomNameplates.instance, () -> {
                nameplatesTeam.updateNameplates();
                CustomNameplates.instance.getTeamPacketManager().sendUpdateToAll(player);
            }, 20, ConfigManager.Nameplate.refresh);
            taskCache.put(player, task);
        }
        else {
            Bukkit.getScheduler().runTaskLater(CustomNameplates.instance, () -> {
                startRefresh(player);
            }, 20);
        }
    }

    @Override
    public void onQuit(Player player) {

        super.onQuit(player);

        BukkitTask bukkitTask = taskCache.remove(player);
        if (bukkitTask != null) bukkitTask.cancel();

    }

//    @Override
//    public void onRP(Player player, PlayerResourcePackStatusEvent.Status status) {
//        super.onRP(player, status);
//    }
}
