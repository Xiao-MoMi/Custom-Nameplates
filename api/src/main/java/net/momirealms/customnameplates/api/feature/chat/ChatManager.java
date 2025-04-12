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
import net.momirealms.customnameplates.api.feature.ChatListener;
import net.momirealms.customnameplates.common.plugin.feature.Reloadable;

/**
 * Manages chat-related functionality, including handling custom chat providers, listeners, and chat events.
 */
public interface ChatManager extends Reloadable {

    String replaceEmojis(CNPlayer player, String text);

    /**
     * Sets a custom chat message provider.
     *
     * @param provider the custom ChatMessageProvider to set
     * @return true if the provider was successfully set, false otherwise
     */
    boolean setCustomChatProvider(ChatMessageProvider provider);

    /**
     * Removes the currently set custom chat message provider.
     *
     * @return true if the provider was successfully removed, false otherwise
     */
    boolean removeCustomChatProvider();

    /**
     * Registers a chat listener to receive chat events.
     *
     * @param listener the ChatListener to register
     */
    void registerListener(ChatListener listener);

    /**
     * Unregisters a chat listener, stopping it from receiving chat events.
     *
     * @param listener the ChatListener to unregister
     */
    void unregisterListener(ChatListener listener);

    /**
     * Returns the current chat message provider.
     *
     * @return the currently set ChatMessageProvider, or null if none is set
     */
    ChatMessageProvider chatProvider();

    /**
     * Handles a chat message event for the specified player and channel.
     *
     * @param player  the player who sent the message
     * @param message the chat message content
     * @param channel the chat channel
     */
    void onChat(CNPlayer player, String message, String channel);
}
