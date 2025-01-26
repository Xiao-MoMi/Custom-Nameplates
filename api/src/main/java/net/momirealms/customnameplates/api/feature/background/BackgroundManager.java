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

package net.momirealms.customnameplates.api.feature.background;

import net.momirealms.customnameplates.common.plugin.feature.Reloadable;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Interface representing a manager for background configurations.
 */
public interface BackgroundManager extends Reloadable {

    /**
     * Retrieves a Background configuration by its unique ID.
     *
     * @param id the unique ID of the background
     * @return the corresponding Background, or null if not found
     */
    @Nullable
    Background backgroundById(String id);

    /**
     * Returns a collection of all available Background configurations.
     *
     * @return a collection of Background instances
     */
    Collection<Background> getBackgrounds();
}
