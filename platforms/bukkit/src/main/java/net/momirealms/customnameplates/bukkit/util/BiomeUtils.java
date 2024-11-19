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

package net.momirealms.customnameplates.bukkit.util;

import org.bukkit.Location;

public class BiomeUtils {

    public static String getBiome(Location loc) {
        try {
            Object serverLevel = Reflections.field$CraftWorld$ServerLevel.get(loc.getWorld());
            Object holder$Biome = Reflections.method$ServerLevel$getNoiseBiome.invoke(serverLevel, loc.getBlockX() >> 2, loc.getBlockY() >> 2, loc.getBlockZ() >> 2);
            Object biome = Reflections.method$Holder$value.invoke(holder$Biome);
            Object resourceLocation = Reflections.method$Registry$getKey.invoke(Reflections.instance$BiomeRegistry, biome);
            if (resourceLocation != null) {
                return resourceLocation.toString();
            }
            return "void";
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
