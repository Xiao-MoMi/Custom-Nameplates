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

package net.momirealms.customnameplates.paper.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.Lists;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.paper.adventure.AdventureManagerImpl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FakeEntityUtils {

    private static int entityID = 114514520;

    public static int getAndIncrease() {
        return entityID++;
    }

    public static PacketContainer getMetaPacket(int entityID, String text, boolean sneak) {
        PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        metaPacket.getIntegers().write(0, entityID);
        if (CustomNameplatesPlugin.getInstance().getVersionManager().isVersionNewerThan1_19_R2()) {
            WrappedDataWatcher wrappedDataWatcher = createArmorStandDataWatcher(text, sneak);
            List<WrappedDataValue> wrappedDataValueList = Lists.newArrayList();
            wrappedDataWatcher.getWatchableObjects().stream().filter(Objects::nonNull).forEach(entry -> wrappedDataValueList.add(new WrappedDataValue(entry.getWatcherObject().getIndex(), entry.getWatcherObject().getSerializer(), entry.getRawValue())));
            metaPacket.getDataValueCollectionModifier().write(0, wrappedDataValueList);
        } else {
            metaPacket.getWatchableCollectionModifier().write(0, createArmorStandDataWatcher(text, sneak).getWatchableObjects());
        }
        return metaPacket;
    }

    private static WrappedDataWatcher createArmorStandDataWatcher(String miniMessage, boolean sneak) {
        WrappedDataWatcher wrappedDataWatcher = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer serializer1 = WrappedDataWatcher.Registry.get(Boolean.class);
        WrappedDataWatcher.Serializer serializer2 = WrappedDataWatcher.Registry.get(Byte.class);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)), Optional.of(WrappedChatComponent.fromJson(AdventureManagerImpl.getInstance().getJsonComponentFromMiniMessage(miniMessage)).getHandle()));
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, serializer1), true);
        byte flag = 0x20;
        if (sneak) flag += (byte) 0x02;
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, serializer2), flag);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(15, serializer2), (byte) 0x01);
        wrappedDataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, serializer1), true);
        return wrappedDataWatcher;
    }
}
