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
}
