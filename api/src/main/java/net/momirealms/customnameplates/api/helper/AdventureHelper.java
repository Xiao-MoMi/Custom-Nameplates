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
import net.kyori.adventure.text.serializer.json.JSONOptions;
import net.kyori.adventure.text.serializer.json.legacyimpl.NBTLegacyHoverEventSerializer;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.api.CustomNameplates;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Helper class for handling Adventure components and related functionalities.
 */
public class AdventureHelper {

    /**
     * Whether to enable legacy color code support
     */
    public static boolean legacySupport = false;

    private final MiniMessage miniMessage;
    private final MiniMessage miniMessageStrict;
    private final GsonComponentSerializer gsonComponentSerializer;

    private final Cache<String, Component> miniMessageToComponentCache;
    private final Cache<String, Object> miniMessageToMinecraftComponentCache;
    private final Cache<Object, String> minecraftComponentToMiniMessageCache;
    private final Cache<String, String> jsonToMiniMessageCache;

    private AdventureHelper() {
        this.miniMessage = MiniMessage.builder().build();
        this.miniMessageStrict = MiniMessage.builder().strict(true).build();
        GsonComponentSerializer.Builder builder = GsonComponentSerializer.builder();
        if (!VersionHelper.isVersionNewerThan1_20_5()) {
            builder.legacyHoverEventSerializer(NBTLegacyHoverEventSerializer.get());
            builder.editOptions((b) -> b.value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, false));
        }
        if (!VersionHelper.isVersionNewerThan1_21_5()) {
            builder.editOptions((b) -> {
                b.value(JSONOptions.EMIT_CLICK_EVENT_TYPE, JSONOptions.ClickEventValueMode.CAMEL_CASE);
                b.value(JSONOptions.EMIT_HOVER_EVENT_TYPE, JSONOptions.HoverEventValueMode.CAMEL_CASE);
                b.value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_KEY_AS_TYPE_AND_UUID_AS_ID, true);
            });
        }
        this.gsonComponentSerializer = builder.build();

        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, r -> {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setName("nameplates-cache-scheduler");
            return thread;
        });
        executor.setRemoveOnCancelPolicy(true);
        executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);

        this.miniMessageToComponentCache =
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .executor(executor)
                        .maximumSize(1024)
                        .build();
        this.miniMessageToMinecraftComponentCache =
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .executor(executor)
                        .maximumSize(1024)
                        .build();
        this.minecraftComponentToMiniMessageCache =
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .executor(executor)
                        .maximumSize(256)
                        .build();
        this.jsonToMiniMessageCache =
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .executor(executor)
                        .maximumSize(256)
                        .build();
    }

    /**
     * Clear cache
     */
    public static void clearCache() {
        AdventureHelper instance = getInstance();
        instance.miniMessageToMinecraftComponentCache.cleanUp();
        instance.miniMessageToComponentCache.cleanUp();
        instance.jsonToMiniMessageCache.cleanUp();
        instance.minecraftComponentToMiniMessageCache.cleanUp();
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
     * Converts a MiniMessage string to a Minecraft component object.
     *
     * @param miniMessage the MiniMessage string
     * @return the Minecraft component object
     */
    public static Object miniMessageToMinecraftComponent(String miniMessage) {
        AdventureHelper instance = getInstance();
        return instance.miniMessageToMinecraftComponentCache.get(miniMessage, (text) -> {
            String json = instance.gsonComponentSerializer.serialize(miniMessage(text));
            return CustomNameplates.getInstance().getPlatform().jsonToMinecraftComponent(json);
        });
    }

    /**
     * Converts a MiniMessage string with a score component to a Minecraft component object.
     *
     * @param miniMessage the MiniMessage string
     * @param name        the name used in the score component
     * @param objective   the objective used in the score component
     * @return the Minecraft component object
     */
    public static Object miniMessageToMinecraftComponent(String miniMessage, String name, String objective) {
        AdventureHelper instance = getInstance();
        return instance.miniMessageToMinecraftComponentCache.get(miniMessage, (text) -> {
            String json = instance.gsonComponentSerializer.serialize(Component.score().name(name).objective(objective).build().append(miniMessage(text)));
            return CustomNameplates.getInstance().getPlatform().jsonToMinecraftComponent(json);
        });
    }

    /**
     * Converts a Minecraft component object to a MiniMessage string.
     *
     * @param component the Minecraft component object
     * @return the MiniMessage string representation
     */
    public static String minecraftComponentToMiniMessage(Object component) {
        AdventureHelper instance = getInstance();
        return instance.minecraftComponentToMiniMessageCache.get(component, (object) -> {
            String json = CustomNameplates.getInstance().getPlatform().minecraftComponentToJson(object);
            return instance.miniMessageStrict.serialize(instance.gsonComponentSerializer.deserialize(json));
        });
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
     * Surrounds the provided text with the default nameplates font.
     *
     * @param text the text to surround
     * @return the text surrounded by the default nameplates font tag
     */
    public static String surroundWithNameplatesFont(String text) {
        return surroundWithMiniMessageFont(text, ConfigManager.namespace() + ":" + ConfigManager.font());
    }

    /**
     * Removes shadow from the provided text using a specific color code trick.
     *
     * @param text the text to modify
     * @return the text wrapped in a color tag to remove shadows
     */
    public static String removeShadowTricky(String text) {
        return "<#FFFEFD>" + text + "</#FFFEFD>";
    }

    /**
     * Removes shadow from the provided text using a specific color code.
     *
     * @param text the text to modify
     * @return the text wrapped in a color tag to remove shadows
     */
    public static String removeShadow(String text) {
        return "<shadow:#00000000><#F0F0F0>" + text + "</#F0F0F0></shadow>";
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
                case 'a', 'A' -> stringBuilder.append("<green>");
                case 'b', 'B' -> stringBuilder.append("<aqua>");
                case 'c', 'C' -> stringBuilder.append("<red>");
                case 'd', 'D' -> stringBuilder.append("<light_purple>");
                case 'e', 'E' -> stringBuilder.append("<yellow>");
                case 'f', 'F' -> stringBuilder.append("<white>");
                case 'r', 'R' -> stringBuilder.append("<reset>");
                case 'l', 'L' -> stringBuilder.append("<b>");
                case 'm', 'M' -> stringBuilder.append("<st>");
                case 'o', 'O' -> stringBuilder.append("<i>");
                case 'n', 'N' -> stringBuilder.append("<u>");
                case 'k', 'K' -> stringBuilder.append("<obf>");
                case 'x', 'X' -> {
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
