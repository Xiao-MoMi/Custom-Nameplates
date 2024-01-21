package net.momirealms.customnameplates.api.mechanic.tag.unlimited;

import net.kyori.adventure.text.Component;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.UnlimitedTagManager;
import net.momirealms.customnameplates.api.mechanic.nameplate.Nameplate;
import net.momirealms.customnameplates.api.mechanic.tag.NameplatePlayer;
import net.momirealms.customnameplates.common.team.TeamColor;
import net.momirealms.customnameplates.common.team.TeamTagVisibility;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;

import java.util.Vector;

public class UnlimitedPlayer extends UnlimitedObject implements NameplatePlayer {

    private final Player owner;
    private final Vector<NamedEntity> tags;
    private double hatOffset;

    public UnlimitedPlayer(UnlimitedTagManager manager, Player player) {
        super(manager, player);
        this.owner = player;
        this.tags = new Vector<>();
    }

    /**
     * Add a tag for a player
     *
     * @param tag tag
     */
    public void addTag(NamedEntity tag) {
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

    /**
     * Remove a tag for a player
     *
     * @param tag tag
     */
    public void removeTag(NamedEntity tag) {
        if (tags.remove(tag)) {
            tag.destroy();
        }
    }

    /**
     * Get the tags that player own
     *
     * @return tags
     */
    public Vector<NamedEntity> getTags() {
        return tags;
    }

    /**
     * Set hat offset. This is useful for cosmetics plugins
     * because hat might hide the name tags
     *
     * @param hatOffset hat offset
     */
    public void setHatOffset(double hatOffset) {
        this.hatOffset = hatOffset;
    }

    @Override
    public void preview() {

    }

    @Override
    public void preview(Nameplate nameplate) {

    }

    /**
     * Get the owner
     */
    @Override
    public Player getOwner() {
        return owner;
    }

    /**
     * Get the hat offset
     */
    public double getHatOffset() {
        return hatOffset;
    }

    /**
     * Add a nearby player so he could see the tag
     * This process is automatically handled by CustomNameplates
     *
     * @param player player
     */
    @Override
    public void addNearbyPlayer(Player player) {
        if (nearbyPlayers.contains(player)) {
            return;
        }
        nearbyPlayers.add(player);
        addForOne(player);
        for (NamedEntity tag : tags) {
            if (tag.canShow() && tag.canSee(player)) {
                tag.addPlayerToViewers(player);
            }
        }
    }

    /**
     * Remove a nearby player so he would no longer receive tag updates
     * This process is automatically handled by CustomNameplates
     *
     * @param player player
     */
    @Override
    public void removeNearbyPlayer(Player player) {
        if (!nearbyPlayers.contains(player)) {
            return;
        }
        super.nearbyPlayers.remove(player);
        removeForOne(player);
        for (NamedEntity tag : tags) {
            tag.removePlayerFromViewers(player);
        }
    }

    @Override
    public void destroy() {
        manager.removeUnlimitedObjectFromMap(entity.getUniqueId());
        for (Player viewer : nearbyPlayers) {
            removeForOne(viewer);
        }
        for (NamedEntity tag : tags) {
            tag.destroy();
        }
        nearbyPlayers.clear();
        tags.clear();
    }

    public void move(Player receiver, short x, short y, short z, boolean onGround) {
        for (NamedEntity tag : tags) {
            tag.move(receiver, x, y, z, onGround);
        }
    }

    public void teleport(Player receiver, double x, double y, double z, boolean onGround) {
        for (NamedEntity tag : tags) {
            tag.teleport(receiver, x, y, z, onGround);
        }
    }

    public void sneak(boolean sneaking, boolean flying) {
        for (NamedEntity tag : tags) {
            tag.setSneak(sneaking, !flying);
        }
    }

    public void handlePose(Pose previous, Pose pose) {
        for (NamedEntity tag : tags) {
            tag.handlePose(previous, pose);
        }
    }

    private void addForOne(Player viewer) {
        CustomNameplatesPlugin.get().getTeamManager().updateTeam(
                owner,
                viewer,
                Component.text(""),
                Component.text(""),
                TeamColor.WHITE,
                TeamTagVisibility.NEVER
        );
    }

    private void removeForOne(Player viewer) {
        CustomNameplatesPlugin.get().getTeamManager().updateTeam(
                owner,
                viewer,
                Component.text(""),
                Component.text(""),
                TeamColor.WHITE,
                TeamTagVisibility.ALWAYS
        );
    }

    public void timer() {
        for (NamedEntity tag : tags) {
            tag.timer();
        }
    }
}
