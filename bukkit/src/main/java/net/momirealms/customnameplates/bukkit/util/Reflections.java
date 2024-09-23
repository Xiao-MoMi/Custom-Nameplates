package net.momirealms.customnameplates.bukkit.util;

import io.netty.channel.Channel;
import net.momirealms.customnameplates.api.helper.VersionHelper;
import net.momirealms.customnameplates.common.util.ReflectionUtils;
import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class Reflections {

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

    public static final Class<?> clazz$ScoreContents = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.chat.contents.ScoreContents")
            )
    );

    public static final Field field$ScoreContents$name = requireNonNull(
            ReflectionUtils.getDeclaredField(
                    clazz$ScoreContents, String.class, 0
            )
    );

    public static final Field field$ScoreContents$objective = requireNonNull(
            ReflectionUtils.getDeclaredField(
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
                    "f", "REMOVE_OPERATION");
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
        if (VersionHelper.isVersionNewerThan1_20()) {
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

    public static final Class<?> clazz$Component$Serializer = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.chat.Component$Serializer"),
                    BukkitReflectionUtils.assembleMCClass("network.chat.IChatBaseComponent$ChatSerializer")
            )
    );

    public static final Class<?> clazz$HolderLookup$Provider = ReflectionUtils.getClazz(
            BukkitReflectionUtils.assembleMCClass("core.HolderLookup$Provider"),
            BukkitReflectionUtils.assembleMCClass("core.HolderLookup$b")
    );

    public static final Method method$Component$Serializer$fromJson = ReflectionUtils.getMethod(
            clazz$Component$Serializer,
            new String[] { "fromJson" },
            String.class, clazz$HolderLookup$Provider
    );

    public static final Method method$Component$Serializer$toJson = ReflectionUtils.getMethod(
            clazz$Component$Serializer,
            new String[] { "toJson" },
            clazz$Component, clazz$HolderLookup$Provider
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

    public static final Class<?> clazz$CraftPlayer = requireNonNull(ReflectionUtils.getClazz(
            BukkitReflectionUtils.assembleCBClass("entity.CraftPlayer")
    ));

    public static final Method method$CraftPlayer$getHandle = requireNonNull(
            ReflectionUtils.getMethod(clazz$CraftPlayer, new String[] { "getHandle" })
    );

    public static final Field field$PlayerConnection = requireNonNull(
            ReflectionUtils.getDeclaredField(clazz$ServerPlayer, clazz$PlayerConnection, 0)
    );

    public static final Method method$SendPacket = requireNonNull(
            ReflectionUtils.getMethods(clazz$PlayerConnection, void.class, clazz$Packet).get(0)
    );

    public static final Class<?> clazz$NetworkManager = requireNonNull(
            ReflectionUtils.getClazz(
                    BukkitReflectionUtils.assembleMCClass("network.Connection"),
                    BukkitReflectionUtils.assembleMCClass("network.NetworkManager")
            )
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
}
