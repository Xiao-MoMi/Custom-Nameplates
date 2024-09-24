package net.momirealms.customnameplates.bukkit.util;

import io.netty.channel.Channel;
import net.momirealms.customnameplates.api.helper.VersionHelper;
import net.momirealms.customnameplates.common.util.ReflectionUtils;
import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
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

    public static final Field field$EntityType$TEXT_DISPLAY;

    static {
        System.out.println(VersionHelper.version());
        if (VersionHelper.isVersionNewerThan1_20_5()) {
            field$EntityType$TEXT_DISPLAY = ReflectionUtils.getDeclaredField(
                    clazz$EntityType,
                    "TEXT_DISPLAY", "bb");
        } else if (VersionHelper.isVersionNewerThan1_20_4()) {
            System.out.println("Niubility");
            field$EntityType$TEXT_DISPLAY = ReflectionUtils.getDeclaredField(
                    clazz$EntityType,
                    "TEXT_DISPLAY", "aY");
        } else {
            System.out.println("sb");
            field$EntityType$TEXT_DISPLAY = ReflectionUtils.getDeclaredField(
                    clazz$EntityType,
                    "TEXT_DISPLAY", "aX");
        }
    }

    public static final Object instance$EntityType$TEXT_DISPLAY;

    static {
        try {
            instance$EntityType$TEXT_DISPLAY = field$EntityType$TEXT_DISPLAY.get(clazz$EntityType);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }

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
                    BukkitReflectionUtils.assembleMCClass("network.syncher.DataWatcher$b")
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
}
