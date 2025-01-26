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

/**
 * Utility class providing various helper methods for handling configuration data
 */
public class ConfigUtils {

    private ConfigUtils() {}

    /**
     * Creates a {@link CarouselText} object from the provided configuration section.
     *
     * @param section the configuration section containing properties for the CarouselText.
     * @return a {@link CarouselText} instance created from the section data.
     */
    public static CarouselText carouselText(Section section) {
        return new CarouselText(
                section.getInt("duration", 200),
                CustomNameplates.getInstance().getRequirementManager().parseRequirements(section.getSection("conditions")),
                section.getString("text", ""),
                section.getBoolean("update-on-display", true)
        );
    }

    /**
     * Creates an array of {@link CarouselText} objects, sorted by their order from the given configuration section.
     *
     * @param section the configuration section containing multiple CarouselText entries.
     * @return an array of {@link CarouselText} instances, sorted by order.
     */
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

    /**
     * Converts a comma-separated string to an ARGB color value.
     *
     * @param arg the comma-separated string representing the ARGB color.
     * @return the corresponding ARGB color integer value.
     */
    public static int argb(String arg) {
        return argb(arg.split(","));
    }

    /**
     * Converts an array of strings to an ARGB color value.
     *
     * @param args an array of strings representing ARGB components (alpha, red, green, blue).
     * @return the corresponding ARGB color integer value.
     */
    public static int argb(String[] args) {
        int alpha = args.length >= 1 ? Integer.parseInt(args[0]) : 64;
        int red = args.length >= 2 ? Integer.parseInt(args[1]) : 0;
        int green = args.length >= 3 ? Integer.parseInt(args[2]) : 0;
        int blue = args.length >= 4 ? Integer.parseInt(args[3]) : 0;
        return argb(alpha, red, green, blue);
    }

    /**
     * Converts the given ARGB components to a single ARGB integer value.
     *
     * @param alpha the alpha component of the color.
     * @param red the red component of the color.
     * @param green the green component of the color.
     * @param blue the blue component of the color.
     * @return the corresponding ARGB color integer value.
     */
    public static int argb(int alpha, int red, int green, int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    /**
     * Converts a comma-separated string to a {@link Vector3} object.
     *
     * @param arg the comma-separated string representing the vector (x, y, z).
     * @return the corresponding {@link Vector3} object.
     */
    public static Vector3 vector3(String arg) {
        return vector3(arg.split(","));
    }

    /**
     * Converts an array of strings to a {@link Vector3} object.
     *
     * @param args an array of strings representing the x, y, and z components of the vector.
     * @return the corresponding {@link Vector3} object.
     */
    public static Vector3 vector3(String[] args) {
        float x = args.length >= 1 ? Float.parseFloat(args[0]) : 0f;
        float y = args.length >= 2 ? Float.parseFloat(args[1]) : 0f;
        float z = args.length >= 3 ? Float.parseFloat(args[2]) : 0f;
        return vector3(x, y, z);
    }

    /**
     * Creates a {@link Vector3} object from the provided float components.
     *
     * @param x the x component of the vector.
     * @param y the y component of the vector.
     * @param z the z component of the vector.
     * @return the corresponding {@link Vector3} object.
     */
    public static Vector3 vector3(float x, float y, float z) {
        return new Vector3(x, y, z);
    }

    /**
     * Recursively retrieves all configuration files with a ".yml" extension from the given folder and its subfolders.
     *
     * @param configFolder the root folder to start searching for YAML files.
     * @return a list of valid configuration files sorted by name.
     */
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

    /**
     * Retrieves a file located in the same folder as the provided file, with a new name.
     *
     * @param file the base file.
     * @param fileName the name of the file to retrieve.
     * @return the file located in the same folder with the specified name.
     */
    public static File getFileInTheSameFolder(File file, String fileName) {
        File folder = file.getParentFile();
        return new File(folder, fileName);
    }

    /**
     * Safely casts an object to the specified type, logging an error if the cast fails.
     *
     * @param object the object to cast.
     * @param clazz the class type to cast to.
     * @param <T> the type to cast to.
     * @return the cast object if successful, or null if the cast fails.
     */
    public static <T> T safeCast(Object object, Class<T> clazz) {
        try {
            return clazz.cast(object);
        } catch (ClassCastException e) {
            CustomNameplates.getInstance().getPluginLogger().severe("Cannot cast " + object.getClass().getSimpleName() + " to " + clazz.getSimpleName(), e);
            return null;
        }
    }
}

