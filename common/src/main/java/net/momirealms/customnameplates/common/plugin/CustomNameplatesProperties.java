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

public class CustomNameplatesProperties {

    private final HashMap<String, String> propertyMap;

    private CustomNameplatesProperties(HashMap<String, String> propertyMap) {
        this.propertyMap = propertyMap;
    }

    public static String getValue(String key) {
        if (!SingletonHolder.INSTANCE.propertyMap.containsKey(key)) {
            throw new RuntimeException("Unknown key: " + key);
        }
        return SingletonHolder.INSTANCE.propertyMap.get(key);
    }

    private static class SingletonHolder {

        private static final CustomNameplatesProperties INSTANCE = getInstance();

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
                 throw new RuntimeException(e);
             }
        }
    }
}
