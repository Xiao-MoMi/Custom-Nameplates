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

package net.momirealms.customnameplates.paper.mechanic.bossbar;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum Overlay {

    NOTCHED_6,
    NOTCHED_10,
    NOTCHED_12,
    NOTCHED_20,
    PROGRESS;

    public static Overlay getOverlay(@NotNull String name) {
        try {
            return Overlay.valueOf(name.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Overlay: " + name + " doesn't exist");
        }
    }
}
