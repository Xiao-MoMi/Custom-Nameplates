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

    public static void load() {
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
            LogUtils.severe("Error occurred when loading reflections", exception);
            exception.printStackTrace();
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
}