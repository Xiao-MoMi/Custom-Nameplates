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

package net.momirealms.customnameplates.object.carrier;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.Lists;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.object.DisplayMode;
import net.momirealms.customnameplates.object.DynamicText;
import net.momirealms.customnameplates.object.requirements.Requirement;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class NamedEntityImpl implements NamedEntity {

    private final NamedEntityManager asm;
    private final Player owner;
    private double yOffset;
    private final int entityId;
    private final UUID uuid = UUID.randomUUID();
    private boolean sneaking;
    private DynamicText dynamicText;
    private String textJson;
    private final Requirement[] requirements;
    private boolean isShown;
    private final TextDisplayMeta textDisplayMeta;

    //dynamic value
    public NamedEntityImpl(NamedEntityManager asm, Player owner, DynamicText text, double yOffset, @NotNull Requirement[] requirements, @Nullable TextDisplayMeta textDisplayMeta) {
        this.asm = asm;
        this.entityId = new Random().nextInt(Integer.MAX_VALUE);
        this.owner = owner;
        this.yOffset = yOffset;
        this.dynamicText = text;
        this.sneaking = owner.isSneaking();
        this.requirements = requirements;
        this.isShown = false;
        this.textJson = dynamicText.getLatestJson();
        this.textDisplayMeta = textDisplayMeta;
    }

    //constant value
    public NamedEntityImpl(NamedEntityManager asm, Player owner, String textJson, double yOffset, @Nullable TextDisplayMeta textDisplayMeta) {
        this.asm = asm;
        this.entityId = new Random().nextInt(Integer.MAX_VALUE);
        this.owner = owner;
        this.yOffset = yOffset;
        this.textJson = textJson;
        this.sneaking = owner.isSneaking();
        this.requirements = null;
        this.isShown = true;
        this.textDisplayMeta = textDisplayMeta;
    }

    @Override
    public boolean canShow() {
        if (requirements == null) return true;
        for (Requirement requirement : requirements) {
            if (!requirement.isConditionMet(owner)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void refresh() {
        if (dynamicText == null) return;
        textJson = dynamicText.getLatestJson();
        updateMetadata();
    }

    @Override
    public double getOffset() {
        return yOffset;
    }

    @Override
    public void setOffset(double offset) {
        if (yOffset == offset) return;
        yOffset = offset;
        for (Player all : asm.getNearbyPlayers()) {
            CustomNameplates.getProtocolManager().sendServerPacket(all, getTeleportPacket());
        }
    }

    @Override
    public void spawn(Player viewer) {
        for (PacketContainer packet : getSpawnPackets()) {
            CustomNameplates.getProtocolManager().sendServerPacket(viewer, packet);
        }
    }

    @Override
    public void spawn() {
        for (Player all : asm.getNearbyPlayers()) {
            spawn(all);
        }
        isShown = true;
    }

    @Override
    public void destroy() {
        PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntLists().write(0, List.of(entityId));
        for (Player all : asm.getNearbyPlayers()) {
            CustomNameplates.getProtocolManager().sendServerPacket(all, destroyPacket);
        }
        isShown = false;
    }

    @Override
    public void destroy(Player viewer) {
        PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntLists().write(0, List.of(entityId));
        CustomNameplates.getProtocolManager().sendServerPacket(viewer, destroyPacket);
    }

    @Override
    public void teleport() {
        PacketContainer packet = getTeleportPacket();
        for (Player all : asm.getNearbyPlayers()) {
            CustomNameplates.getProtocolManager().sendServerPacket(all, packet);
        }
    }

    @Override
    public void teleport(Player viewer) {
        if (!asm.isNearby(viewer) && viewer != owner) {
            asm.spawn(viewer);
        } else {
            CustomNameplates.getProtocolManager().sendServerPacket(viewer, getTeleportPacket());
        }
    }

    @Override
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

    @Override
    public boolean isShown() {
        return isShown;
    }

    @Override
    public DynamicText getDynamicText() {
        return dynamicText;
    }

    @Override
    public int getEntityId() {
        return entityId;
    }

    @Override
    public void respawn(Player viewer) {
        destroy(viewer);
        spawn(viewer);
    }

    public PacketContainer getTeleportPacket() {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        packet.getIntegers().write(0, entityId);
        Location location = getEntityLocation();
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
            WrappedDataWatcher wrappedDataWatcher =
                    asm.getDisplayMode() == DisplayMode.ARMOR_STAND ?
                    createArmorStandDataWatcher(textJson) : createTextDisplayDataWatcher(textJson, textDisplayMeta);
            List<WrappedDataValue> wrappedDataValueList = Lists.newArrayList();
            wrappedDataWatcher.getWatchableObjects().stream().filter(Objects::nonNull).forEach(entry -> wrappedDataValueList.add(new WrappedDataValue(entry.getWatcherObject().getIndex(), entry.getWatcherObject().getSerializer(), entry.getRawValue())));
            metaPacket.getDataValueCollectionModifier().write(0, wrappedDataValueList);
        } else {
            metaPacket.getWatchableCollectionModifier().write(0, createArmorStandDataWatcher(textJson).getWatchableObjects());
        }
        return metaPacket;
    }

    private Location getEntityLocation() {
        double x = owner.getLocation().getX();
        double y = getY() + yOffset;
        double z = owner.getLocation().getZ();
        if (!owner.isSleeping()) {
            if (sneaking) y += 1.5;
            else y += 1.8;
        }
        else y += 0.2;
        y += asm.getHatOffset();
        return new Location(null, x, y, z);
    }

    private double getY() {
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

    private WrappedDataWatcher createArmorStandDataWatcher(String json) {
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

    private WrappedDataWatcher createTextDisplayDataWatcher(String textJson, TextDisplayMeta textDisplayMeta) {
        WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(22, WrappedDataWatcher.Registry.getChatComponentSerializer(false)), WrappedChatComponent.fromJson(textJson));
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(24, WrappedDataWatcher.Registry.get(Integer.class)), textDisplayMeta.backgroundColor());
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(14, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 3);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(25, WrappedDataWatcher.Registry.get(Byte.class)), textDisplayMeta.opacity());
        int mask = 0;
        if (textDisplayMeta.hasShadow()) mask += 1;
        if (textDisplayMeta.isSeeThrough()) mask += 2;
        if (textDisplayMeta.useDefaultBackground()) mask += 4;
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(26, WrappedDataWatcher.Registry.get(Byte.class)), (byte) mask);
        return wrappedDataWatcher;
    }

    private PacketContainer[] getSpawnPackets() {
        PacketContainer entityPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        entityPacket.getModifier().write(0, entityId);
        entityPacket.getModifier().write(1, uuid);
        entityPacket.getEntityTypeModifier().write(0, asm.getDisplayMode() == DisplayMode.ARMOR_STAND ? EntityType.ARMOR_STAND : EntityType.TEXT_DISPLAY);
        Location location = getEntityLocation();
        entityPacket.getDoubles().write(0, location.getX());
        entityPacket.getDoubles().write(1, location.getY());
        entityPacket.getDoubles().write(2, location.getZ());
        PacketContainer metaPacket = getMetaPacket();
        return new PacketContainer[] {entityPacket, metaPacket};
    }
}