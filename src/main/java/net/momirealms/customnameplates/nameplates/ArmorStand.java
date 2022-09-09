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

package net.momirealms.customnameplates.nameplates;

import net.momirealms.customnameplates.objects.TextCache;
import org.bukkit.entity.Player;

public interface ArmorStand {

    void setOffset(double var1);

    double getOffset();

    TextCache getText();

    void teleport();

    void teleport(Player player);

    void setSneak(boolean isSneaking, boolean respawn);

    void destroy();

    void destroy(Player player);

    void refresh();

    int getEntityId();

    void spawn(Player player);

    void respawn(Player player);
}
