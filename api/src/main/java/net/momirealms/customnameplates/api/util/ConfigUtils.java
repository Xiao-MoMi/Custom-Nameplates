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

package net.momirealms.customnameplates.api.util;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.CarouselText;
import net.momirealms.customnameplates.api.requirement.Requirement;

import java.io.File;
import java.util.*;

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

    public static List<File> getConfigsDeeply(File configFolder) {
        ArrayList<File> validConfigs = new ArrayList<>();
        Deque<File> fileDeque = new ArrayDeque<>();
        fileDeque.push(configFolder);
        while (!fileDeque.isEmpty()) {
            File file = fileDeque.pop();
            File[] files = file.listFiles();
            if (files == null) continue;
            for (File subFile : files) {
                if (subFile.isDirectory()) {
                    fileDeque.push(subFile);
                } else if (subFile.isFile() && subFile.getName().endsWith(".yml")) {
                    validConfigs.add(subFile);
                }
            }
        }
        validConfigs.sort(Comparator.comparing(File::getName));
        return validConfigs;
    }

    public static File getFileInTheSameFolder(File file, String fileName) {
        File folder = file.getParentFile();
        return new File(folder, fileName);
    }

    public static <T> T safeCast(Object object, Class<T> clazz) {
        try {
            return clazz.cast(object);
        } catch (ClassCastException e) {
            CustomNameplates.getInstance().getPluginLogger().severe("Cannot cast " + object.getClass().getSimpleName() + " to " + clazz.getSimpleName(), e);
            return null;
        }
    }
}
