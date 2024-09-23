package net.momirealms.customnameplates.bukkit.util;

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

    public static final Constructor<?> constructor$ClientboundSetActionBarTextPacket = requireNonNull(
            ReflectionUtils.getConstructor(
                    requireNonNull(ReflectionUtils.getClazz(
                            BukkitReflectionUtils.assembleMCClass("network.protocol.game.ClientboundSetActionBarTextPacket")
                            )),
                    clazz$Component
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
            ReflectionUtils.getField(
                    clazz$ClientboundBossEventPacket$AddOperation,
                    0
            )
    );

    public static final Field field$ClientboundBossEventPacket$AddOperation$progress = requireNonNull(
            ReflectionUtils.getField(
                    clazz$ClientboundBossEventPacket$AddOperation,
                    1
            )
    );

    public static final Field field$ClientboundBossEventPacket$AddOperation$color = requireNonNull(
            ReflectionUtils.getField(
                    clazz$ClientboundBossEventPacket$AddOperation,
                    2
            )
    );

    public static final Field field$ClientboundBossEventPacket$AddOperation$overlay = requireNonNull(
            ReflectionUtils.getField(
                    clazz$ClientboundBossEventPacket$AddOperation,
                    3
            )
    );

    public static final Field field$ClientboundBossEventPacket$AddOperation$darkenScreen = requireNonNull(
            ReflectionUtils.getField(
                    clazz$ClientboundBossEventPacket$AddOperation,
                    4
            )
    );

    public static final Field field$ClientboundBossEventPacket$AddOperation$playMusic = requireNonNull(
            ReflectionUtils.getField(
                    clazz$ClientboundBossEventPacket$AddOperation,
                    5
            )
    );

    public static final Field field$ClientboundBossEventPacket$AddOperation$createWorldFog = requireNonNull(
            ReflectionUtils.getField(
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
            Field field = ReflectionUtils.getField(
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
}
