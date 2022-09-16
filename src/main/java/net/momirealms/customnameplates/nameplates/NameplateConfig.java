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

import net.momirealms.customnameplates.objects.SimpleChar;
import org.bukkit.ChatColor;

public record NameplateConfig(ChatColor color, String name, SimpleChar left, SimpleChar middle, SimpleChar right) {

    public static NameplateConfig EMPTY = new NameplateConfig(ChatColor.WHITE, "none", SimpleChar.none, SimpleChar.none, SimpleChar.none);

    @Override
    public ChatColor color() {
        return color;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public SimpleChar left() {
        return left;
    }

    @Override
    public SimpleChar middle() {
        return middle;
    }

    @Override
    public SimpleChar right() {
        return right;
    }
}
