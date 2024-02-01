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

package net.momirealms.customnameplates.api.mechanic.tag.team;

import net.momirealms.customnameplates.api.mechanic.misc.ViewerText;
import net.momirealms.customnameplates.api.mechanic.tag.NameplatePlayer;

public interface TeamTagPlayer extends NameplatePlayer {

    /**
     * Set a player's prefix
     *
     * @param prefix prefix
     */
    void setPrefix(String prefix);

    /**
     * Set a player's suffix
     *
     * @param suffix suffix
     */
    void setSuffix(String suffix);

    /**
     * Get a player's prefix
     *
     * @return prefix
     */
    ViewerText getPrefix();

    /**
     * Get a player's suffix
     *
     * @return suffix
     */
    ViewerText getSuffix();

    /**
     * Destroy the tag
     */
    void destroy();
}
