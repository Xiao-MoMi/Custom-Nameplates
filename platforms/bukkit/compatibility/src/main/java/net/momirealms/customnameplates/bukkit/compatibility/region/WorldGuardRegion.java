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

package net.momirealms.customnameplates.bukkit.compatibility.region;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.requirement.EmptyRequirement;

import java.util.HashSet;
import java.util.List;

public class WorldGuardRegion {

    public static void register() {
        CustomNameplates.getInstance().getRequirementManager().registerRequirement((args, interval) -> {
            HashSet<String> regions = new HashSet<>();
            int mode = 1;
            if (args instanceof Section section) {
                mode = section.getInt("mode", 1);
                regions.addAll(section.getStringList("values"));
            } else {
                if (args instanceof List<?> list) {
                    for (Object o : list) {
                        if (o instanceof String) {
                            regions.add((String) o);
                        }
                    }
                } else {
                    CustomNameplates.getInstance().getPluginLogger().warn("Invalid value type: " + args.getClass().getSimpleName() + " found at region requirement which is expected be `Section` or `StringList`");
                    return EmptyRequirement.instance();
                }
            }
            return new RegionRequirement(interval, mode, regions);
        }, "region");
    }
}
