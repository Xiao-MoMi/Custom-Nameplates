package net.momirealms.customnameplates.utils;

import java.lang.reflect.Field;

public class Reflection {

    public static Object removeBar;

    public static void load() throws Exception{
        Class<?> bar = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutBoss");
        Field remove = bar.getDeclaredField("f");
        remove.setAccessible(true);
        removeBar = remove.get(null);
    }
}
