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

import com.comphenix.protocol.utility.MinecraftReflection;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.util.LogUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtils {

    private ReflectionUtils() {}

    private static Object removeBossBarPacket;
    private static Constructor<?> progressConstructor;
    private static Constructor<?> updateConstructor;
    private static Method iChatComponentMethod;
    private static Object emptyComponent;
    private static Method serializeComponentMethod;
    private static Method keyAsStringMethod;
    private static Method keyFromStringMethod;
    private static Object miniMessageInstance;
    private static Class<?> keyClass;
    private static boolean isPaper;

    public static void load() {
        if (CustomNameplatesPlugin.get().getVersionManager().isMojmap()) {
            try {
                Class<?> bar = Class.forName("net.minecraft.network.protocol.game.ClientboundBossEventPacket");
                Field remove = bar.getDeclaredField("REMOVE_OPERATION");
                remove.setAccessible(true);
                removeBossBarPacket = remove.get(null);
                Class<?> packetBossClassF = Class.forName("net.minecraft.network.protocol.game.ClientboundBossEventPacket$UpdateProgressOperation");
                progressConstructor = packetBossClassF.getDeclaredConstructor(float.class);
                progressConstructor.setAccessible(true);
                Class<?> packetBossClassE = Class.forName("net.minecraft.network.protocol.game.ClientboundBossEventPacket$UpdateNameOperation");
                updateConstructor = packetBossClassE.getDeclaredConstructor(MinecraftReflection.getIChatBaseComponentClass());
                updateConstructor.setAccessible(true);
                Class<?> craftChatMessageClass = Class.forName("org.bukkit.craftbukkit.util.CraftChatMessage");
                iChatComponentMethod = craftChatMessageClass.getDeclaredMethod("fromJSON", String.class);
                iChatComponentMethod.setAccessible(true);
                emptyComponent = iChatComponentMethod.invoke(null, "{\"text\":\"\"}");
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException exception) {
                LogUtils.severe("Error occurred while loading reflections", exception);
            }
        } else {
            try {
                Class<?> bar = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutBoss");
                Field remove = bar.getDeclaredField("f");
                remove.setAccessible(true);
                removeBossBarPacket = remove.get(null);
                Class<?> packetBossClassF = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutBoss$f");
                progressConstructor = packetBossClassF.getDeclaredConstructor(float.class);
                progressConstructor.setAccessible(true);
                Class<?> packetBossClassE = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutBoss$e");
                updateConstructor = packetBossClassE.getDeclaredConstructor(MinecraftReflection.getIChatBaseComponentClass());
                updateConstructor.setAccessible(true);
                iChatComponentMethod = MinecraftReflection.getChatSerializerClass().getMethod("a", String.class);
                iChatComponentMethod.setAccessible(true);
                emptyComponent = iChatComponentMethod.invoke(null, "{\"text\":\"\"}");
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException exception) {
                LogUtils.severe("Error occurred while loading reflections", exception);
            }
        }
        try {
            Class<?> componentClass = Class.forName("net;kyori;adventure;text;Component".replace(";", "."));
            Class<?> miniMessageClass = Class.forName("net;kyori;adventure;text;minimessage;MiniMessage".replace(";", "."));
            Method miniMessageInstanceGetMethod = miniMessageClass.getMethod("miniMessage");
            miniMessageInstance = miniMessageInstanceGetMethod.invoke(null);
            serializeComponentMethod = miniMessageClass.getMethod("serialize", componentClass);
            keyClass = Class.forName("net;kyori;adventure;key;Key".replace(";", "."));
            keyAsStringMethod = keyClass.getMethod("asString");
            keyFromStringMethod = keyClass.getMethod("key", String.class);
            isPaper = true;
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                 IllegalAccessException ignored) {
        }
    }

    public static Object getRemoveBossBarPacket() {
        return removeBossBarPacket;
    }

    public static Constructor<?> getProgressConstructor() {
        return progressConstructor;
    }

    public static Constructor<?> getUpdateConstructor() {
        return updateConstructor;
    }

    public static Method getiChatComponentMethod() {
        return iChatComponentMethod;
    }

    public static Object getEmptyComponent() {
        return emptyComponent;
    }

    public static Class<?> getKeyClass() {
        return keyClass;
    }

    public static boolean isPaper() {
        return isPaper;
    }

    public static String getKeyAsString(Object key) {
        try {
            return (String) keyAsStringMethod.invoke(key);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Object getKerFromString(String key) {
        try {
            return keyFromStringMethod.invoke(null, key);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMiniMessageTextFromNonShadedComponent(Object component) {
        try {
            return (String) serializeComponentMethod.invoke(miniMessageInstance, component);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return "";
    }
}