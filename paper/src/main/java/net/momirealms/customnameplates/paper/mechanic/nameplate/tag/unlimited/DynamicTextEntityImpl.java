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
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.Lists;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.RequirementManager;
import net.momirealms.customnameplates.api.mechanic.misc.ViewerText;
import net.momirealms.customnameplates.api.mechanic.tag.unlimited.DynamicTextEntity;
import net.momirealms.customnameplates.api.requirement.Condition;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.paper.adventure.AdventureManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.misc.PacketManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class DynamicTextEntityImpl implements DynamicTextEntity {

    private final UUID uuid = UUID.randomUUID();
    private final UnlimitedPlayer owner;
    private double yOffset;
    private final int entityId;
    private boolean sneaking;
    private final ViewerText viewerText;
    private final Requirement[] ownerRequirements;
    private final Requirement[] viewerRequirements;
    private final Vector<Player> viewers;
    private final int refreshFrequency;
    private final int checkFrequency;
    private int checkTimer;
    private int refreshTimer;
    private boolean ownerCanShow;

    public DynamicTextEntityImpl(
            UnlimitedPlayer unlimitedPlayer,
            ViewerText text,
            int refreshFrequency,
            int checkFrequency,
            double yOffset,
            Requirement[] ownerRequirements,
            Requirement[] viewerRequirements
    ) {
        this.entityId = new Random().nextInt(Integer.MAX_VALUE);
        this.owner = unlimitedPlayer;
        this.yOffset = yOffset;
        this.viewerText = text;
        this.sneaking = unlimitedPlayer.getPlayer().isSneaking();
        this.ownerRequirements = ownerRequirements;
        this.viewerRequirements = viewerRequirements;
        this.checkFrequency = checkFrequency;
        this.refreshFrequency = refreshFrequency;
        this.viewers = new Vector<>();
        this.ownerCanShow = RequirementManager.isRequirementMet(new Condition(owner.getPlayer()), ownerRequirements);
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
        PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntLists().write(0, List.of(entityId));
        for (Player all : viewers) {
            PacketManager.getInstance().send(all, destroyPacket);
        }
        viewerText.clear();
        viewers.clear();
    }

    @Override
    public void destroy(Player viewer) {
        PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntLists().write(0, List.of(entityId));
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
    public void teleport(Player viewer, double x, double y, double z, boolean onGround) {
        if (viewers.contains(viewer)) {
            PacketManager.getInstance().send(viewer, getTeleportPacket(x, y, z, onGround));
        }
    }

    @Override
    public void setSneak(boolean isSneaking, boolean onGround) {
        this.sneaking = isSneaking;
        if (!onGround) {
            for (Player viewer : viewers) {
                PacketManager.getInstance().send(viewer, getMetaPacket(viewerText.getLatestValue(viewer)));
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
            PacketManager.getInstance().send(viewer, getMetaPacket(viewerText.getLatestValue(viewer)));
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

    protected PacketContainer getMetaPacket(String text) {
        PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        metaPacket.getIntegers().write(0, entityId);
        String json = AdventureManagerImpl.getInstance().componentToJson(AdventureManagerImpl.getInstance().getComponentFromMiniMessage(text));
        if (CustomNameplatesPlugin.getInstance().getVersionManager().isVersionNewerThan1_19_R2()) {
            WrappedDataWatcher wrappedDataWatcher = createArmorStandDataWatcher(json);
            List<WrappedDataValue> wrappedDataValueList = Lists.newArrayList();
            wrappedDataWatcher.getWatchableObjects().stream().filter(Objects::nonNull).forEach(entry -> wrappedDataValueList.add(new WrappedDataValue(entry.getWatcherObject().getIndex(), entry.getWatcherObject().getSerializer(), entry.getRawValue())));
            metaPacket.getDataValueCollectionModifier().write(0, wrappedDataValueList);
        } else {
            metaPacket.getWatchableCollectionModifier().write(0, createArmorStandDataWatcher(json).getWatchableObjects());
        }
        return metaPacket;
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

    private WrappedDataWatcher createArmorStandDataWatcher(String json) {
        WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer serializer1 = WrappedDataWatcher.Registry.get(Boolean.class);
        WrappedDataWatcher.Serializer serializer2 = WrappedDataWatcher.Registry.get(Byte.class);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), Optional.of(WrappedChatComponent.fromJson(json).getHandle()));
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, serializer1), true);
        byte flag = 0x20;
        if (sneaking) flag += (byte) 0x02;
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, serializer2), flag);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, serializer2), (byte) 0x01);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, serializer1), true);
        return wrappedDataWatcher;
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
        PacketContainer metaPacket = getMetaPacket(text);
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