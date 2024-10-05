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

package net.momirealms.customnameplates.api.feature;

import net.momirealms.customnameplates.api.CNPlayer;

/**
 * Represents a listener for player chat events, providing a callback method
 * when a player sends a message in a specific channel.
 */
public interface ChatListener {

    /**
     * Called when a player sends a chat message in a specified channel.
     *
     * @param player  the player who sent the message
     * @param message the content of the message
     * @param channel the channel where the message was sent
     */
    void onPlayerChat(CNPlayer player, String message, String channel);
}
