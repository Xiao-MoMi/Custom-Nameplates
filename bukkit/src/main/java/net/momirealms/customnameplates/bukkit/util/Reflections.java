package net.momirealms.customnameplates.bukkit.util;

import net.momirealms.customnameplates.common.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

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

    public static final Method method$fromJSON = requireNonNull(
            ReflectionUtils.getMethod(
                    clazz$CraftChatMessage,
                    new String[]{"fromJSON"},
                    String.class
            )
    );
}
