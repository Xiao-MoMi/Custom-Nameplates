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

package net.momirealms.customnameplates.api.feature.chat;

import net.momirealms.customnameplates.api.CNPlayer;

/**
 * Provides chat-related functionality, including channel membership and ignore status between players.
 */
public interface ChatMessageProvider {

    /**
     * Checks if a player has joined a specific chat channel.
     *
     * @param player    the player to check
     * @param channelID the ID of the chat channel
     * @return true if the player has joined the channel, false otherwise
     */
    boolean hasJoinedChannel(CNPlayer player, String channelID);

    /**
     * Checks if a player is allowed to join a specific chat channel.
     *
     * @param player    the player to check
     * @param channelID the ID of the chat channel
     * @return true if the player can join the channel, false otherwise
     */
    boolean canJoinChannel(CNPlayer player, String channelID);

    /**
     * Checks if a player is ignoring another player.
     *
     * @param sender   the player sending messages
     * @param receiver the player who may be ignoring the sender
     * @return true if the receiver is ignoring the sender, false otherwise
     */
    boolean isIgnoring(CNPlayer sender, CNPlayer receiver);
}
