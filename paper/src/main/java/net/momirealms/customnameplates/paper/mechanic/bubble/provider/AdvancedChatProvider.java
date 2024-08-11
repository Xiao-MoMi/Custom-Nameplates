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

package net.momirealms.customnameplates.paper.mechanic.bubble.provider;

import net.advancedplugins.chat.api.AdvancedChannelChatEvent;
import net.advancedplugins.chat.api.AdvancedChatAPI;
import net.advancedplugins.chat.channel.ChatChannel;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.BubbleManager;
import net.momirealms.customnameplates.api.mechanic.bubble.provider.AbstractChatProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;

import java.util.List;

public class AdvancedChatProvider extends AbstractChatProvider {

    public AdvancedChatProvider(BubbleManager chatBubblesManager) {
        super(chatBubblesManager);
    }

    @Override
    public void register() {
        Bukkit.getPluginManager().registerEvents(this, CustomNameplatesPlugin.get());
    }

    @Override
    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public boolean hasJoinedChannel(Player player, String channelID) {
        ChatChannel chatChannel = AdvancedChatAPI.getApi().getPlayerChatChannel(player.getUniqueId());
        if (chatChannel == null) {
            return false;
        }
        return chatChannel.getSectionName().equals(channelID);
    }

    @Override
    public boolean canJoinChannel(Player player, String channelID) {
        List<ChatChannel> channelList = AdvancedChatAPI.getApi().getAllowedChatChannels(player);
        for (ChatChannel chatChannel : channelList) {
            if (chatChannel.getSectionName().equals(channelID)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isIgnoring(Player sender, Player receiver) {
        return AdvancedChatAPI.getApi().getIgnoredPlayers(receiver).contains(sender.getName());
    }

    @EventHandler (ignoreCancelled = true)
    public void onAdvancedChat(AdvancedChannelChatEvent event) {
        String channel = event.getChannel().getSectionName();
        for (String black : chatBubblesManager.getBlacklistChannels()) {
            if (channel.equals(black)) return;
        }
        if (event.getSender() instanceof Player player) {
            if (!player.isOnline())
                return;
            CustomNameplatesPlugin.get().getScheduler().runTaskAsync(() -> {
                chatBubblesManager.onChat(player, event.getMessage(), channel);
            });
        }
    }
}