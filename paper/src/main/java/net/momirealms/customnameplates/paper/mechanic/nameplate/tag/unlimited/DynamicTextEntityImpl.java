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

package net.momirealms.customnameplates.paper.mechanic.nameplate.tag.unlimited;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.RequirementManager;
import net.momirealms.customnameplates.api.mechanic.misc.ViewerText;
import net.momirealms.customnameplates.api.mechanic.tag.unlimited.DynamicTextEntity;
import net.momirealms.customnameplates.api.requirement.Condition;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.paper.mechanic.misc.PacketManager;
import net.momirealms.customnameplates.paper.util.FakeEntityUtils;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;

import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class DynamicTextEntityImpl implements DynamicTextEntity {

    private final UUID uuid = UUID.randomUUID();
    private final UnlimitedPlayer owner;
    private double yOffset;
    private final int entityId;
    private final ViewerText viewerText;
    private final Requirement[] ownerRequirements;
    private final Requirement[] viewerRequirements;
    private final Vector<Player> viewers;
    private final int refreshFrequency;
    private final int checkFrequency;
    private int checkTimer;
    private int refreshTimer;
    private boolean ownerCanShow;
    private final PacketContainer destroyPacket;

    public DynamicTextEntityImpl(
            UnlimitedPlayer unlimitedPlayer,
            ViewerText text,
            int refreshFrequency,
            int checkFrequency,
            double yOffset,
            Requirement[] ownerRequirements,
            Requirement[] viewerRequirements
    ) {
        this.entityId = FakeEntityUtils.getAndIncrease();
        this.owner = unlimitedPlayer;
        this.yOffset = yOffset;
        this.viewerText = text;
        this.ownerRequirements = ownerRequirements;
        this.viewerRequirements = viewerRequirements;
        this.checkFrequency = checkFrequency;
        this.refreshFrequency = refreshFrequency;
        this.viewers = new Vector<>();
        this.ownerCanShow = RequirementManager.isRequirementMet(new Condition(owner.getPlayer()), ownerRequirements);
        this.destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        this.destroyPacket.getIntLists().write(0, List.of(entityId));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DynamicTextEntityImpl that = (DynamicTextEntityImpl) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public boolean canShow() {
        return ownerCanShow;
    }

    @Override
    public boolean isShownTo(Player viewer) {
        return viewers.contains(viewer);
    }

    @Override
    public boolean canSee(Player viewer) {
        Condition condition = new Condition(viewer);
        return RequirementManager.isRequirementMet(condition, viewerRequirements);
    }

    @Override
    public void timer() {
        if (owner.isPreviewing()) {
            Location location = owner.getPlayer().getLocation();
            teleport(owner.getPlayer(), location.getX(), location.getY(), location.getZ(), false);
        }

        checkTimer++;
        if (checkTimer >= checkFrequency) {
            checkTimer = 0;
            if (!RequirementManager.isRequirementMet(new Condition(owner.getPlayer()), ownerRequirements)) {
                ownerCanShow = false;
                for (Player all : owner.getNearbyPlayers()) {
                    removePlayerFromViewers(all);
                }
            } else {
                ownerCanShow = true;
                for (Player all : owner.getNearbyPlayers()) {
                    if (canSee(all)) {
                        addPlayerToViewers(all);
                    } else {
                        removePlayerFromViewers(all);
                    }
                }
            }
        }

        refreshTimer++;
        if (refreshTimer >= refreshFrequency) {
            refreshTimer = 0;
            viewerText.updateForOwner();
            updateText();
        }
    }

    @Override
    public void removePlayerFromViewers(Player player) {
        if (!viewers.contains(player)) {
            return;
        }
        this.viewers.remove(player);
        destroy(player);
    }

    @Override
    public void addPlayerToViewers(Player player) {
        if (viewers.contains(player)) {
            return;
        }
        this.viewers.add(player);
        spawn(player, owner.getPlayer().getPose());
    }

    @Override
    public double getOffset() {
        return yOffset;
    }

    @Override
    public void setOffset(double offset) {
        if (yOffset == offset) return;
        yOffset = offset;
        for (Player all : viewers) {
            PacketManager.getInstance().send(all, getTeleportPacket(0));
        }
    }

    @Override
    public ViewerText getViewerText() {
        return viewerText;
    }

    @Override
    public void spawn(Player viewer, Pose pose) {
        if (viewerText.updateForViewer(viewer)) {
            PacketManager.getInstance().send(viewer, getSpawnPackets(viewerText.getLatestValue(viewer), pose));
        }
    }

    @Override
    public void spawn(Pose pose) {
        for (Player all : viewers) {
            spawn(all, pose);
        }
    }

    @Override
    public void destroy() {
        for (Player all : viewers) {
            PacketManager.getInstance().send(all, destroyPacket);
        }
        viewerText.clear();
        viewers.clear();
    }

    @Override
    public void destroy(Player viewer) {
        PacketManager.getInstance().send(viewer, destroyPacket);
        viewerText.removeViewer(viewer);
    }

    @Override
    public void teleport(double x, double y, double z, boolean onGround) {
        PacketContainer packet = getTeleportPacket(x, y, z, onGround);
        for (Player all : viewers) {
            PacketManager.getInstance().send(all, packet);
        }
    }

    @Override
    public void teleport() {
        PacketContainer packet = getTeleportPacket(getCorrection(owner.getPlayer().getPose()));
        for (Player viewer : viewers) {
            PacketManager.getInstance().send(viewer, packet);
        }
    }

    @Override
    public void teleport(Player viewer, double x, double y, double z, boolean onGround) {
        if (viewers.contains(viewer)) {
            PacketManager.getInstance().send(viewer, getTeleportPacket(x, y, z, onGround));
        }
    }

    @Override
    public void setSneak(boolean isSneaking, boolean onGround) {
        if (!onGround) {
            for (Player viewer : viewers) {
                PacketManager.getInstance().send(viewer, FakeEntityUtils.getMetaPacket(entityId, viewerText.getLatestValue(viewer), isSneaking));
            }
        }
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    @Override
    public void move(short x, short y, short z, boolean onGround) {
        PacketContainer packet = getMovePacket(x, y, z, onGround);
        for (Player viewer : viewers) {
            PacketManager.getInstance().send(viewer, packet);
        }
    }

    @Override
    public void move(Player viewer, short x, short y, short z, boolean onGround) {
        if (viewers.contains(viewer)) {
            PacketContainer packet = getMovePacket(x, y, z, onGround);
            PacketManager.getInstance().send(viewer, packet);
        }
    }

    @Override
    public void respawn(Player viewer, Pose pose) {
        destroy(viewer);
        spawn(viewer, pose);
    }

    @Override
    public void respawn(Pose pose) {
        for (Player viewer : viewers) {
            respawn(viewer, pose);
        }
    }

    @Override
    public void updateText() {
        for (Player viewer : viewers) {
            updateText(viewer);
        }
    }

    @Override
    public void updateText(Player viewer) {
        if (viewerText.updateForViewer(viewer)) {
            PacketManager.getInstance().send(viewer, FakeEntityUtils.getMetaPacket(entityId, viewerText.getLatestValue(viewer), owner.getPlayer().isSneaking()));
        }
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void handlePose(Pose previous, Pose pose) {
        // Add delay to prevent the tag from appearing earlier
        CustomNameplatesPlugin.get().getScheduler().runTaskAsyncLater(() -> {
            for (Player viewer : viewers) {
                respawn(viewer, pose);
            }
        }, 20, TimeUnit.MILLISECONDS);
    }

    public PacketContainer getMovePacket(short x, short y, short z, boolean onGround) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE);
        packet.getIntegers().write(0, entityId);
        packet.getShorts().write(0, x);
        packet.getShorts().write(1, y);
        packet.getShorts().write(2, z);
        packet.getBooleans().write(0, onGround);
        return packet;
    }

    public PacketContainer getTeleportPacket(double x, double y, double z, boolean onGround) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        packet.getIntegers().write(0, entityId);
        packet.getDoubles().write(0, x);
        packet.getDoubles().write(1, y + owner.getHatOffset() + getCorrection(owner.getPlayer().getPose()) + yOffset);
        packet.getDoubles().write(2, z);
        packet.getBooleans().write(0, onGround);
        return packet;
    }

    public PacketContainer getTeleportPacket(double correction) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        packet.getIntegers().write(0, entityId);
        Location location = getEntityLocation(correction);
        packet.getDoubles().write(0, location.getX());
        packet.getDoubles().write(1, location.getY());
        packet.getDoubles().write(2, location.getZ());
        return packet;
    }

    private Location getEntityLocation(double correction) {
        var player = owner.getPlayer();
        double x = player.getLocation().getX();
        double y = player.getLocation().getY();
        double z = player.getLocation().getZ();
        y += yOffset;
        y += owner.getHatOffset();
        y += correction;
        return new Location(null, x, y, z);
    }

    private PacketContainer[] getSpawnPackets(String text, Pose pose) {
        PacketContainer entityPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        entityPacket.getModifier().write(0, entityId);
        entityPacket.getModifier().write(1, uuid);
        entityPacket.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        Location location = getEntityLocation(getCorrection(pose));
        entityPacket.getDoubles().write(0, location.getX());
        entityPacket.getDoubles().write(1, location.getY());
        entityPacket.getDoubles().write(2, location.getZ());
        PacketContainer metaPacket = FakeEntityUtils.getMetaPacket(entityId, text, pose == Pose.SNEAKING);
        return new PacketContainer[] {entityPacket, metaPacket};
    }

    private double getCorrection(Pose pose) {
        return switch (pose) {
            case STANDING -> 1.8;
            case SNEAKING -> 1.5;
            case SLEEPING -> 0.2;
            case SWIMMING, FALL_FLYING, SPIN_ATTACK -> 0.55;
            default -> 0;
        };
    }
}