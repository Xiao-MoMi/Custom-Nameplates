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

package net.momirealms.customnameplates.bukkit.compatibility.chat;

import net.advancedplugins.chat.api.AdvancedChannelChatEvent;
import net.advancedplugins.chat.api.AdvancedChatAPI;
import net.advancedplugins.chat.api.AdvancedChatEvent;
import net.advancedplugins.chat.channel.ChatChannel;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.chat.AbstractChatMessageProvider;
import net.momirealms.customnameplates.api.feature.chat.ChatManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Objects;

public class AdvancedChatProvider extends AbstractChatMessageProvider implements Listener {

    public AdvancedChatProvider(CustomNameplates plugin, ChatManager manager) {
        super(plugin, manager);
    }

    @EventHandler(ignoreCancelled = true)
    public void onAdvancedChat(AdvancedChatEvent event) {
        Player player = event.getChatEvent().getPlayer();
        if (!player.isOnline()) return;
        CNPlayer cnPlayer = plugin.getPlayer(player.getUniqueId());
        if (cnPlayer == null) return;
        plugin.getScheduler().async().execute(() -> {
            manager.onChat(cnPlayer, event.getMessage(), "");
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onAdvancedChat(AdvancedChannelChatEvent event) {
        String channel = event.getChannel().getSectionName();
        if (event.getSender() instanceof Player player) {
            if (!player.isOnline()) return;
            CNPlayer cnPlayer = plugin.getPlayer(player.getUniqueId());
            if (cnPlayer == null) return;
            plugin.getScheduler().async().execute(() -> {
                manager.onChat(cnPlayer, event.getMessage(), channel);
            });
        }
    }

    @Override
    public boolean hasJoinedChannel(CNPlayer player, String channelID) {
        ChatChannel chatChannel = AdvancedChatAPI.getApi().getPlayerChatChannel(player.uuid());
        if (chatChannel == null) {
            return false;
        }
        return chatChannel.getSectionName().equals(channelID);
    }

    @Override
    public boolean canJoinChannel(CNPlayer player, String channelID) {
        ChatChannel chatChannel = AdvancedChatAPI.getApi().getPlayerChatChannel(player.uuid());
        if (chatChannel == null) {
            return false;
        }
        return chatChannel.getSectionName().equals(channelID);
    }

    @Override
    public boolean isIgnoring(CNPlayer sender, CNPlayer receiver) {
        return AdvancedChatAPI.getApi().getIgnoredPlayers((Player) receiver.player()).contains(sender.name());
    }

    @Override
    public void register() {
        Bukkit.getPluginManager().registerEvents(this, Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("CustomNameplates")));
    }

    @Override
    public void unregister() {
        HandlerList.unregisterAll(this);
    }
}