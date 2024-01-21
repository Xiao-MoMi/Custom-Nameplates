package net.momirealms.customnameplates.paper.mechanic.nameplate.tag.unlimited;

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.mechanic.tag.unlimited.DynamicTextEntity;
import net.momirealms.customnameplates.api.mechanic.tag.unlimited.DynamicTextTagSetting;
import net.momirealms.customnameplates.api.mechanic.tag.unlimited.EntityTagPlayer;
import net.momirealms.customnameplates.common.team.TeamColor;
import net.momirealms.customnameplates.common.team.TeamTagVisibility;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;

import java.util.Vector;

public class UnlimitedPlayer extends UnlimitedObject implements EntityTagPlayer {

    private final Player owner;
    private final Vector<DynamicTextEntity> tags;
    private double hatOffset;
    private boolean isPreviewing;

    public UnlimitedPlayer(UnlimitedTagManagerImpl manager, Player player) {
        super(manager, player);
        this.owner = player;
        this.tags = new Vector<>();
    }

    @Override
    public void addTag(DynamicTextEntity tag) {
        if (tags.contains(tag)) {
            return;
        }
        tags.add(tag);
        for (Player all : nearbyPlayers) {
            if (tag.canShow() && tag.canSee(all)) {
                tag.addPlayerToViewers(all);
            }
        }
    }

    @Override
    public DynamicTextEntity addTag(DynamicTextTagSetting setting) {
        var tag = manager.createNamedEntity(this, setting);
        addTag(tag);
        return tag;
    }

    @Override
    public void removeTag(DynamicTextEntity tag) {
        if (tags.remove(tag)) {
            tag.destroy();
        }
    }

    @Override
    public Vector<DynamicTextEntity> getTags() {
        return tags;
    }

    @Override
    public void setHatOffset(double hatOffset) {
        this.hatOffset = hatOffset;
    }

    @Override
    public void setPreview(boolean preview) {
        if (isPreviewing == preview) {
            return;
        }
        isPreviewing = preview;
        if (isPreviewing) {
            addNearbyPlayerNaturally(owner);
        } else {
            removeNearbyPlayerNaturally(owner);
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
        for (DynamicTextEntity tag : tags) {
            tag.updateText();
        }
    }

    @Override
    public double getHatOffset() {
        return hatOffset;
    }

    @Override
    public void addNearbyPlayerNaturally(Player player) {
        if (nearbyPlayers.contains(player)) {
            return;
        }
        nearbyPlayers.add(player);
        setNameInvisibleFor(player);
        for (DynamicTextEntity tag : tags) {
            if (tag.canShow() && tag.canSee(player)) {
                tag.addPlayerToViewers(player);
            }
        }
    }

    @Override
    public void removeNearbyPlayerNaturally(Player player) {
        if (!nearbyPlayers.contains(player)) {
            return;
        }
        super.nearbyPlayers.remove(player);
        setNameVisibleFor(player);
        for (DynamicTextEntity tag : tags) {
            tag.removePlayerFromViewers(player);
        }
    }

    @Override
    public void destroy() {
        manager.removeUnlimitedObjectFromMap(entity.getUniqueId());
        for (Player viewer : nearbyPlayers) {
            setNameVisibleFor(viewer);
        }
        for (DynamicTextEntity tag : tags) {
            tag.destroy();
        }
        nearbyPlayers.clear();
        tags.clear();
    }

    public void sneak(boolean sneaking, boolean flying) {
        for (DynamicTextEntity tag : tags) {
            tag.setSneak(sneaking, !flying);
        }
    }

    @Override
    public void move(Player receiver, short x, short y, short z, boolean onGround) {
        for (DynamicTextEntity tag : tags) {
            tag.move(receiver, x, y, z, onGround);
        }
    }

    @Override
    public void teleport(Player receiver, double x, double y, double z, boolean onGround) {
        for (DynamicTextEntity tag : tags) {
            tag.teleport(receiver, x, y, z, onGround);
        }
    }

    @Override
    public void handlePose(Pose previous, Pose pose) {
        for (DynamicTextEntity tag : tags) {
            tag.handlePose(previous, pose);
        }
    }

    private void setNameInvisibleFor(Player viewer) {
        CustomNameplatesPlugin.get().getTeamManager().updateTeam(
                owner,
                viewer,
                "",
               "",
                TeamColor.WHITE,
                TeamTagVisibility.NEVER
        );
    }

    private void setNameVisibleFor(Player viewer) {
        CustomNameplatesPlugin.get().getTeamManager().updateTeam(
                owner,
                viewer,
                "",
               "",
                TeamColor.WHITE,
                TeamTagVisibility.ALWAYS
        );
    }

    public void timer() {
        for (DynamicTextEntity tag : tags) {
            tag.timer();
        }
    }
}
