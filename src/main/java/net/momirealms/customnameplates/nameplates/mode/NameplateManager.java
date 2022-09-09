package net.momirealms.customnameplates.nameplates.mode;

import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.Function;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public abstract class NameplateManager extends Function {

    protected NameplateManager(String name) {
        super(name);
    }

    @Override
    public void load() {
    }

    @Override
    public void unload(){
    }

    public void onJoin(Player player) {
        CustomNameplates.instance.getDataManager().loadData(player);
    }

    public void onQuit(Player player) {
        CustomNameplates.instance.getDataManager().unloadPlayer(player.getUniqueId());
        if (ConfigManager.Main.tab) return;
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        String teamName = player.getName();
        Team team = scoreboard.getTeam(teamName);
        if (team != null) team.unregister();
        CustomNameplates.instance.getTeamManager().getTeams().remove(teamName);
    }

//    public void onRP(Player player, PlayerResourcePackStatusEvent.Status status) {
//        if (!ConfigManager.Nameplate.show_after) return;
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                PlayerData playerData = CustomNameplates.instance.getDataManager().getCache().get(player.getUniqueId());
//                if (playerData == null) return;
//                if (status == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
//                    playerData.setAccepted(true);
//                    SqlHandler.save(playerData, player.getUniqueId());
//                    CustomNameplates.instance.getTeamPacketManager().sendUpdateToOne(player);
//                }
//                else if (status == PlayerResourcePackStatusEvent.Status.DECLINED || status == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
//                    playerData.setAccepted(false);
//                    SqlHandler.save(playerData, player.getUniqueId());
//                    CustomNameplates.instance.getTeamPacketManager().sendUpdateToOne(player);
//                }
//            }
//        }.runTaskAsynchronously(CustomNameplates.instance);
//    }
}