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

package net.momirealms.customnameplates.api.helper;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customnameplates.api.CustomNameplates;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Helper class for handling Adventure components and related functionalities.
 */
public class AdventureHelper {

    private final MiniMessage miniMessage;
    private final MiniMessage miniMessageStrict;
    private final GsonComponentSerializer gsonComponentSerializer;
    public static boolean legacySupport = false;

    private final Cache<String, String> miniMessageToJsonCache;
    private final Cache<String, Component> miniMessageToComponentCache;
    private final Cache<String, Object> miniMessageToMinecraftComponentCache;
    private final Cache<Object, String> minecraftComponentToMiniMessageCache;
    private final Cache<String, String> jsonToMiniMessageCache;
    private final Cache<String, Component> jsonToComponentCache;
    private final Cache<Component, String> componentToJsonCache;

    private AdventureHelper() {
        this.miniMessage = MiniMessage.builder().build();
        this.miniMessageStrict = MiniMessage.builder().strict(true).build();
        this.gsonComponentSerializer = GsonComponentSerializer.builder().build();

        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(4, r -> {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setName("customnameplates-scheduler");
            return thread;
        });
        executor.setRemoveOnCancelPolicy(true);
        executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);

        this.miniMessageToJsonCache =
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .executor(executor)
                        .build();
        this.miniMessageToComponentCache =
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .executor(executor)
                        .build();
        this.miniMessageToMinecraftComponentCache =
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .executor(executor)
                        .build();
        this.minecraftComponentToMiniMessageCache =
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .executor(executor)
                        .build();
        this.jsonToMiniMessageCache =
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .executor(executor)
                        .build();
        this.jsonToComponentCache =
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .executor(executor)
                        .build();
        this.componentToJsonCache =
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .executor(executor)
                        .build();
    }

    private static class SingletonHolder {
        private static final AdventureHelper INSTANCE = new AdventureHelper();
    }

    /**
     * Retrieves the singleton instance of AdventureHelper.
     *
     * @return the singleton instance
     */
    public static AdventureHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Converts a MiniMessage string to a Component.
     *
     * @param miniMessage the MiniMessage string
     * @return the resulting Component
     */
    public static Component miniMessage(String miniMessage) {
        AdventureHelper instance = getInstance();
        return instance.miniMessageToComponentCache.get(miniMessage, (text) -> {
            if (legacySupport) {
                return miniMessage().deserialize(legacyToMiniMessage(text));
            } else {
                return miniMessage().deserialize(text);
            }
        });
    }

    /**
     * Converts a json string to a MiniMessage string.
     *
     * @param json the JSON string
     * @return the MiniMessage string representation
     */
    public static String jsonToMiniMessage(String json) {
        AdventureHelper instance = getInstance();
        return instance.jsonToMiniMessageCache.get(json, (rawJson) ->
                instance.miniMessageStrict.serialize(instance.gsonComponentSerializer.deserialize(json))
        );
    }

    /**
     * Converts a MiniMessage string to a JSON string.
     *
     * @param miniMessage the MiniMessage string
     * @return the JSON string representation
     */
    public static String miniMessageToJson(String miniMessage) {
        AdventureHelper instance = getInstance();
        return instance.miniMessageToJsonCache.get(miniMessage, (text) ->
                instance.gsonComponentSerializer.serialize(miniMessage(text))
        );
    }

    public static String miniMessageToJson(String miniMessage, String name, String objective) {
        AdventureHelper instance = getInstance();
        return instance.miniMessageToJsonCache.get(miniMessage, (text) ->
                instance.gsonComponentSerializer.serialize(Component.score().name(name).objective(objective).build().append(miniMessage(text)))
        );
    }

    public static Object miniMessageToMinecraftComponent(String miniMessage) {
        AdventureHelper instance = getInstance();
        return instance.miniMessageToMinecraftComponentCache.get(miniMessage, (text) -> {
            String json = miniMessageToJson(text);
            return CustomNameplates.getInstance().getPlatform().jsonToMinecraftComponent(json);
        });
    }

    public static Object miniMessageToMinecraftComponent(String miniMessage, String name, String objective) {
        AdventureHelper instance = getInstance();
        return instance.miniMessageToMinecraftComponentCache.get(miniMessage, (text) -> {
            String json = miniMessageToJson(text, name, objective);
            return CustomNameplates.getInstance().getPlatform().jsonToMinecraftComponent(json);
        });
    }

    public static String minecraftComponentToMiniMessage(Object component) {
        AdventureHelper instance = getInstance();
        return instance.minecraftComponentToMiniMessageCache.get(component, (object) -> {
            String json = CustomNameplates.getInstance().getPlatform().minecraftComponentToJson(object);
            return jsonToMiniMessage(json);
        });
    }

    public static Component jsonToComponent(String json) {
        AdventureHelper instance = getInstance();
        return instance.jsonToComponentCache.get(json, instance.gsonComponentSerializer::deserialize);
    }

    public static String componentToJson(Component component) {
        AdventureHelper instance = getInstance();
        return instance.componentToJsonCache.get(component, instance.gsonComponentSerializer::serialize);
    }

    /**
     * Retrieves the MiniMessage instance.
     *
     * @return the MiniMessage instance
     */
    public static MiniMessage miniMessage() {
        return getInstance().miniMessage;
    }

    /**
     * Retrieves the GsonComponentSerializer instance.
     *
     * @return the GsonComponentSerializer instance
     */
    public static GsonComponentSerializer gson() {
        return getInstance().gsonComponentSerializer;
    }

    /**
     * Surrounds text with a MiniMessage font tag.
     *
     * @param text the text to surround
     * @param font the font as a {@link Key}
     * @return the text surrounded by the MiniMessage font tag
     */
    public static String surroundWithMiniMessageFont(String text, Key font) {
        return "<font:" + font.asString() + ">" + text + "</font>";
    }

    /**
     * Surrounds text with a MiniMessage font tag.
     *
     * @param text the text to surround
     * @param font the font as a {@link String}
     * @return the text surrounded by the MiniMessage font tag
     */
    public static String surroundWithMiniMessageFont(String text, String font) {
        return "<font:" + font + ">" + text + "</font>";
    }

    /**
     * Checks if a character is a legacy color code.
     *
     * @param c the character to check
     * @return true if the character is a color code, false otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isLegacyColorCode(char c) {
        return c == '§' || c == '&';
    }

    /**
     * Strips the minimessage tags
     *
     * @param text text
     * @return the stripped texts
     */
    public static String stripTags(String text) {
        return getInstance().miniMessage.stripTags(text);
    }

    /**
     * Converts a legacy color code string to a MiniMessage string.
     *
     * @param legacy the legacy color code string
     * @return the MiniMessage string representation
     */
    public static String legacyToMiniMessage(String legacy) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = legacy.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (!isLegacyColorCode(chars[i])) {
                stringBuilder.append(chars[i]);
                continue;
            }
            if (i + 1 >= chars.length) {
                stringBuilder.append(chars[i]);
                continue;
            }
            switch (chars[i+1]) {
                case '0' -> stringBuilder.append("<black>");
                case '1' -> stringBuilder.append("<dark_blue>");
                case '2' -> stringBuilder.append("<dark_green>");
                case '3' -> stringBuilder.append("<dark_aqua>");
                case '4' -> stringBuilder.append("<dark_red>");
                case '5' -> stringBuilder.append("<dark_purple>");
                case '6' -> stringBuilder.append("<gold>");
                case '7' -> stringBuilder.append("<gray>");
                case '8' -> stringBuilder.append("<dark_gray>");
                case '9' -> stringBuilder.append("<blue>");
                case 'a' -> stringBuilder.append("<green>");
                case 'b' -> stringBuilder.append("<aqua>");
                case 'c' -> stringBuilder.append("<red>");
                case 'd' -> stringBuilder.append("<light_purple>");
                case 'e' -> stringBuilder.append("<yellow>");
                case 'f' -> stringBuilder.append("<white>");
                case 'r' -> stringBuilder.append("<r><!i>");
                case 'l' -> stringBuilder.append("<b>");
                case 'm' -> stringBuilder.append("<st>");
                case 'o' -> stringBuilder.append("<i>");
                case 'n' -> stringBuilder.append("<u>");
                case 'k' -> stringBuilder.append("<obf>");
                case 'x' -> {
                    if (i + 13 >= chars.length
                            || !isLegacyColorCode(chars[i+2])
                            || !isLegacyColorCode(chars[i+4])
                            || !isLegacyColorCode(chars[i+6])
                            || !isLegacyColorCode(chars[i+8])
                            || !isLegacyColorCode(chars[i+10])
                            || !isLegacyColorCode(chars[i+12])) {
                        stringBuilder.append(chars[i]);
                        continue;
                    }
                    stringBuilder
                            .append("<#")
                            .append(chars[i+3])
                            .append(chars[i+5])
                            .append(chars[i+7])
                            .append(chars[i+9])
                            .append(chars[i+11])
                            .append(chars[i+13])
                            .append(">");
                    i += 12;
                }
                default -> {
                    stringBuilder.append(chars[i]);
                    continue;
                }
            }
            i++;
        }
        return stringBuilder.toString();
    }
}
