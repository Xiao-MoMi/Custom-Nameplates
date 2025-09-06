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

package net.momirealms.customnameplates.bukkit;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.helper.AdventureHelper;
import net.momirealms.customnameplates.api.helper.VersionHelper;
import net.momirealms.customnameplates.backend.feature.chat.AbstractChatManager;
import net.momirealms.customnameplates.bukkit.compatibility.chat.*;
import net.momirealms.customnameplates.bukkit.compatibility.emoji.ItemsAdderEmojiProvider;
import net.momirealms.customnameplates.bukkit.compatibility.emoji.OraxenEmojiProvider;
import org.bukkit.Bukkit;

public class BukkitChatManager extends AbstractChatManager {

    public BukkitChatManager(CustomNameplates plugin) {
        super(plugin);
    }

    @Override
    protected void setUpPlatformProvider() {
        if (ConfigManager.chatTrChat() && Bukkit.getPluginManager().isPluginEnabled("TrChat")) {
            this.chatProvider = new TrChatProvider(plugin, this);
            plugin.getPluginLogger().info("TrChat hooked!");
        } else if (ConfigManager.chatVenture() && Bukkit.getPluginManager().isPluginEnabled("VentureChat")) {
            this.chatProvider = new VentureChatProvider(plugin, this);
            plugin.getPluginLogger().info("VentureChat hooked!");
        } else if (ConfigManager.chatHusk() && Bukkit.getPluginManager().isPluginEnabled("HuskChat")) {
            this.chatProvider = new HuskChatProvider(plugin, this);
            plugin.getPluginLogger().info("HuskChat hooked!");
        } else if (ConfigManager.chatCarbon() && Bukkit.getPluginManager().isPluginEnabled("CarbonChat")) {
            this.chatProvider = new CarbonChatProvider(plugin, this);
            plugin.getPluginLogger().info("CarbonChat hooked!");
        } else if (ConfigManager.chatAdvanced() && Bukkit.getPluginManager().isPluginEnabled("AdvancedChat")) {
            this.chatProvider = new AdvancedChatProvider(plugin, this);
            plugin.getPluginLogger().info("AdvancedChat hooked!");
        } else if (ConfigManager.chatEss() && Bukkit.getPluginManager().isPluginEnabled("EssentialsChat")) {
            this.chatProvider = new EssentialsChatProvider(plugin, this);
            plugin.getPluginLogger().info("EssentialsChat hooked!");
        } else if (ConfigManager.chatChatControlRed() && Bukkit.getPluginManager().isPluginEnabled("ChatControl")) {
            this.chatProvider = new ChatControlRedProvider(plugin, this);
            plugin.getPluginLogger().info("ChatControl hooked!");
        } else if (ConfigManager.chatChatty() && Bukkit.getPluginManager().isPluginEnabled("Chatty")) {
            this.chatProvider = new ChattyProvider(plugin, this);
            plugin.getPluginLogger().info("Chatty hooked!");
        } else if (VersionHelper.isPaperOrItsForks()) {
            this.chatProvider = new PaperAsyncChatProvider(plugin, this);
        } else {
            this.chatProvider = new AsyncChatProvider(plugin, this);
        }
    }

    @Override
    protected void setUpPlatformEmojiProviders() {
        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
            this.emojiProviders.add(new ItemsAdderEmojiProvider());
            plugin.debug(() -> "ItemsAdderEmojiProvider Enabled");
        }
        if (Bukkit.getPluginManager().getPlugin("Oraxen") != null) {
            try {
                this.emojiProviders.add(new OraxenEmojiProvider(Bukkit.getPluginManager().getPlugin("Oraxen").getDescription().getVersion().startsWith("1") ? 1 : 2));
            } catch (Exception ignore) {
            }
        }
    }

    @Override
    public void onChat(CNPlayer player, String message, String channel) {
        if (ConfigManager.stripChatColorTags()) {
            super.onChat(player, AdventureHelper.stripTags(AdventureHelper.legacyToMiniMessage(message)), channel);
        } else {
            super.onChat(player, message, channel);
        }
    }
}
