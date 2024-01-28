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

package net.momirealms.customnameplates.api.manager;

import net.momirealms.customnameplates.api.mechanic.tag.unlimited.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface UnlimitedTagManager {

    @NotNull StaticTextEntity createNamedEntity(EntityTagEntity entity, StaticTextTagSetting setting);

    /**
     * Create a named entity (ArmorStand) for a player
     * To apply the changes, you should add it the named player instance
     *
     * @param player player
     * @param setting setting
     * @return named entity
     */
    @NotNull
    DynamicTextEntity createNamedEntity(EntityTagPlayer player, DynamicTextTagSetting setting);

    EntityTagEntity createOrGetTagForEntity(Entity entity);

    EntityTagPlayer createOrGetTagForPlayer(Player player);
}
