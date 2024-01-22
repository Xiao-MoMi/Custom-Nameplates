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

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.common.Pair;
import net.momirealms.customnameplates.paper.mechanic.misc.TimeLimitText;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class ConfigUtils {

    private ConfigUtils() {}

    /**
     * Converts an object into an ArrayList of strings.
     *
     * @param object The input object
     * @return An ArrayList of strings
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<String> stringListArgs(Object object) {
        ArrayList<String> list = new ArrayList<>();
        if (object instanceof String member) {
            list.add(member);
        } else if (object instanceof List<?> members) {
            list.addAll((Collection<? extends String>) members);
        } else if (object instanceof String[] strings) {
            list.addAll(List.of(strings));
        }
        return list;
    }

    /**
     * Splits a string into a pair of integers using the "~" delimiter.
     *
     * @param value The input string
     * @return A Pair of integers
     */
    public static Pair<Integer, Integer> splitStringIntegerArgs(String value, String regex) {
        String[] split = value.split(regex);
        return Pair.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    /**
     * Converts an object into a double value.
     *
     * @param arg The input object
     * @return A double value
     */
    public static double getDoubleValue(Object arg) {
        if (arg instanceof Double d) {
            return d;
        } else if (arg instanceof Integer i) {
            return Double.valueOf(i);
        }
        return 0;
    }

    public static TimeLimitText[] getTimeLimitTexts(ConfigurationSection section) {

        TreeMap<Integer, TimeLimitText> map = new TreeMap<>();
        if (section == null) {
            LogUtils.warn("No text display order set, this might cause bugs!");
            map.put(1, new TimeLimitText(100, -1, "", new Requirement[0]));
            return getOrderedTexts(map);
        }

        for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
            if (!(entry.getValue() instanceof ConfigurationSection textSection))
                continue;

            var text = TimeLimitText.Builder.of()
                    .text(textSection.getString("text", ""))
                    .refreshFrequency(textSection.getInt("refresh-frequency", -1))
                    .duration(textSection.getInt("duration", 200))
                    .requirement(CustomNameplatesPlugin.get().getRequirementManager().getRequirements(textSection.getConfigurationSection("conditions")))
                    .build();

            int order;
            try {
                order = Integer.parseInt(entry.getKey());
            } catch (NumberFormatException e) {
                LogUtils.warn("Invalid order format: " + entry.getKey());
                continue;
            }

            map.put(order, text);
        }

        return getOrderedTexts(map);
    }

    private static TimeLimitText[] getOrderedTexts(TreeMap<Integer, TimeLimitText> map) {
        return map.values().toArray(new TimeLimitText[0]);
    }

    public static void saveResource(@NotNull String resourcePath) {
        if (resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        if (in == null) {
            return;
        }

        File outFile = new File(CustomNameplatesPlugin.get().getDataFolder(), resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(CustomNameplatesPlugin.get().getDataFolder(), resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists()) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            }
        } catch (IOException ex) {
            LogUtils.warn("Could not save " + outFile.getName() + " to " + outFile);
        }
    }

    @Nullable
    public static InputStream getResource(@NotNull String filename) {
        try {
            URL url = CustomNameplatesPlugin.get().getClass().getClassLoader().getResource(filename);
            if (url == null) {
                return null;
            }
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    public static String native2ascii(char c) {
        StringBuilder stringBuilder_1 = new StringBuilder("\\u");
        StringBuilder stringBuilder_2 = new StringBuilder(Integer.toHexString(c));
        stringBuilder_2.reverse();
        for (int n = 4 - stringBuilder_2.length(), i = 0; i < n; i++) stringBuilder_2.append('0');
        for (int j = 0; j < 4; j++) stringBuilder_1.append(stringBuilder_2.charAt(3 - j));
        return stringBuilder_1.toString();
    }

    public static char[] convertUnicodeStringToChars(String unicodeString) {
        String processedString = unicodeString.replace("\\u", "");
        int length = processedString.length() / 4;
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            int codePoint = Integer.parseInt(processedString.substring(i * 4, i * 4 + 4), 16);
            if (Character.isSupplementaryCodePoint(codePoint)) {
                chars[i] = Character.highSurrogate(codePoint);
                chars[++i] = Character.lowSurrogate(codePoint);
            } else {
                chars[i] = (char) codePoint;
            }
        }
        return chars;
    }
}
