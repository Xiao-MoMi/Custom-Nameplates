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

package net.momirealms.customnameplates.api.feature.nameplate;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.common.plugin.feature.Reloadable;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Manages the nameplates system, providing functionality to retrieve, check access,
 * and manipulate nameplates associated with players.
 */
public interface NameplateManager extends Reloadable {

    /**
     * Retrieves a Nameplate by its unique ID.
     *
     * @param id the unique ID of the nameplate
     * @return the Nameplate instance, or null if not found
     */
    @Nullable
    Nameplate nameplateById(String id);

    /**
     * Returns a collection of all available nameplates.
     *
     * @return a collection of Nameplate instances
     */
    Collection<Nameplate> nameplates();

    /**
     * Checks if a player has access to a specific nameplate by its ID.
     *
     * @param player the player to check
     * @param id     the ID of the nameplate
     * @return true if the player has access, false otherwise
     */
    boolean hasNameplate(CNPlayer player, String id);

    /**
     * Returns a collection of nameplates available to the player.
     *
     * @param player the player to check
     * @return a collection of available Nameplate instances
     */
    Collection<Nameplate> availableNameplates(CNPlayer player);

    /**
     * Returns the ID of the default nameplate to be used when no specific nameplate is set.
     *
     * @return the default nameplate ID
     */
    String defaultNameplateId();

    /**
     * Returns the player's name tag to be displayed on the nameplate.
     *
     * @return the player's name tag
     */
    String playerNameTag();
}
