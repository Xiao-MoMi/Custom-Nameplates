/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customnameplates.bukkit.util;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import net.momirealms.customnameplates.api.helper.VersionHelper;
import net.momirealms.customnameplates.common.util.ReflectionUtils;
import sun.misc.Unsafe;

import java.lang.reflect.*;
import java.util.*;

import static java.util.Objects.requireNonNull;

public final class Reflections {

    public static void load() {}

    public static final Class<?> clazz$Component = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.chat.Component"),
                    BukkitReflectionUtils.assembleMCClass("network.chat.IChatBaseComponent")
            )
    );

    public static final Class<?> clazz$ComponentContents = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.chat.ComponentContents")
            )
    );

    public static final Method method$Component$getContents = requireNonNull(
            ReflectionUtils.getMethods(
                    clazz$Component, clazz$ComponentContents
            ).get(0)
    );

    public static final Class<?> clazz$Either = ReflectionUtils.getClazz(
            "com.mojang.datafixers.util.Either"
    );

    public static final Method method$Either$right = Optional.ofNullable(clazz$Either)
            .map(it -> ReflectionUtils.getMethod(it, new String[]{"right"}))
            .orElse(null);

    public static final Class<?> clazz$ScoreContents = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.chat.contents.ScoreContents")
            )
    );

    public static final Field field$ScoreContents$name = requireNonNull(
            VersionHelper.isVersionNewerThan1_21_2() ?
                    ReflectionUtils.getInstanceDeclaredField(
                    clazz$ScoreContents, clazz$Either, 0
            ) :
            ReflectionUtils.getInstanceDeclaredField(
                    clazz$ScoreContents, String.class, 0
            )
    );

    public static final Field field$ScoreContents$objective = requireNonNull(
            VersionHelper.isVersionNewerThan1_21_2() ?
            ReflectionUtils.getInstanceDeclaredField(
                    clazz$ScoreContents, String.class, 0
            ) :
            ReflectionUtils.getInstanceDeclaredField(
                    clazz$ScoreContents, String.class, 1
            )
    );

    public static final Class<?> clazz$ClientboundSetActionBarTextPacket = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.ClientboundSetActionBarTextPacket")
            )
    );

    public static final Constructor<?> constructor$ClientboundSetActionBarTextPacket = requireNonNull(
            ReflectionUtils.getConstructor(
                    clazz$ClientboundSetActionBarTextPacket, clazz$Component
            )
    );

    public static final Field field$ClientboundSetActionBarTextPacket$text = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundSetActionBarTextPacket, clazz$Component, 0
            )
    );

    public static final Class<?> clazz$ClientboundSystemChatPacket = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.ClientboundSystemChatPacket")
            )
    );

    public static final Field field$ClientboundSystemChatPacket$component =
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundSystemChatPacket, clazz$Component, 0
            );

    public static final Field field$ClientboundSystemChatPacket$overlay =
            requireNonNull(ReflectionUtils.getDeclaredField(
                    clazz$ClientboundSystemChatPacket, boolean.class, 0
            ));

    public static final Field field$ClientboundSystemChatPacket$text =
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundSystemChatPacket, String.class, 0
            );

    public static final Constructor<?> constructor$ClientboundSystemChatPacket = requireNonNull(
            ReflectionUtils.getConstructor(
                    clazz$ClientboundSystemChatPacket, clazz$Component, boolean.class
            )
    );

    public static final Class<?> clazz$CraftChatMessage = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleCBClass("util.CraftChatMessage")
            )
    );

    public static final Method method$CraftChatMessage$fromJSON = requireNonNull(
            ReflectionUtils.getMethod(
                    clazz$CraftChatMessage,
                    new String[]{"fromJSON"},
                    String.class
            )
    );

    public static final Method method$CraftChatMessage$toJSON = requireNonNull(
            ReflectionUtils.getMethod(
                    clazz$CraftChatMessage,
                    new String[]{"toJSON"},
                    clazz$Component
            )
    );

    public static final Class<?> clazz$ClientboundBossEventPacket = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.ClientboundBossEventPacket"),
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.PacketPlayOutBoss")
            )
    );

    public static final Class<?> clazz$ClientboundBossEventPacket$Operation = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.ClientboundBossEventPacket$Operation"),
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.PacketPlayOutBoss$Action")
            )
    );

    public static final Constructor<?> constructor$ClientboundBossEventPacket = requireNonNull(
            ReflectionUtils.getDeclaredConstructor(
                    clazz$ClientboundBossEventPacket,
                    UUID.class, clazz$ClientboundBossEventPacket$Operation
            )
    );

    public static final Class<?> clazz$ClientboundBossEventPacket$AddOperation = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.ClientboundBossEventPacket$AddOperation"),
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.PacketPlayOutBoss$a")
            )
    );


    public static final Class<?> clazz$BossEvent$BossBarColor = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("world.BossEvent$BossBarColor"),
                    BukkitReflectionUtils.assembleMCClass("world.BossBattle$BarColor")
            )
    );

    public static final Method method$BossEvent$BossBarColor$valueOf = requireNonNull(
            ReflectionUtils.getMethod(
                    clazz$BossEvent$BossBarColor,
                    new String[]{"valueOf"},
                    String.class
            )
    );

    public static final Class<?> clazz$BossEvent$BossBarOverlay = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("world.BossEvent$BossBarOverlay"),
                    BukkitReflectionUtils.assembleMCClass("world.BossBattle$BarStyle")
            )
    );

    public static final Method method$BossEvent$BossBarOverlay$valueOf = requireNonNull(
            ReflectionUtils.getMethod(
                    clazz$BossEvent$BossBarOverlay,
                    new String[]{"valueOf"},
                    String.class
            )
    );

    public static final Field field$ClientboundBossEventPacket$AddOperation$name = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundBossEventPacket$AddOperation,
                    0
            )
    );

    public static final Field field$ClientboundBossEventPacket$AddOperation$progress = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundBossEventPacket$AddOperation,
                    1
            )
    );

    public static final Field field$ClientboundBossEventPacket$AddOperation$color = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundBossEventPacket$AddOperation,
                    2
            )
    );

    public static final Field field$ClientboundBossEventPacket$AddOperation$overlay = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundBossEventPacket$AddOperation,
                    3
            )
    );

    public static final Field field$ClientboundBossEventPacket$AddOperation$darkenScreen = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundBossEventPacket$AddOperation,
                    4
            )
    );

    public static final Field field$ClientboundBossEventPacket$AddOperation$playMusic = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundBossEventPacket$AddOperation,
                    5
            )
    );

    public static final Field field$ClientboundBossEventPacket$AddOperation$createWorldFog = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundBossEventPacket$AddOperation,
                    6
            )
    );

    public static final Unsafe UNSAFE;

    static {
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            UNSAFE = (Unsafe) unsafeField.get(null);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object allocateAddOperationInstance() throws InstantiationException {
        return UNSAFE.allocateInstance(clazz$ClientboundBossEventPacket$AddOperation);
    }

    public static final Object instance$ClientboundBossEventPacket$REMOVE_OPERATION;

    static {
        try {
            Field field = ReflectionUtils.getDeclaredField(
                    clazz$ClientboundBossEventPacket,
                    VersionHelper.isVersionNewerThan1_20_5() ? "g" : "f", "REMOVE_OPERATION");
            field.setAccessible(true);
            instance$ClientboundBossEventPacket$REMOVE_OPERATION = requireNonNull(field.get(null));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static final Class<?> clazz$ClientboundBossEventPacket$UpdateNameOperation = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.ClientboundBossEventPacket$UpdateNameOperation"),
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.PacketPlayOutBoss$e")
            )
    );

    public static final Constructor<?> constructor$ClientboundBossEventPacket$UpdateNameOperation = requireNonNull(
            ReflectionUtils.getDeclaredConstructor(
                    clazz$ClientboundBossEventPacket$UpdateNameOperation,
                    clazz$Component
            )
    );

    public static final Class<?> clazz$CraftRegistry = ReflectionUtils.getClazz(
        BukkitReflectionUtils.assembleCBClass("CraftRegistry")
    );

    public static final Object instance$MinecraftRegistry;

    static {
        if (VersionHelper.isVersionNewerThan1_20_5()) {
            try {
                Method method = requireNonNull(ReflectionUtils.getMethod(clazz$CraftRegistry, new String[]{"getMinecraftRegistry"}));
                instance$MinecraftRegistry = method.invoke(null);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        } else {
            instance$MinecraftRegistry = null;
        }
    }

    public static final Class<?> clazz$HolderLookup$Provider = ReflectionUtils.getClazz(
            BukkitReflectionUtils.assembleMCClass("core.HolderLookup$Provider"),
            BukkitReflectionUtils.assembleMCClass(VersionHelper.isVersionNewerThan1_20_5() ? "core.HolderLookup$a" : "core.HolderLookup$b")
    );

    public static final Class<?> clazz$ClientboundBundlePacket = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.ClientboundBundlePacket")
            )
    );

    public static final Constructor<?> constructor$ClientboundBundlePacket = requireNonNull(
            ReflectionUtils.getConstructor(clazz$ClientboundBundlePacket, Iterable.class)
    );

    public static final Class<?> clazz$Packet = requireNonNull(ReflectionUtils.getClazz(
            BukkitReflectionUtils.assembleMCClass("network.protocol.Packet")
    ));

    public static final Class<?> clazz$ServerPlayer = requireNonNull(ReflectionUtils.getClazz(
            BukkitReflectionUtils.assembleMCClass("server.level.ServerPlayer"),
            BukkitReflectionUtils.assembleMCClass("server.level.EntityPlayer")
    ));

    public static final Class<?> clazz$PlayerConnection = requireNonNull(ReflectionUtils.getClazz(
            BukkitReflectionUtils.assembleMCClass("server.network.ServerGamePacketListenerImpl"),
            BukkitReflectionUtils.assembleMCClass("server.network.PlayerConnection")
    ));

    public static final Class<?> clazz$NetworkManager = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.Connection"),
                    BukkitReflectionUtils.assembleMCClass("network.NetworkManager")
            )
    );

    public static final Class<?> clazz$PacketSendListener = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.PacketSendListener")
            )
    );

    public static final Class<?> clazz$CraftPlayer = requireNonNull(ReflectionUtils.getClazz(
            BukkitReflectionUtils.assembleCBClass("entity.CraftPlayer")
    ));

    public static final Method method$CraftPlayer$getHandle = requireNonNull(
            ReflectionUtils.getMethod(clazz$CraftPlayer, new String[] { "getHandle" })
    );

    public static final Field field$PlayerConnection = requireNonNull(
            ReflectionUtils.getInstanceDeclaredField(clazz$ServerPlayer, clazz$PlayerConnection, 0)
    );

    public static final Method method$SendPacket = requireNonNull(
            ReflectionUtils.getMethods(clazz$PlayerConnection, void.class, clazz$Packet).get(0)
    );

    public static final Field field$NetworkManager = requireNonNull(
            VersionHelper.isVersionNewerThan1_20_2() ?
            ReflectionUtils.getDeclaredField(clazz$PlayerConnection.getSuperclass(), clazz$NetworkManager, 0) :
            ReflectionUtils.getDeclaredField(clazz$PlayerConnection, clazz$NetworkManager, 0)
    );

    public static final Field field$Channel = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$NetworkManager, Channel.class, 0
            )
    );

    public static final Field field$BundlePacket$packets = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundBundlePacket.getSuperclass(), Iterable.class, 0
            )
    );

    public static final Class<?> clazz$ClientboundAddEntityPacket = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.PacketPlayOutSpawnEntity"),
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.ClientboundAddEntityPacket")
            )
    );

    public static final Class<?> clazz$PacketPlayOutNamedEntitySpawn =
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.PacketPlayOutNamedEntitySpawn")
            );

    public static final Class<?> clazz$ClientboundRemoveEntitiesPacket = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.ClientboundRemoveEntitiesPacket"),
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.PacketPlayOutEntityDestroy")
            )
    );

    public static final Class<?> clazz$ClientboundPlayerInfoRemovePacket = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.ClientboundPlayerInfoRemovePacket")
            )
    );

    public static final Field field$ClientboundPlayerInfoRemovePacket$profileIds = requireNonNull(
            ReflectionUtils.getInstanceDeclaredField(
                    clazz$ClientboundPlayerInfoRemovePacket, 0
            )
    );

    public static final Field field$ClientboundRemoveEntitiesPacket$entityIds = requireNonNull(
            ReflectionUtils.getInstanceDeclaredField(
                    clazz$ClientboundRemoveEntitiesPacket, 0
            )
    );

    public static final Field field$ClientboundAddEntityPacket$entityId = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundAddEntityPacket, int.class, 0
            )
    );

    public static final Field field$PacketPlayOutNamedEntitySpawn$entityId = clazz$PacketPlayOutNamedEntitySpawn != null ?
            ReflectionUtils.getDeclaredField(
                    clazz$PacketPlayOutNamedEntitySpawn, int.class, 0
            ) : null;

    public static final Class<?> clazz$EntityType = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("world.entity.EntityType"),
                    BukkitReflectionUtils.assembleMCClass("world.entity.EntityTypes")
            )
    );

    public static final Class<?> clazz$Vec3 = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("world.phys.Vec3"),
                    BukkitReflectionUtils.assembleMCClass("world.phys.Vec3D")
            )
    );

    public static final Constructor<?> constructor$Vec3 = requireNonNull(
            ReflectionUtils.getConstructor(
                    clazz$Vec3, double.class, double.class, double.class
            )
    );

    public static final Constructor<?> constructor$ClientboundAddEntityPacket = requireNonNull(
            ReflectionUtils.getConstructor(clazz$ClientboundAddEntityPacket,
                    int.class, UUID.class,
                    double.class, double.class, double.class,
                    float.class, float.class,
                    clazz$EntityType,
                    int.class, clazz$Vec3, double.class
            )
    );

    public static final Constructor<?> constructor$ClientboundRemoveEntitiesPacket = requireNonNull(
            ReflectionUtils.getConstructor(clazz$ClientboundRemoveEntitiesPacket, int[].class)
    );

    public static final Class<?> clazz$ClientboundSetPassengersPacket = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.PacketPlayOutMount"),
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.ClientboundSetPassengersPacket")
            )
    );

    public static final Field field$ClientboundSetPassengersPacket$vehicle = requireNonNull(
            ReflectionUtils.getInstanceDeclaredField(
                    clazz$ClientboundSetPassengersPacket, 0
            )
    );

    public static final Field field$ClientboundSetPassengersPacket$passengers = requireNonNull(
            ReflectionUtils.getInstanceDeclaredField(
                    clazz$ClientboundSetPassengersPacket, 1
            )
    );

    public static Object allocateClientboundSetPassengersPacketInstance() throws InstantiationException {
            return UNSAFE.allocateInstance(clazz$ClientboundSetPassengersPacket);
    }

    public static final Field field$Vec3$Zero = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$Vec3, clazz$Vec3, 0
            )
    );

    public static final Object instance$Vec3$Zero;

    static {
        try {
            instance$Vec3$Zero = field$Vec3$Zero.get(null);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    public static final Class<?> clazz$ClientboundSetEntityDataPacket = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.PacketPlayOutEntityMetadata"),
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.ClientboundSetEntityDataPacket")
            )
    );

    public static final Constructor<?> constructor$ClientboundSetEntityDataPacket = requireNonNull(
            ReflectionUtils.getConstructor(clazz$ClientboundSetEntityDataPacket,
                    int.class, List.class)
    );

    public static final Class<?> clazz$EntityDataSerializers = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.syncher.EntityDataSerializers"),
                    BukkitReflectionUtils.assembleMCClass("network.syncher.DataWatcherRegistry")
            )
    );

    public static final Class<?> clazz$EntityDataSerializer = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.syncher.EntityDataSerializer"),
                    BukkitReflectionUtils.assembleMCClass("network.syncher.DataWatcherSerializer")
            )
    );

    public static final Class<?> clazz$EntityDataAccessor = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.syncher.EntityDataAccessor"),
                    BukkitReflectionUtils.assembleMCClass("network.syncher.DataWatcherObject")
            )
    );

    public static final Constructor<?> constructor$EntityDataAccessor = requireNonNull(
            ReflectionUtils.getConstructor(
                    clazz$EntityDataAccessor, int.class, clazz$EntityDataSerializer
            )
    );

    public static final Class<?> clazz$SynchedEntityData$DataValue = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.syncher.SynchedEntityData$DataValue"),
                    BukkitReflectionUtils.assembleMCClass(VersionHelper.isVersionNewerThan1_20_5() ? "network.syncher.DataWatcher$c" : "network.syncher.DataWatcher$b")
            )
    );

    public static final Method method$SynchedEntityData$DataValue$create = requireNonNull(
            ReflectionUtils.getMethod(
                    clazz$SynchedEntityData$DataValue, clazz$SynchedEntityData$DataValue, clazz$EntityDataAccessor, Object.class
            )
    );

    public static final Method method$Component$empty = requireNonNull(
            ReflectionUtils.getStaticMethod(
                    clazz$Component, clazz$Component
            )
    );

    public static final Object instance$Component$empty;

    static {
        try {
            instance$Component$empty = method$Component$empty.invoke(null);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    public static final Class<?> clazz$Quaternionf = requireNonNull(
            ReflectionUtils.getClazz(
                    "org.joml.Quaternionf"
            )
    );

    public static final Constructor<?> constructor$Quaternionf = requireNonNull(
            ReflectionUtils.getConstructor(
                    clazz$Quaternionf, float.class, float.class, float.class, float.class
            )
    );

    public static final Object instance$Quaternionf$None;

    static {
        try {
            instance$Quaternionf$None = constructor$Quaternionf.newInstance(0f, 0f, 0f, 1f);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    public static final Class<?> clazz$Vector3f = requireNonNull(
            ReflectionUtils.getClazz(
                    "org.joml.Vector3f"
            )
    );

    public static final Constructor<?> constructor$Vector3f = requireNonNull(
            ReflectionUtils.getConstructor(
                    clazz$Vector3f, float.class, float.class, float.class
            )
    );

    public static final Object instance$Vector3f$None;

    static {
        try {
            instance$Vector3f$None = constructor$Vector3f.newInstance(0f, 0f, 0f);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    public static final Field field$ClientboundSetEntityDataPacket$id = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundSetEntityDataPacket, int.class, 0
            )
    );

    public static final Field field$ClientboundSetEntityDataPacket$packedItems = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundSetEntityDataPacket, List.class, 0
            )
    );

    public static final Field field$SynchedEntityData$DataValue$id = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$SynchedEntityData$DataValue, int.class, 0
            )
    );

    public static final Field field$SynchedEntityData$DataValue$value = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$SynchedEntityData$DataValue, 2
            )
    );

    public static final Class<?> clazz$ClientboundUpdateAttributesPacket = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.ClientboundUpdateAttributesPacket"),
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.PacketPlayOutUpdateAttributes")
            )
    );

    public static final Field field$ClientboundUpdateAttributesPacket$id = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundUpdateAttributesPacket, int.class, 0
            )
    );

    public static final Field field$ClientboundUpdateAttributesPacket$attributes = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundUpdateAttributesPacket, List.class, 0
            )
    );

    public static final Class<?> clazz$ClientboundUpdateAttributesPacket$AttributeSnapshot = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.ClientboundUpdateAttributesPacket$AttributeSnapshot"),
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.PacketPlayOutUpdateAttributes$AttributeSnapshot")
            )
    );

    public static final Class<?> clazz$Holder = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("core.Holder")
            )
    );

    public static final Field field$ClientboundUpdateAttributesPacket$AttributeSnapshot$attribute =
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundUpdateAttributesPacket$AttributeSnapshot, clazz$Holder, 0
            );

    public static final Field field$ClientboundUpdateAttributesPacket$AttributeSnapshot$base =
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundUpdateAttributesPacket$AttributeSnapshot, double.class, 0
            );

    public static final Field field$ClientboundUpdateAttributesPacket$AttributeSnapshot$modifiers =
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundUpdateAttributesPacket$AttributeSnapshot, Collection.class, 0
            );

    public static final Method method$Holder$value = requireNonNull(
            ReflectionUtils.getMethod(
                    clazz$Holder, new String[]{"a", "value"}
            )
    );

    public static final Class<?> clazz$Attribute = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("world.entity.ai.attributes.Attribute"),
                    BukkitReflectionUtils.assembleMCClass("world.entity.ai.attributes.AttributeBase")
            )
    );

    public static final Field field$Attribute$id = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$Attribute, String.class, 0
            )
    );

    public static final Class<?> clazz$AttributeModifier = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("world.entity.ai.attributes.AttributeModifier")
            )
    );

    public static final Field field$AttributeModifier$amount = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$AttributeModifier, double.class, 0
            )
    );

    public static final Class<?> clazz$ClientboundGameEventPacket = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.ClientboundGameEventPacket"),
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.PacketPlayOutGameStateChange")
            )
    );

    public static final Class<?> clazz$ClientboundGameEventPacket$Type = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.ClientboundGameEventPacket$Type"),
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.PacketPlayOutGameStateChange$a")
            )
    );

    public static final Field field$ClientboundGameEventPacket$event = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundGameEventPacket, clazz$ClientboundGameEventPacket$Type, 0
            )
    );

    public static final Field field$ClientboundGameEventPacket$param = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundGameEventPacket, float.class, 0
            )
    );

    public static final Field field$ClientboundGameEventPacket$Type$id = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundGameEventPacket$Type, int.class, 0
            )
    );

    public static final Class<?> clazz$ClientboundPlayerInfoUpdatePacket = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.ClientboundPlayerInfoUpdatePacket")
            )
    );

    public static final Field field$ClientboundPlayerInfoUpdatePacket$actions = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundPlayerInfoUpdatePacket, EnumSet.class, 0
            )
    );

    public static final Field field$ClientboundPlayerInfoUpdatePacket$entries = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundPlayerInfoUpdatePacket, List.class, 0
            )
    );

    public static final Class<?> clazz$ClientboundPlayerInfoUpdatePacket$Action = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.ClientboundPlayerInfoUpdatePacket$Action"),
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.ClientboundPlayerInfoUpdatePacket$a")
            )
    );

    public static final Enum<?> enum$ClientboundPlayerInfoUpdatePacket$Action$UPDATE_GAME_MODE;
    public static final Enum<?> enum$ClientboundPlayerInfoUpdatePacket$Action$ADD_PLAYER;

    static {
        Enum<?> updateGameMode;
        try {
            updateGameMode = Enum.valueOf((Class<Enum>) clazz$ClientboundPlayerInfoUpdatePacket$Action, "UPDATE_GAME_MODE");
        } catch (Exception e) {
            updateGameMode = Enum.valueOf((Class<Enum>) clazz$ClientboundPlayerInfoUpdatePacket$Action, "c");
        }
        enum$ClientboundPlayerInfoUpdatePacket$Action$UPDATE_GAME_MODE = updateGameMode;

        Enum<?> addPlayer;
        try {
            addPlayer = Enum.valueOf((Class<Enum>) clazz$ClientboundPlayerInfoUpdatePacket$Action, "ADD_PLAYER");
        } catch (Exception e) {
            addPlayer = Enum.valueOf((Class<Enum>) clazz$ClientboundPlayerInfoUpdatePacket$Action, "a");
        }
        enum$ClientboundPlayerInfoUpdatePacket$Action$ADD_PLAYER = addPlayer;
    }

    public static final Class<?> clazz$ClientboundPlayerInfoUpdatePacket$Entry = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.ClientboundPlayerInfoUpdatePacket$Entry"),
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.ClientboundPlayerInfoUpdatePacket$b")
            )
    );

    public static final Class<?> clazz$GameType = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("world.level.GameType"),
                    BukkitReflectionUtils.assembleMCClass("world.level.EnumGamemode")
            )
    );

    public static final Field field$ClientboundPlayerInfoUpdatePacket$Entry$gameMode = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundPlayerInfoUpdatePacket$Entry, clazz$GameType, 0
            )
    );

    public static final Field field$ClientboundPlayerInfoUpdatePacket$Entry$profileId = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundPlayerInfoUpdatePacket$Entry, UUID.class, 0
            )
    );

    public static final Method method$GameType$getId = requireNonNull(
            ReflectionUtils.getMethod(
                    clazz$GameType, new String[] { "getId", "a" }
            )
    );

    public static final Class<?> clazz$Biome = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("world.level.biome.Biome"),
                    BukkitReflectionUtils.assembleMCClass("world.level.biome.BiomeBase")
            )
    );

    public static final Class<?> clazz$CraftWorld = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleCBClass("CraftWorld")
            )
    );

    public static final Class<?> clazz$ServerLevel = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("server.level.ServerLevel"),
                    BukkitReflectionUtils.assembleMCClass("server.level.WorldServer")
            )
    );

    public static final Field field$CraftWorld$ServerLevel = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$CraftWorld, clazz$ServerLevel, 0
            )
    );

    public static final Method method$ServerLevel$getNoiseBiome = requireNonNull(
            ReflectionUtils.getMethod(
                    clazz$ServerLevel, clazz$Holder, int.class, int.class, int.class
            )
    );

    public static final Class<?> clazz$ResourceLocation = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("resources.ResourceLocation"),
                    BukkitReflectionUtils.assembleMCClass("resources.MinecraftKey"),
                    BukkitReflectionUtils.assembleMCClass("resources.Identifier")
            )
    );

    public static final Class<?> clazz$ResourceKey = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("resources.ResourceKey")
            )
    );

    public static final Class<?> clazz$MinecraftServer = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("server.MinecraftServer")
            )
    );

    public static final Method method$MinecraftServer$getServer = requireNonNull(
            ReflectionUtils.getMethod(clazz$MinecraftServer, new String[] { "getServer" })
    );

    public static final Class<?> clazz$LayeredRegistryAccess = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("core.LayeredRegistryAccess")
            )
    );

    public static final Field field$MinecraftServer$registries = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$MinecraftServer, clazz$LayeredRegistryAccess, 0
            )
    );

    public static final Class<?> clazz$RegistryAccess$Frozen = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("core.RegistryAccess$Frozen"),
                    BukkitReflectionUtils.assembleMCClass("core.IRegistryCustom$Dimension")
            )
    );

    public static final Class<?> clazz$RegistryAccess = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("core.RegistryAccess"),
                    BukkitReflectionUtils.assembleMCClass("core.IRegistryCustom")
            )
    );

    public static final Field field$LayeredRegistryAccess$composite = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$LayeredRegistryAccess, clazz$RegistryAccess$Frozen, 0
            )
    );

    public static final Class<?> clazz$Registry = requireNonNull(
            requireNonNull(ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("core.WritableRegistry"),
                    BukkitReflectionUtils.assembleMCClass("core.IRegistryWritable")
            )).getInterfaces()[0]
    );

    public static final Method method$RegistryAccess$registryOrThrow = requireNonNull(
            ReflectionUtils.getMethod(
                    clazz$RegistryAccess, clazz$Registry, clazz$ResourceKey
            )
    );

    public static final Class<?> clazz$Registries = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("core.registries.Registries")
            )
    );

    public static final Method method$Registry$getKey = requireNonNull(
            ReflectionUtils.getMethod(clazz$Registry, clazz$ResourceLocation, Object.class)
    );

    public static final Object instance$BiomeRegistry;

    static {
        Field[] fields = clazz$Registries.getDeclaredFields();
        try {
            Object field$Registries$Biome = null;
            for (Field field : fields) {
                Type fieldType = field.getGenericType();
                if (fieldType instanceof ParameterizedType paramType) {
                    if (paramType.getRawType() == clazz$ResourceKey) {
                        Type[] actualTypeArguments = paramType.getActualTypeArguments();
                        if (actualTypeArguments.length == 1 && actualTypeArguments[0] instanceof ParameterizedType registryType) {
                            if (registryType.getActualTypeArguments()[0] == clazz$Biome) {
                                field$Registries$Biome = field.get(null);
                                break;
                            }
                        }
                    }
                }
            }
            requireNonNull(field$Registries$Biome);
            Object server = method$MinecraftServer$getServer.invoke(null);
            Object registries = field$MinecraftServer$registries.get(server);
            Object compositeAccess = field$LayeredRegistryAccess$composite.get(registries);
            instance$BiomeRegistry = method$RegistryAccess$registryOrThrow.invoke(compositeAccess, field$Registries$Biome);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static final Class<?> clazz$ClientboundSetPlayerTeamPacket = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.ClientboundSetPlayerTeamPacket"),
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.PacketPlayOutScoreboardTeam")
            )
    );

    public static final Field field$ClientboundSetPlayerTeamPacket$method = requireNonNull(
            ReflectionUtils.getInstanceDeclaredField(
                    clazz$ClientboundSetPlayerTeamPacket, int.class, 0
            )
    );

    public static final Field field$ClientboundSetPlayerTeamPacket$players = requireNonNull(
            ReflectionUtils.getInstanceDeclaredField(
                    clazz$ClientboundSetPlayerTeamPacket, Collection.class, 0
            )
    );

    public static final Field field$ClientboundSetPlayerTeamPacket$name = requireNonNull(
            ReflectionUtils.getInstanceDeclaredField(
                    clazz$ClientboundSetPlayerTeamPacket, String.class, 0
            )
    );

    public static final Field field$ClientboundSetPlayerTeamPacket$parameters = requireNonNull(
            ReflectionUtils.getInstanceDeclaredField(
                    clazz$ClientboundSetPlayerTeamPacket, Optional.class, 0
            )
    );

    public static final Class<?> clazz$ClientboundSetPlayerTeamPacket$Parameters = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.ClientboundSetPlayerTeamPacket$Parameters"),
                    BukkitReflectionUtils.assembleMCClass("network.protocol.game.PacketPlayOutScoreboardTeam$b")
            )
    );

    public static final Class<?> clazz$Team$Visibility = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("world.scores.ScoreboardTeamBase$EnumNameTagVisibility"),
                    BukkitReflectionUtils.assembleMCClass("world.scores.Team$Visibility")
            )
    );

    public static final Field field$ClientboundSetPlayerTeamPacket$Parameters$nametagVisibility = requireNonNull(
            VersionHelper.isVersionNewerThan1_21_5() ?
                    ReflectionUtils.getInstanceDeclaredField(
                            clazz$ClientboundSetPlayerTeamPacket$Parameters, clazz$Team$Visibility, 0
                    )
                    : ReflectionUtils.getInstanceDeclaredField(
                            clazz$ClientboundSetPlayerTeamPacket$Parameters, String.class, 0
                    )
    );

    public static final Method method$Team$Visibility$values = requireNonNull(
            ReflectionUtils.getStaticMethod(
                    clazz$Team$Visibility, clazz$Team$Visibility.arrayType()
            )
    );

    public static final Object instance$Team$Visibility$NEVER;

    static {
        try {
            Object[] values = (Object[]) method$Team$Visibility$values.invoke(null);
            instance$Team$Visibility$NEVER = values[1];
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static final Class<?> clazz$ServerConnectionListener = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("server.network.ServerConnectionListener"),
                    BukkitReflectionUtils.assembleMCClass("server.network.ServerConnection")
            )
    );

    public static final Field field$MinecraftServer$connection = requireNonNull(
            ReflectionUtils.getDeclaredField(clazz$MinecraftServer, clazz$ServerConnectionListener, 0)
    );

    public static final Field field$ServerConnectionListener$channels;

    static {
        Field[] fields = clazz$ServerConnectionListener.getDeclaredFields();
        Field f = null;
        for (Field field : fields) {
            if (List.class.isAssignableFrom(field.getType())) {
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType paramType) {
                    Type[] actualTypeArguments = paramType.getActualTypeArguments();
                    if (actualTypeArguments.length > 0 && actualTypeArguments[0] == ChannelFuture.class) {
                        f = ReflectionUtils.setAccessible(field);
                        break;
                    }
                }
            }
        }
        field$ServerConnectionListener$channels = requireNonNull(f);
    }

    public static final Class<?> clazz$AttributeModifier$Operation = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("world.entity.ai.attributes.AttributeModifier$Operation")
            )
    );

    public static final Method method$AttributeModifier$Operation$values = requireNonNull(
            ReflectionUtils.getStaticMethod(
                    clazz$AttributeModifier$Operation, clazz$AttributeModifier$Operation.arrayType()
            )
    );

    public static final Object instance$AttributeModifier$Operation$ADD_VALUE;
    public static final Object instance$AttributeModifier$Operation$ADD_MULTIPLIED_BASE;
    public static final Object instance$AttributeModifier$Operation$ADD_MULTIPLIED_TOTAL;

    static {
        try {
            Object[] values = (Object[]) method$AttributeModifier$Operation$values.invoke(null);
            instance$AttributeModifier$Operation$ADD_VALUE = values[0];
            instance$AttributeModifier$Operation$ADD_MULTIPLIED_BASE = values[1];
            instance$AttributeModifier$Operation$ADD_MULTIPLIED_TOTAL = values[2];
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    public static final Field field$AttributeModifier$operation = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$AttributeModifier, clazz$AttributeModifier$Operation, 0
            )
    );

    public static final Method method$ResourceLocation$fromNamespaceAndPath = requireNonNull(
            ReflectionUtils.getStaticMethod(
                    clazz$ResourceLocation, clazz$ResourceLocation, String.class, String.class
            )
    );

    public static final Object instance$BuiltInRegistries$ENTITY_TYPE;
    public static final Object instance$Registries$ENTITY_TYPE;
    public static final Object instance$registryAccess;

    static {
        Field[] fields = clazz$Registries.getDeclaredFields();
        try {
            Object registries$EntityType  = null;
            for (Field field : fields) {
                Type fieldType = field.getGenericType();
                if (fieldType instanceof ParameterizedType paramType) {
                    if (paramType.getRawType() == clazz$ResourceKey) {
                        Type[] actualTypeArguments = paramType.getActualTypeArguments();
                        if (actualTypeArguments.length == 1 && actualTypeArguments[0] instanceof ParameterizedType registryType) {
                            Type type = registryType.getActualTypeArguments()[0];
                            if (type instanceof  ParameterizedType parameterizedType) {
                                Type rawType = parameterizedType.getRawType();
                                if (rawType == clazz$EntityType) {
                                    registries$EntityType = field.get(null);
                                }
                            }
                        }
                    }
                }
            }
            instance$Registries$ENTITY_TYPE = requireNonNull(registries$EntityType);
            Object server = method$MinecraftServer$getServer.invoke(null);
            Object registries = field$MinecraftServer$registries.get(server);
            instance$registryAccess = field$LayeredRegistryAccess$composite.get(registries);
            instance$BuiltInRegistries$ENTITY_TYPE = method$RegistryAccess$registryOrThrow.invoke(instance$registryAccess, registries$EntityType);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static final Method method$Registry$get = requireNonNull(
            ReflectionUtils.getMethods(
                    clazz$Registry, Object.class, clazz$ResourceLocation
            ).stream().filter(m -> m.getReturnType() != Optional.class).findAny().orElse(null)
    );

    public static final Object instance$EntityType$TEXT_DISPLAY;

    static {
        try {
            Object textDisplay = method$ResourceLocation$fromNamespaceAndPath.invoke(null, "minecraft", "text_display");
            instance$EntityType$TEXT_DISPLAY = Reflections.method$Registry$get.invoke(Reflections.instance$BuiltInRegistries$ENTITY_TYPE, textDisplay);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static final Class<?> clazz$AdventureComponent = requireNonNull(
            ReflectionUtils.getClazz(
                    "net{}kyori{}adventure{}text{}Component".replace("{}", ".")
            )
    );

    // <= 1.20.4
    public static final Field field$ClientboundSetActionBarTextPacket$adventure$text =
            ReflectionUtils.getDeclaredField(
                    clazz$ClientboundSetActionBarTextPacket, clazz$AdventureComponent, 0
            );

    public static final Class<?> clazz$ComponentSerializer = requireNonNull(
            ReflectionUtils.getClazz(
                    "net{}kyori{}adventure{}text{}serializer{}ComponentSerializer".replace("{}", ".")
            )
    );

    public static final Class<?> clazz$GsonComponentSerializer = requireNonNull(
            ReflectionUtils.getClazz(
                    "net{}kyori{}adventure{}text{}serializer{}gson{}GsonComponentSerializer".replace("{}", ".")
            )
    );

    public static final Class<?> clazz$GsonComponentSerializer$Builder = requireNonNull(
            ReflectionUtils.getClazz(
                    "net{}kyori{}adventure{}text{}serializer{}gson{}GsonComponentSerializer$Builder".replace("{}", ".")
            )
    );

    public static final Method method$GsonComponentSerializer$builder = requireNonNull(
            ReflectionUtils.getMethod(
                    clazz$GsonComponentSerializer, clazz$GsonComponentSerializer$Builder
            )
    );

    public static final Method method$GsonComponentSerializer$Builder$build = requireNonNull(
            ReflectionUtils.getMethod(
                    clazz$GsonComponentSerializer$Builder, clazz$GsonComponentSerializer
            )
    );

    public static final Method method$ComponentSerializer$serialize = requireNonNull(
            ReflectionUtils.getMethod(
                    clazz$ComponentSerializer, Object.class, new String[] {"serialize"}, clazz$AdventureComponent
            )
    );

    public static final Method method$ComponentSerializer$deserialize = requireNonNull(
            ReflectionUtils.getMethod(
                    clazz$ComponentSerializer, Object.class, new String[] {"deserialize"}, Object.class
            )
    );

    public static final Class<?> clazz$ClientboundLoginFinishedPacket = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.protocol.login.PacketLoginOutSuccess"),
                    BukkitReflectionUtils.assembleMCClass("network.protocol.login.ClientboundLoginFinishedPacket"),
                    BukkitReflectionUtils.assembleMCClass("network.protocol.login.ClientboundGameProfilePacket")
            )
    );

    public static final Class<?> clazz$GameProfile = requireNonNull(
            ReflectionUtils.getClazz("com.mojang.authlib.GameProfile")
    );

    public static final Field field$ClientboundLoginFinishedPacket$gameProfile = requireNonNull(
            ReflectionUtils.getDeclaredField(clazz$ClientboundLoginFinishedPacket, clazz$GameProfile, 0)
    );

    public static final Field field$GameProfile$name = requireNonNull(
            ReflectionUtils.getDeclaredField(clazz$GameProfile, String.class, 0)
    );
}
