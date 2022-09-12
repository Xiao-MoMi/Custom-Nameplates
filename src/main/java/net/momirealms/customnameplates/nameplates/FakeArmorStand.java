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

package net.momirealms.customnameplates.nameplates;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.objects.TextCache;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FakeArmorStand implements ArmorStand {

    private static int idCounter = 1145141919;

    private final ArmorStandManager asm;
    private final Player owner;
    private double yOffset;
    private final int entityId = idCounter++;
    private final UUID uuid = UUID.randomUUID();
    private boolean sneaking;
    private TextCache text;
    private final PacketContainer destroyPacket;
    private WrappedChatComponent wrappedChatComponent;

    public FakeArmorStand(ArmorStandManager asm, Player owner, TextCache text, double yOffset) {
        this.asm = asm;
        this.owner = owner;
        this.yOffset = yOffset;
        this.text = text;
        sneaking = owner.isSneaking();
        destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntLists().write(0, List.of(entityId));
    }

    public FakeArmorStand(ArmorStandManager asm, Player owner, WrappedChatComponent wrappedChatComponent) {
        this.asm = asm;
        this.owner = owner;
        this.yOffset = ConfigManager.Bubbles.yOffset;
        this.wrappedChatComponent = wrappedChatComponent;
        sneaking = owner.isSneaking();
        destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntLists().write(0, List.of(entityId));
    }

    @Override
    public void refresh() {
        updateMetadata();
    }

    @Override
    public double getOffset() {
        return yOffset;
    }

    @Override
    public TextCache getText() {
        return text;
    }

    @Override
    public void setOffset(double offset) {
        if (yOffset == offset) return;
        yOffset = offset;
        for (Player all : asm.getNearbyPlayers()) {
            try {
                CustomNameplates.protocolManager.sendServerPacket(all, getTeleportPacket());
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void spawn(Player viewer) {
        for (PacketContainer packet : getSpawnPackets(viewer)) {
            try {
                CustomNameplates.protocolManager.sendServerPacket(viewer, packet);
            }
            catch (InvocationTargetException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void destroy() {
        for (Player all : asm.getNearbyPlayers()) {
            try {
                CustomNameplates.protocolManager.sendServerPacket(all, destroyPacket);
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void destroy(Player viewer) {
        try {
            CustomNameplates.protocolManager.sendServerPacket(viewer, destroyPacket);
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void teleport() {
        PacketContainer packet = getTeleportPacket();
        for (Player all : asm.getNearbyPlayers()) {
            try {
                CustomNameplates.protocolManager.sendServerPacket(all, packet);
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void teleport(Player viewer) {
        if (!asm.isNearby(viewer) && viewer != owner) {
            asm.spawn(viewer);
        } else {
            try {
                CustomNameplates.protocolManager.sendServerPacket(viewer, getTeleportPacket());
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
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
    public int getEntityId() {
        return entityId;
    }

    @Override
    public void respawn(Player viewer) {
        destroy(viewer);
        spawn(viewer);
    }


    //传送包
    public PacketContainer getTeleportPacket() {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        packet.getIntegers().write(0, entityId);
        Location location = getArmorStandLocation();
        packet.getDoubles().write(0, location.getX());
        packet.getDoubles().write(1, location.getY());
        packet.getDoubles().write(2, location.getZ());
        return packet;
    }

    //更新盔甲架信息
    public void updateMetadata() {
        for (Player viewer : asm.getNearbyPlayers()) {
            PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
            metaPacket.getIntegers().write(0, entityId);
            metaPacket.getWatchableCollectionModifier().write(0, createDataWatcher(getText().getViewerText(viewer), true).getWatchableObjects());
            try {
                CustomNameplates.protocolManager.sendServerPacket(viewer, metaPacket);
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    //获取盔甲架的位置
    public Location getArmorStandLocation() {
        double x = owner.getLocation().getX();
        double y = getY() + yOffset;
        double z = owner.getLocation().getZ();
        if (!owner.isSleeping()) {
            if (sneaking) y += 1.37;
            else y += 1.8;
        }
        else y += 0.2;
        return new Location(null, x, y, z);
    }

    //获取玩家Y坐标
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

    //创建实体信息包
    public WrappedDataWatcher createDataWatcher(String text, boolean dynamic) {

        WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer serializer1 = WrappedDataWatcher.Registry.get(Boolean.class);
        WrappedDataWatcher.Serializer serializer2 = WrappedDataWatcher.Registry.get(Byte.class);
        //设置名称
        if (dynamic) {
            wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), Optional.of(WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(text))).getHandle()));
        }
        else {
            wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), Optional.of(wrappedChatComponent));
        }
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, serializer1), true);
        byte flag = 0x20; //隐身
        if (sneaking) flag += (byte) 0x02;
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, serializer2), flag);
        if (ConfigManager.Nameplate.smallSize) {
            wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, serializer2), (byte) 0x01);
        }
        //设置名字可见
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, serializer1), true);
        return wrappedDataWatcher;
    }

    //创建生成包
    public PacketContainer[] getSpawnPackets(Player viewer) {

        PacketContainer entityPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        entityPacket.getModifier().write(0, entityId);
        entityPacket.getModifier().write(1, uuid);
        entityPacket.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        Location location = getArmorStandLocation();
        entityPacket.getDoubles().write(0, location.getX());
        entityPacket.getDoubles().write(1, location.getY());
        entityPacket.getDoubles().write(2, location.getZ());

        PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        metaPacket.getIntegers().write(0, entityId);
        if (this.wrappedChatComponent == null) {
            metaPacket.getWatchableCollectionModifier().write(0, createDataWatcher(getText().getViewerText(viewer), true).getWatchableObjects());
        }
        else {
            metaPacket.getWatchableCollectionModifier().write(0, createDataWatcher("", false).getWatchableObjects());
        }
        return new PacketContainer[] {entityPacket, metaPacket};
    }
}