package net.momirealms.customnameplates.bukkit.packet;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.packet.PacketSender;
import net.momirealms.customnameplates.bukkit.util.BukkitReflectionUtils;
import net.momirealms.customnameplates.api.helper.VersionHelper;
import net.momirealms.customnameplates.common.util.ReflectionUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Objects.requireNonNull;

public class BukkitPacketSender implements PacketSender {

    private final Field field$connection;
    private final Method method$getHandle;
    private final Method method$sendPacket;
    private final Constructor<?> constructor$bundlePacket;
    private final BiConsumer<CNPlayer<?>, List<Object>> packetsConsumer;

    public BukkitPacketSender() {
        Class<?> packetClazz = requireNonNull(ReflectionUtils.getClazz(
                BukkitReflectionUtils.assembleMCClass("network.protocol.Packet")
        ));
        Class<?> serverPlayerClazz = requireNonNull(ReflectionUtils.getClazz(
                BukkitReflectionUtils.assembleMCClass("server.level.ServerPlayer"),
                BukkitReflectionUtils.assembleMCClass("server.level.EntityPlayer")
        ));
        Class<?> playerConnectionClass = requireNonNull(ReflectionUtils.getClazz(
                BukkitReflectionUtils.assembleMCClass("server.network.ServerGamePacketListenerImpl"),
                BukkitReflectionUtils.assembleMCClass("server.network.PlayerConnection")
        ));
        Class<?> craftPlayerClazz = requireNonNull(ReflectionUtils.getClazz(
                BukkitReflectionUtils.assembleCBClass("entity.CraftPlayer")
        ));
        Class<?> clientBoundBundlePacket = ReflectionUtils.getClazz(
                BukkitReflectionUtils.assembleMCClass("network.protocol.game.ClientboundBundlePacket")
        );
        if (clientBoundBundlePacket != null) {
            constructor$bundlePacket = ReflectionUtils.getConstructor(clientBoundBundlePacket, Iterable.class);
        } else {
            constructor$bundlePacket = null;
        }
        method$getHandle = requireNonNull(ReflectionUtils.getMethod(craftPlayerClazz, new String[] { "getHandle" }));
        field$connection = requireNonNull(ReflectionUtils.getField(serverPlayerClazz, playerConnectionClass, 0));
        method$sendPacket = requireNonNull(ReflectionUtils.getMethods(playerConnectionClass, void.class, packetClazz).get(0));



        if (VersionHelper.isVersionNewerThan1_19_4() && constructor$bundlePacket != null) {
            packetsConsumer = ((player, objects) -> {
                try {
                    Object bundle = constructor$bundlePacket.newInstance(objects);
                    sendPacket(player, bundle);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            packetsConsumer = (player, objects) -> {
                for (Object packetObj : objects) {
                    sendPacket(player, packetObj);
                }
            };
        }
    }

    @Override
    public void sendPacket(@NotNull CNPlayer<?> player, final Object packet) {
        try {
            method$sendPacket.invoke(field$connection.get(method$getHandle.invoke(player.player())), packet);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendPackets(@NotNull CNPlayer<?> player, final List<Object> packet) {
        packetsConsumer.accept(player, packet);
    }
}
