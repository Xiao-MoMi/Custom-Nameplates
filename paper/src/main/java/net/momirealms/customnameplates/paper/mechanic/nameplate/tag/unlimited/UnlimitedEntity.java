package net.momirealms.customnameplates.paper.mechanic.nameplate.tag.unlimited;

import net.momirealms.customnameplates.api.mechanic.tag.unlimited.EntityTagEntity;
import net.momirealms.customnameplates.api.mechanic.tag.unlimited.StaticTextEntity;
import net.momirealms.customnameplates.api.mechanic.tag.unlimited.StaticTextTagSetting;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;

import java.util.Vector;

public class UnlimitedEntity extends UnlimitedObject implements EntityTagEntity {

    private final Vector<StaticTextEntity> tags;

    public UnlimitedEntity(UnlimitedTagManagerImpl manager, Entity entity) {
        super(manager, entity);
        this.tags = new Vector<>();
    }

    @Override
    public void addTag(StaticTextEntity tag) {
        if (tags.contains(tag)) {
            return;
        }
        tags.add(tag);
        for (Player all : nearbyPlayers) {
            tag.addPlayerToViewers(all);
        }
    }

    @Override
    public StaticTextEntity addTag(StaticTextTagSetting setting) {
        var tag = manager.createNamedEntity(this, setting);
        addTag(tag);
        return tag;
    }

    @Override
    public void removeTag(StaticTextEntity tag) {
        if (tags.remove(tag)) {
            tag.destroy();
        }
    }

    @Override
    public Vector<StaticTextEntity> getTags() {
        return tags;
    }

    @Override
    public void forceAddNearbyPlayer(Player player) {
        if (nearbyPlayers.contains(player)) {
            return;
        }
        nearbyPlayers.add(player);
        for (StaticTextEntity tag : tags) {
            tag.addPlayerToViewers(player);
        }
    }

    @Override
    public void forceRemoveNearbyPlayer(Player player) {
        if (!nearbyPlayers.contains(player)) {
            return;
        }
        super.nearbyPlayers.remove(player);
        for (StaticTextEntity tag : tags) {
            tag.removePlayerFromViewers(player);
        }
    }

    @Override
    public void addNearbyPlayerNaturally(Player player) {
        if (nearbyPlayers.contains(player)) {
            return;
        }
        nearbyPlayers.add(player);
        for (StaticTextEntity tag : tags) {
            if (tag.getComeRule().isPassed(player, entity)) {
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
        for (StaticTextEntity tag : tags) {
            if (tag.getLeaveRule().isPassed(player, entity)) {
                tag.removePlayerFromViewers(player);
            }
        }
    }

    @Override
    public void move(Player receiver, short x, short y, short z, boolean onGround) {
        for (StaticTextEntity tag : tags) {
            tag.move(receiver, x, y, z, onGround);
        }
    }

    @Override
    public void teleport(Player receiver, double x, double y, double z, boolean onGround) {
        for (StaticTextEntity tag : tags) {
            tag.teleport(receiver, x, y, z, onGround);
        }
    }

    @Override
    public void handlePose(Pose previous, Pose pose) {
        for (StaticTextEntity tag : tags) {
            tag.handlePose(previous, pose);
        }
    }

    @Override
    public void destroy() {
        manager.removeUnlimitedObjectFromMap(entity.getUniqueId());
        for (StaticTextEntity tag : tags) {
            tag.destroy();
        }
        nearbyPlayers.clear();
        tags.clear();
    }
}
