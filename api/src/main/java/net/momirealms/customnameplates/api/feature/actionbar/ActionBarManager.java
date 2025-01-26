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

package net.momirealms.customnameplates.api.feature.actionbar;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.common.plugin.feature.Reloadable;
import org.jetbrains.annotations.Nullable;

/**
 * Interface representing a manager for handling ActionBars.
 */
public interface ActionBarManager extends Reloadable {

    /**
     * Refreshes the conditions of all ActionBars.
     */
    void refreshConditions();

    /**
     * Checks heartbeats for any necessary updates or actions related to ActionBars.
     */
    void checkHeartBeats();

    /**
     * Retrieves the ActionBar configuration by its name.
     *
     * @param name the name of the ActionBar configuration
     * @return the corresponding ActionBarConfig, or null if not found
     */
    @Nullable
    ActionBarConfig configById(String name);

    /**
     * Returns all available ActionBar configurations.
     *
     * @return an array of all ActionBarConfig instances
     */
    ActionBarConfig[] actionBarConfigs();

    /**
     * Gets the external ActionBar for a specific player.
     *
     * @param player the player
     * @return the external ActionBar string for the player
     */
    @Nullable
    String getExternalActionBar(CNPlayer player);
}
