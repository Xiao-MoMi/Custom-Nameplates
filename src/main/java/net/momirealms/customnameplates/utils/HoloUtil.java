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
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customnameplates.CustomNameplates;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

public class HoloUtil {

    public static HashMap<Player, Integer> cache = new HashMap<>();

    public static void showHolo(Component component, Player player, int duration){

        if (cache.get(player) != null){
            removeHolo(player, cache.get(player));
        }

        PacketContainer packet1 = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);

        int id = new Random().nextInt(1000000000);
        packet1.getModifier().write(0, id);
        packet1.getModifier().write(1, UUID.randomUUID());
        packet1.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        Location location = player.getLocation();
        packet1.getDoubles().write(0, location.getX());
        packet1.getDoubles().write(1, location.getY()+0.8);
        packet1.getDoubles().write(2, location.getZ());

        PacketContainer packet2 = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

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
        packet2.getModifier().write(0,id);
        packet2.getWatchableCollectionModifier().write(0, wrappedDataWatcher.getWatchableObjects());
        cache.put(player, id);

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet1);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet2);
        }
        catch (Exception e) {
            AdventureUtil.consoleMessage("<red>[CustomNameplates] 无法为玩家 "+ player.getName()+" 展示悬浮信息!</red>");
            e.printStackTrace();
        }

        for (int i = 1; i < duration * 20 - 1; i++){
            Bukkit.getScheduler().runTaskLater(CustomNameplates.plugin, ()->{
                updatePlace(player, id);
            }, i);
        }

        Bukkit.getScheduler().runTaskLater(CustomNameplates.plugin, ()->{
            removeHolo(player, id);
            cache.remove(location);
        }, duration * 20L);
    }

    public static void updatePlace(Player player, int entityId){
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        packet.getIntegers().write(0, entityId);
        Location location = player.getLocation();
        packet.getDoubles().write(0, location.getX());
        packet.getDoubles().write(1, location.getY()+0.8);
        packet.getDoubles().write(2, location.getZ());
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        }
        catch (Exception e) {
            AdventureUtil.consoleMessage("<red>[CustomNameplates] 无法为玩家 "+ player.getName()+" 更新悬浮信息!</red>");
            e.printStackTrace();
        }
    }

    public static void removeHolo(Player player, int entityId){
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        packet.getIntLists().write(0, List.of(entityId));
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        }
        catch (Exception e) {
            AdventureUtil.consoleMessage("<red>[CustomNameplates] 无法为玩家 "+ player.getName()+" 移除悬浮信息!</red>");
            e.printStackTrace();
        }
    }
}
