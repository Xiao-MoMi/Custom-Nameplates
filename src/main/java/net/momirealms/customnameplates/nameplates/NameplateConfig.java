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

import org.bukkit.ChatColor;

import java.util.Objects;

public record NameplateConfig(ChatColor color, int height, String name, int yoffset, String bubbleColor) {

    public static NameplateConfig EMPTY = new NameplateConfig(ChatColor.WHITE, 16, "none", 12, "<white>");

    public ChatColor getColor() {
        return this.color;
    }

    public int getHeight() {
        return this.height;
    }

    public String getName() {
        return this.name;
    }

    public int getYOffset() {
        return this.yoffset;
    }

    public String bubbleColor() {
        return bubbleColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NameplateConfig that = (NameplateConfig) o;
        return height == that.height && yoffset == that.yoffset && color == that.color && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, height, name, yoffset);
    }
}
