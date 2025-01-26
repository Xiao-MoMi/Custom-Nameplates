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

import net.momirealms.customnameplates.api.CustomNameplates;

/**
 * Abstract base class for chat message providers, providing common functionality
 * for managing the plugin and chat manager.
 */
public abstract class AbstractChatMessageProvider implements ChatMessageProvider {
    /**
     * The CustomNameplates plugin
     */
    protected CustomNameplates plugin;
    /**
     * The chat manager
     */
    protected ChatManager manager;

    /**
     * Constructs a new AbstractChatMessageProvider.
     *
     * @param plugin  the CustomNameplates plugin instance
     * @param manager the ChatManager instance to handle chat-related operations
     */
    public AbstractChatMessageProvider(CustomNameplates plugin, ChatManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    /**
     * Registers the chat message provider. Intended to be overridden by subclasses if needed.
     */
    public void register() {
    }

    /**
     * Unregisters the chat message provider. Intended to be overridden by subclasses if needed.
     */
    public void unregister() {
    }
}
