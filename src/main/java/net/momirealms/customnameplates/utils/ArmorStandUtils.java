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

package net.momirealms.customnameplates.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customnameplates.CustomNameplates;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

public class ArmorStandUtils {

    public static void preview(Component component, Player player, int duration) {
        int id = new Random().nextInt(Integer.MAX_VALUE);
        sendSpawnPacket(player, id);
        sendMetaPacket(player, id, component);
        for (int i = 1; i < duration * 20 - 1; i++){
            Bukkit.getScheduler().runTaskLater(CustomNameplates.getInstance(), ()-> sendTeleportPacket(player, id), i);
        }
        Bukkit.getScheduler().runTaskLater(CustomNameplates.getInstance(), ()-> sendDestroyPacket(player, id), duration * 20L);
    }

    public static void sendSpawnPacket(Player player, int id) {
        PacketContainer spawnPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        spawnPacket.getModifier().write(0, id);
        spawnPacket.getModifier().write(1, UUID.randomUUID());
        spawnPacket.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        Location location = player.getLocation();
        spawnPacket.getDoubles().write(0, location.getX());
        spawnPacket.getDoubles().write(1, location.getY()+0.8);
        spawnPacket.getDoubles().write(2, location.getZ());
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, spawnPacket);
    }

    public static void sendMetaPacket(Player player, int id, Component component) {
        PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer serializer1 = WrappedDataWatcher.Registry.get(Boolean.class);
        WrappedDataWatcher.Serializer serializer2 = WrappedDataWatcher.Registry.get(Byte.class);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), Optional.of(WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(component)).getHandle()));
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, serializer1), true);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, serializer1), true);
        byte mask1 = 0x20;
        byte mask2 = 0x01;
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, serializer2), mask1);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, serializer2), mask2);
        metaPacket.getModifier().write(0, id);
        if (CustomNameplates.getInstance().getVersionHelper().isVersionNewerThan1_19_R2()) {
            List<WrappedDataValue> wrappedDataValueList = Lists.newArrayList();
            wrappedDataWatcher.getWatchableObjects().stream().filter(Objects::nonNull).forEach(entry -> {
                final WrappedDataWatcher.WrappedDataWatcherObject dataWatcherObject = entry.getWatcherObject();
                wrappedDataValueList.add(new WrappedDataValue(dataWatcherObject.getIndex(), dataWatcherObject.getSerializer(), entry.getRawValue()));
            });
            metaPacket.getDataValueCollectionModifier().write(0, wrappedDataValueList);
        } else {
            metaPacket.getWatchableCollectionModifier().write(0, wrappedDataWatcher.getWatchableObjects());
        }
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, metaPacket);
    }

    public static void sendTeleportPacket(Player player, int id){
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        packet.getIntegers().write(0, id);
        Location location = player.getLocation();
        packet.getDoubles().write(0, location.getX());
        packet.getDoubles().write(1, location.getY()+0.8);
        packet.getDoubles().write(2, location.getZ());
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }

    public static void sendDestroyPacket(Player player, int id) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        packet.getIntLists().write(0, List.of(id));
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }
}
