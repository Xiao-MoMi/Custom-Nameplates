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

package net.momirealms.customnameplates.api.event;

import net.momirealms.customnameplates.api.storage.data.PlayerData;
import net.momirealms.customnameplates.common.event.NameplatesEvent;
import net.momirealms.customnameplates.common.event.Param;

/**
 * Represents an event that is triggered when player data is loaded.
 * This event provides access to the {@link PlayerData} for further processing.
 */
public interface DataLoadEvent extends NameplatesEvent {

    /**
     * Returns the {@link PlayerData} associated with this event.
     *
     * @return the loaded player data
     */
    @Param(0)
    PlayerData data();
}
