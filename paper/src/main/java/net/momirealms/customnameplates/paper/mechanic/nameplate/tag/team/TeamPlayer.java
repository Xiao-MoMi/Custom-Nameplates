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
    private final TeamPreviewSimpleEntity previewEntity;

    public TeamPlayer(TeamTagManager manager, Player owner, String prefix, String suffix) {
        this.manager = manager;
        this.owner = owner;
        this.prefix = new ViewerText(owner, prefix);
        this.suffix = new ViewerText(owner, suffix);
        this.nearbyPlayers = new Vector<>();
        this.prefix.updateForOwner();
        this.suffix.updateForOwner();
        this.previewEntity = new TeamPreviewSimpleEntity(this);
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = new ViewerText(owner, prefix);
    }

    @Override
    public void setSuffix(String suffix) {
        this.suffix = new ViewerText(owner, suffix);
    }

    @Override
    public ViewerText getPrefix() {
        return prefix;
    }

    @Override
    public ViewerText getSuffix() {
        return suffix;
    }

    public void updateForNearbyPlayers(boolean force) {
        this.prefix.updateForOwner();
        this.suffix.updateForOwner();
        for (Player viewer : nearbyPlayers) {
            updateForOne(viewer, force);
        }
        if (isPreviewing) {
            previewEntity.update();
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
        previewEntity.destroy();
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
            this.previewEntity.spawn();
        } else {
            this.previewEntity.destroy();
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

    @Override
    public void updateText() {
        updateForNearbyPlayers(false);
        if (isPreviewing) {
            previewEntity.update();
        }
    }
}
