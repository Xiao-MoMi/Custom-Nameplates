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

package net.momirealms.customnameplates.common.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * A utility class for loading and retrieving properties from a custom properties file.
 * <p>
 * This class loads the properties from the {@code custom-nameplates.properties} file
 * located in the classpath and provides a way to retrieve property values by key.
 * The properties are loaded and stored in a singleton instance to ensure only
 * one loading of the properties file throughout the lifetime of the application.
 * </p>
 */
public class CustomNameplatesProperties {

    /**
     * A map that holds the key-value pairs of properties.
     */
    private final HashMap<String, String> propertyMap;

    /**
     * Private constructor that initializes the property map.
     *
     * @param propertyMap the map holding the property key-value pairs
     */
    private CustomNameplatesProperties(HashMap<String, String> propertyMap) {
        this.propertyMap = propertyMap;
    }

    /**
     * Retrieves the value of a property based on the provided key.
     * <p>
     * This method throws a {@link RuntimeException} if the key is not found in the properties file.
     * </p>
     *
     * @param key the property key to retrieve
     * @return the value associated with the given key
     * @throws RuntimeException if the key is unknown or not found in the properties file
     */
    public static String getValue(String key) {
        if (!SingletonHolder.INSTANCE.propertyMap.containsKey(key)) {
            throw new RuntimeException("Unknown key: " + key);
        }
        return SingletonHolder.INSTANCE.propertyMap.get(key);
    }

    /**
     * A static inner class to hold the singleton instance of {@link CustomNameplatesProperties}.
     * This class is used to lazily load the properties file and ensure only one instance is created.
     */
    private static class SingletonHolder {

        /**
         * The singleton instance of {@link CustomNameplatesProperties}.
         */
        private static final CustomNameplatesProperties INSTANCE = getInstance();

        /**
         * Loads the properties file and creates an instance of {@link CustomNameplatesProperties}.
         *
         * @return an instance of {@link CustomNameplatesProperties} containing the loaded properties
         * @throws RuntimeException if there is an error reading the properties file
         */
        private static CustomNameplatesProperties getInstance() {
            try (InputStream inputStream = CustomNameplatesProperties.class.getClassLoader().getResourceAsStream("custom-nameplates.properties")) {
                HashMap<String, String> versionMap = new HashMap<>();
                Properties properties = new Properties();
                properties.load(inputStream);
                for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                    if (entry.getKey() instanceof String key && entry.getValue() instanceof String value) {
                        versionMap.put(key, value);
                    }
                }
                return new CustomNameplatesProperties(versionMap);
            } catch (IOException e) {
                throw new RuntimeException("Error loading properties file", e);
            }
        }
    }
}
