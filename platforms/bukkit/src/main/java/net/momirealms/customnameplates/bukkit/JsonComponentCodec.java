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
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customnameplates.bukkit;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.momirealms.customnameplates.api.helper.VersionHelper;
import net.momirealms.customnameplates.bukkit.util.Reflections;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.function.Function;

final class JsonComponentCodec {
    private static final Function<String, JsonParseException> INVALID_COMPONENT = JsonParseException::new;

    private final MethodHandle minecraftRegistry;
    private final MethodHandle serializationContext;
    private final MethodHandle parse;
    private final MethodHandle getOrThrow;
    private final Object jsonOps;

    private JsonComponentCodec(MethodHandle minecraftRegistry, MethodHandle serializationContext, MethodHandle parse,
                               MethodHandle getOrThrow, Object jsonOps) {
        this.minecraftRegistry = minecraftRegistry;
        this.serializationContext = serializationContext;
        this.parse = parse;
        this.getOrThrow = getOrThrow;
        this.jsonOps = jsonOps;
    }

    @Nullable
    static JsonComponentCodec find() {
        if (!VersionHelper.isVersionNewerThan1_20_5() || Reflections.clazz$CraftRegistry == null) {
            return null;
        }
        try {
            MethodHandles.Lookup lookup = MethodHandles.publicLookup();
            Class<?> dynamicOps = Class.forName("com.mojang.serialization.DynamicOps");
            Class<?> componentSerialization = Class.forName("net.minecraft.network.chat.ComponentSerialization");
            Class<?> decoder = Class.forName("com.mojang.serialization.Decoder");
            Class<?> dataResult = Class.forName("com.mojang.serialization.DataResult");
            MethodHandle registry = lookup.unreflect(Reflections.clazz$CraftRegistry.getMethod("getMinecraftRegistry"));
            MethodHandle context = lookup.unreflect(registry.type().returnType()
                    .getMethod("createSerializationContext", dynamicOps));
            Object componentCodec = componentSerialization.getField("CODEC").get(null);
            MethodHandle parse = lookup.unreflect(decoder.getMethod("parse", dynamicOps, Object.class))
                    .bindTo(componentCodec);
            MethodHandle getOrThrow = lookup.unreflect(dataResult.getMethod("getOrThrow", Function.class));
            Object jsonOps = Class.forName("com.mojang.serialization.JsonOps").getField("INSTANCE").get(null);
            return new JsonComponentCodec(registry, context, parse, getOrThrow, jsonOps);
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return null;
        }
    }

    Object decode(JsonElement json) {
        try {
            Object registry = minecraftRegistry.invoke();
            Object context = serializationContext.invoke(registry, jsonOps);
            return getOrThrow.invoke(parse.invoke(context, json), INVALID_COMPONENT);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new IllegalStateException("Could not decode the actionbar component", e);
        }
    }
}
