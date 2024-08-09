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

import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.api.events.VentureChatEvent;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.BubbleManager;
import net.momirealms.customnameplates.api.mechanic.bubble.provider.AbstractChatProvider;
import net.momirealms.customnameplates.api.util.LogUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;

public class VentureChatProvider extends AbstractChatProvider {

    public VentureChatProvider(BubbleManager chatBubblesManager) {
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
        MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer(player);
        return mcp.getCurrentChannel().getName().equals(channelID);
    }

    @Override
    public boolean canJoinChannel(Player player, String channelID) {
        ChatChannel channel = ChatChannel.getChannel(channelID);
        if (channel == null) {
            LogUtils.warn("Channel " + channelID + " doesn't exist.");
            return false;
        }
        if (channel.hasPermission()) {
            return player.hasPermission(channel.getPermission());
        }
        return true;
    }

    @Override
    public boolean isIgnoring(Player sender, Player receiver) {
        return false;
    }

    @EventHandler (ignoreCancelled = true)
    public void onVentureChat(VentureChatEvent event) {
        String channelName = event.getChannel().getName();
        for (String channel : chatBubblesManager.getBlacklistChannels()) {
            if (channelName.equals(channel)) return;
        }
        final MineverseChatPlayer chatPlayer = event.getMineverseChatPlayer();
        if (chatPlayer == null) {
            return;
        }
        CustomNameplatesPlugin.get().getScheduler().runTaskAsync(() -> {
            chatBubblesManager.onChat(chatPlayer.getPlayer(), event.getChat(), channelName);
        });
    }
}
