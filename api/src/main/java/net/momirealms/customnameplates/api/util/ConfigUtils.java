package net.momirealms.customnameplates.api.util;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.CarouselText;
import net.momirealms.customnameplates.api.requirement.Requirement;

import java.util.Map;
import java.util.TreeMap;

public class ConfigUtils {

    public static CarouselText carouselText(Section section) {
        return new CarouselText(
                section.getInt("duration", 200),
                CustomNameplates.getInstance().getRequirementManager().parseRequirements(section.getSection("conditions")),
                section.getString("text", ""),
                section.getBoolean("update-on-display", true)
        );
    }

    public static CarouselText[] carouselTexts(Section section) {
        TreeMap<Integer, CarouselText> map = new TreeMap<>();
        if (section == null) {
            CustomNameplates.getInstance().getPluginLogger().warn("text-display-order section is null, this might cause bugs!");
            map.put(1, new CarouselText(100, new Requirement[0], "", false));
            return map.values().toArray(new CarouselText[0]);
        }
        for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
            if (!(entry.getValue() instanceof Section inner))
                continue;
            int order;
            try {
                order = Integer.parseInt(entry.getKey());
            } catch (NumberFormatException e) {
                CustomNameplates.getInstance().getPluginLogger().warn("Invalid order format: " + entry.getKey());
                continue;
            }
            map.put(order, carouselText(inner));
        }
        return map.values().toArray(new CarouselText[0]);
    }

    public static int argb(String arg) {
        return argb(arg.split(","));
    }

    public static int argb(String[] args) {
        int alpha = args.length >= 1 ? Integer.parseInt(args[0]) : 64;
        int red = args.length >= 2 ? Integer.parseInt(args[1]) : 0;
        int green = args.length >= 3 ? Integer.parseInt(args[2]) : 0;
        int blue = args.length >= 4 ? Integer.parseInt(args[3]) : 0;
        return argb(alpha, red, green, blue);
    }

    public static int argb(int alpha, int red, int green, int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public static Vector3 vector3(String arg) {
        return vector3(arg.split(","));
    }

    public static Vector3 vector3(String[] args) {
        float x = args.length >= 1 ? Float.parseFloat(args[0]) : 0f;
        float y = args.length >= 2 ? Float.parseFloat(args[1]) : 0f;
        float z = args.length >= 3 ? Float.parseFloat(args[2]) : 0f;
        return vector3(x, y, z);
    }

    public static Vector3 vector3(float x, float y, float z) {
        return new Vector3(x, y, z);
    }
}
