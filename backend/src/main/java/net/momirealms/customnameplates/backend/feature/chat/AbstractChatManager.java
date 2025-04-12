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

package net.momirealms.customnameplates.backend.feature.chat;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.ChatListener;
import net.momirealms.customnameplates.api.feature.chat.AbstractChatMessageProvider;
import net.momirealms.customnameplates.api.feature.chat.ChatManager;
import net.momirealms.customnameplates.api.feature.chat.ChatMessageProvider;
import net.momirealms.customnameplates.api.feature.chat.emoji.EmojiProvider;
import net.momirealms.customnameplates.api.helper.AdventureHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractChatManager implements ChatManager {

    protected final CustomNameplates plugin;
    protected final List<EmojiProvider> emojiProviders = new ArrayList<>();
    protected final List<ChatListener> listeners = new ArrayList<>();
    protected ChatMessageProvider chatProvider;
    protected ChatMessageProvider customProvider;

    public AbstractChatManager(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    @Override
    public void unload() {
        this.emojiProviders.clear();
        if (chatProvider instanceof AbstractChatMessageProvider chatMessageProvider) {
            chatMessageProvider.unregister();
        }
        chatProvider = null;
    }

    @Override
    public void load() {
        setUpPlatformEmojiProviders();
        if (customProvider == null) {
            setUpPlatformProvider();
        } else {
            chatProvider = customProvider;
        }
        if (chatProvider instanceof AbstractChatMessageProvider chatMessageProvider) {
            chatMessageProvider.register();
        }
    }

    protected abstract void setUpPlatformProvider();

    protected abstract void setUpPlatformEmojiProviders();

    @Override
    public String replaceEmojis(CNPlayer player, String text) {
        for (EmojiProvider emojiProvider : emojiProviders) {
            text = emojiProvider.replace(player, text);
        }
        return text;
    }

    @Override
    public boolean setCustomChatProvider(ChatMessageProvider provider) {
        if (this.customProvider != null)
            return false;
        this.customProvider = provider;
        this.reload();
        return true;
    }

    @Override
    public boolean removeCustomChatProvider() {
        if (this.customProvider != null) {
            this.customProvider = null;
            this.reload();
            return true;
        }
        return false;
    }

    @Override
    public void disable() {
        unload();
        this.listeners.clear();
    }

    @Override
    public void registerListener(final ChatListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void unregisterListener(final ChatListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public ChatMessageProvider chatProvider() {
        return chatProvider;
    }

    @Override
    public void onChat(CNPlayer player, String message, String channel) {
        String text = message;
        plugin.debug(() -> player.name() + " says [" + message + "]");
        for (EmojiProvider provider : emojiProviders) {
            text = provider.replace(player, text);
        }
        text = AdventureHelper.legacyToMiniMessage(text);
        for (ChatListener listener : listeners) {
            listener.onPlayerChat(player, text, channel);
        }
    }
}
