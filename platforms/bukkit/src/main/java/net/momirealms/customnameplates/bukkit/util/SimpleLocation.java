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

import net.momirealms.customnameplates.api.util.Vector3;

public class SimpleLocation {

    private String world;
    private Vector3 position;

    public SimpleLocation(String world, Vector3 position) {
        this.world = world;
        this.position = position;
    }

    public String world() {
        return world;
    }

    public void world(String world) {
        this.world = world;
    }

    public Vector3 position() {
        return position;
    }

    public void position(Vector3 position) {
        this.position = position;
    }
}
