package net.momirealms.customnameplates.paper.mechanic.nameplate.tag.team;

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.TeamTagManager;
import net.momirealms.customnameplates.api.mechanic.misc.ViewerText;
import net.momirealms.customnameplates.api.mechanic.tag.team.TeamTagPlayer;
import net.momirealms.customnameplates.common.team.TeamColor;
import net.momirealms.customnameplates.common.team.TeamTagVisibility;
import org.bukkit.entity.Player;

import java.util.Vector;

public class TeamPlayer implements TeamTagPlayer {

    private final TeamTagManager manager;
    private final Player owner;
    private ViewerText prefix;
    private ViewerText suffix;
    private final Vector<Player> nearbyPlayers;
    private boolean isPreviewing;

    public TeamPlayer(TeamTagManager manager, Player owner, String prefix, String suffix) {
        this.manager = manager;
        this.owner = owner;
        this.prefix = new ViewerText(owner, prefix);
        this.suffix = new ViewerText(owner, suffix);
        this.nearbyPlayers = new Vector<>();
        this.prefix.updateForOwner();
        this.suffix.updateForOwner();
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = new ViewerText(owner, prefix);
    }

    @Override
    public void setSuffix(String suffix) {
        this.suffix = new ViewerText(owner, suffix);
    }

    public void updateForNearbyPlayers(boolean force) {
        this.prefix.updateForOwner();
        this.suffix.updateForOwner();
        for (Player viewer : nearbyPlayers) {
            updateForOne(viewer, force);
        }
    }

    public void removeNearbyPlayer(Player player) {
        if (!nearbyPlayers.contains(player)) {
            return;
        }
        nearbyPlayers.remove(player);
        removeForOne(player);
        prefix.removeViewer(player);
        suffix.removeViewer(player);
    }

    public void addNearbyPlayer(Player player) {
        if (nearbyPlayers.contains(player)) {
            return;
        }
        nearbyPlayers.add(player);
        updateForOne(player, false);
    }

    @Override
    public void destroy() {
        manager.removeTeamPlayerFromMap(owner.getUniqueId());
        for (Player viewer : nearbyPlayers) {
            removeForOne(viewer);
        }
        nearbyPlayers.clear();
    }

    private void updateForOne(Player viewer, boolean force) {
        try {
            if ((prefix.updateForViewer(viewer) | suffix.updateForViewer(viewer)) || force) {
                CustomNameplatesPlugin.get().getTeamManager().updateTeam(
                        owner,
                        viewer,
                        prefix.getLatestValue(viewer),
                        suffix.getLatestValue(viewer),
                        CustomNameplatesPlugin.get().getNameplateManager().getTeamColor(owner),
                        TeamTagVisibility.ALWAYS
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeForOne(Player viewer) {
        CustomNameplatesPlugin.get().getTeamManager().updateTeam(
                owner,
                viewer,
                "",
                "",
                TeamColor.WHITE,
                TeamTagVisibility.ALWAYS
        );
    }

    @Override
    public void setPreview(boolean preview) {
        if (isPreviewing == preview) {
            return;
        }
        isPreviewing = preview;
        if (isPreviewing) {

        } else {

        }
    }

    @Override
    public boolean isPreviewing() {
        return isPreviewing;
    }

    @Override
    public Player getPlayer() {
        return owner;
    }
}
