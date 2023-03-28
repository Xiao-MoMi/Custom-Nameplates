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

package net.momirealms.customnameplates.object.armorstand;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.Lists;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.object.DynamicText;
import net.momirealms.customnameplates.object.requirements.PlayerCondition;
import net.momirealms.customnameplates.object.requirements.Requirement;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;

import java.util.*;

public class FakeArmorStand {

    private final ArmorStandManager asm;
    private final Player owner;
    private double yOffset;
    private final int entityId;
    private final UUID uuid = UUID.randomUUID();
    private boolean sneaking;
    private DynamicText dynamicText;
    private String textJson;
    private final Requirement[] requirements;
    private boolean isShown;

    // nameplate
    public FakeArmorStand(ArmorStandManager asm, Player owner, DynamicText text, double yOffset, Requirement[] requirements) {
        this.asm = asm;
        this.entityId = new Random().nextInt(Integer.MAX_VALUE);
        this.owner = owner;
        this.yOffset = yOffset;
        this.dynamicText = text;
        this.sneaking = owner.isSneaking();
        this.requirements = requirements;
        this.isShown = false;
        this.textJson = dynamicText.getLatestJson();
    }

    // bubble
    public FakeArmorStand(ArmorStandManager asm, Player owner, String textJson, double yOffset) {
        this.asm = asm;
        this.entityId = new Random().nextInt(Integer.MAX_VALUE);
        this.owner = owner;
        this.yOffset = yOffset;
        this.textJson = textJson;
        this.sneaking = owner.isSneaking();
        this.requirements = null;
        this.isShown = true;
    }

    public boolean canShow() {
        if (requirements == null) return true;
        for (Requirement requirement : requirements) {
            if (!requirement.isConditionMet(new PlayerCondition(owner))) {
                return false;
            }
        }
        return true;
    }

    // dynamicText would not be null because bubbles would not be updated
    public void refresh() {
        textJson = dynamicText.getLatestJson();
        updateMetadata();
    }

    public double getOffset() {
        return yOffset;
    }

    public void setOffset(double offset) {
        if (yOffset == offset) return;
        yOffset = offset;
        for (Player all : asm.getNearbyPlayers()) {
            CustomNameplates.getProtocolManager().sendServerPacket(all, getTeleportPacket());
        }
    }

    public void spawn(Player viewer) {
        for (PacketContainer packet : getSpawnPackets()) {
            CustomNameplates.getProtocolManager().sendServerPacket(viewer, packet);
        }
    }

    public void spawn() {
        for (Player all : asm.getNearbyPlayers()) {
            spawn(all);
        }
        isShown = true;
    }

    public void destroy() {
        PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntLists().write(0, List.of(entityId));
        for (Player all : asm.getNearbyPlayers()) {
            CustomNameplates.getProtocolManager().sendServerPacket(all, destroyPacket);
        }
        isShown = false;
    }

    public void destroy(Player viewer) {
        PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntLists().write(0, List.of(entityId));
        CustomNameplates.getProtocolManager().sendServerPacket(viewer, destroyPacket);
    }

    public void teleport() {
        PacketContainer packet = getTeleportPacket();
        for (Player all : asm.getNearbyPlayers()) {
            CustomNameplates.getProtocolManager().sendServerPacket(all, packet);
        }
    }

    public void teleport(Player viewer) {
        if (!asm.isNearby(viewer) && viewer != owner) {
            asm.spawn(viewer);
        } else {
            CustomNameplates.getProtocolManager().sendServerPacket(viewer, getTeleportPacket());
        }
    }

    public void setSneak(boolean isSneaking, boolean respawn) {
        this.sneaking = isSneaking;
        if (respawn) {
            for (Player viewer : asm.getNearbyPlayers()) {
                respawn(viewer);
            }
        }
        else {
            refresh();
        }
    }

    public int getEntityId() {
        return entityId;
    }

    public void respawn(Player viewer) {
        destroy(viewer);
        spawn(viewer);
    }

