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

package net.momirealms.customnameplates.bukkit.compatibility.disguise;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class DisguiseUtils {

    public static boolean isDisguised(Entity entity) {
        return DisguiseAPI.isDisguised(entity);
    }

    public static double getDisguisedHeight(Entity entity) {
        Disguise disguise = DisguiseAPI.getDisguise(entity);
        return disguise.getHeight() + disguise.getWatcher().getYModifier() + disguise.getWatcher().getNameYModifier();
    }

    public static EntityType getDisguisedType(Entity entity) {
        return DisguiseAPI.getDisguise(entity).getType().getEntityType();
    }
}
