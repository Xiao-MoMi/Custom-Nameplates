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

package net.momirealms.customnameplates.common.plugin.feature;

/**
 * Interface for objects that can be reloaded, unloaded, and loaded again.
 */
public interface Reloadable {

    /**
     * Reloads the feature or component by first unloading it and then loading it again.
     */
    default void reload() {
        unload();
        load();
    }

    /**
     * Unloads the feature or component.
     */
    default void unload() {
    }

    /**
     * Loads the feature or component.
     */
    default void load() {
    }

    /**
     * Disables the feature or component by unloading it.
     */
    default void disable() {
        unload();
    }
}