    public PacketContainer getTeleportPacket() {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        packet.getIntegers().write(0, entityId);
        Location location = getArmorStandLocation();
        packet.getDoubles().write(0, location.getX());
        packet.getDoubles().write(1, location.getY());
        packet.getDoubles().write(2, location.getZ());
        return packet;
    }

    public void updateMetadata() {
        PacketContainer metaPacket = getMetaPacket();
        for (Player viewer : asm.getNearbyPlayers()) {
            CustomNameplates.getProtocolManager().sendServerPacket(viewer, metaPacket);
        }
    }

    protected PacketContainer getMetaPacket() {
        PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        metaPacket.getIntegers().write(0, entityId);
        if (CustomNameplates.getInstance().getVersionHelper().isVersionNewerThan1_19_R2()) {
            WrappedDataWatcher wrappedDataWatcher = createDataWatcher(textJson);
            List<WrappedDataValue> wrappedDataValueList = Lists.newArrayList();
            wrappedDataWatcher.getWatchableObjects().stream().filter(Objects::nonNull).forEach(entry -> wrappedDataValueList.add(new WrappedDataValue(entry.getWatcherObject().getIndex(), entry.getWatcherObject().getSerializer(), entry.getRawValue())));
            metaPacket.getDataValueCollectionModifier().write(0, wrappedDataValueList);
        } else {
            metaPacket.getWatchableCollectionModifier().write(0, createDataWatcher(textJson).getWatchableObjects());
        }
        return metaPacket;
    }

    public Location getArmorStandLocation() {
        double x = owner.getLocation().getX();
        double y = getY() + yOffset;
        double z = owner.getLocation().getZ();
        if (!owner.isSleeping()) {
            if (sneaking) y += 1.5;
            else y += 1.8;
        }
        else y += 0.2;
        return new Location(null, x, y, z);
    }

    protected double getY() {
        Entity vehicle = owner.getVehicle();
        if (vehicle != null) {
            if (vehicle.getType() == EntityType.HORSE) {
                return vehicle.getLocation().getY() + 0.85;
            }
            if (vehicle.getType() == EntityType.DONKEY) {
                return vehicle.getLocation().getY() + 0.525;
            }
            if (vehicle.getType() == EntityType.PIG) {
                return vehicle.getLocation().getY() + 0.325;
            }
            if (vehicle.getType() == EntityType.STRIDER) {
                return vehicle.getLocation().getY() + 1.15;
            }
        }
        if (owner.getPose() == Pose.SWIMMING || owner.isGliding()) {
            return owner.getLocation().getY() - 1.22;
        }
        return owner.getLocation().getY();
    }

    public WrappedDataWatcher createDataWatcher(String json) {
        WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer serializer1 = WrappedDataWatcher.Registry.get(Boolean.class);
        WrappedDataWatcher.Serializer serializer2 = WrappedDataWatcher.Registry.get(Byte.class);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), Optional.of(WrappedChatComponent.fromJson(json).getHandle()));
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, serializer1), true);
        byte flag = 0x20; // invisibility
        if (sneaking) flag += (byte) 0x02;
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, serializer2), flag);
        // small size
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, serializer2), (byte) 0x01);
        // visible text
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, serializer1), true);
        return wrappedDataWatcher;
    }

    public PacketContainer[] getSpawnPackets() {
        PacketContainer entityPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        entityPacket.getModifier().write(0, entityId);
        entityPacket.getModifier().write(1, uuid);
        entityPacket.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        Location location = getArmorStandLocation();
        entityPacket.getDoubles().write(0, location.getX());
        entityPacket.getDoubles().write(1, location.getY());
        entityPacket.getDoubles().write(2, location.getZ());
        PacketContainer metaPacket = getMetaPacket();
        return new PacketContainer[] {entityPacket, metaPacket};
    }

    public boolean isShown() {
        return isShown;
    }

    public DynamicText getDynamicText() {
        return dynamicText;
    }
}