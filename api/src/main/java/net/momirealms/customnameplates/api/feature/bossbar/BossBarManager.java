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

package net.momirealms.customnameplates.api.feature.bossbar;

import net.momirealms.customnameplates.common.plugin.feature.Reloadable;

/**
 * Manager interface for handling BossBar configurations and their updates.
 */
public interface BossBarManager extends Reloadable {

    /**
     * Retrieves the BossBar configuration by its id.
     *
     * @param id the name of the BossBar configuration
     * @return the corresponding BossBarConfig, or null if not found
     */
    BossBarConfig configById(String id);

    /**
     * Returns all available BossBar configurations.
     *
     * @return an array of all BossBarConfig instances
     */
    BossBarConfig[] bossBarConfigs();

    /**
     * Called on every server tick to update the state of BossBars.
     */
    void onTick();
}
