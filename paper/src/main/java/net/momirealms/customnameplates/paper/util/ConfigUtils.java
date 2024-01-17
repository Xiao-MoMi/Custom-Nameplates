package net.momirealms.customnameplates.paper.util;

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.common.Pair;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.paper.mechanic.misc.TimeLimitText;
import org.bukkit.configuration.ConfigurationSection;

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
}
