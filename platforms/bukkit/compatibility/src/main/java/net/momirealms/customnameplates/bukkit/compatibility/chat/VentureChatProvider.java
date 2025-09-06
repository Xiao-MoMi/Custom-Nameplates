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

import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.api.events.VentureChatEvent;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.chat.AbstractChatMessageProvider;
import net.momirealms.customnameplates.api.feature.chat.ChatManager;
import net.momirealms.customnameplates.api.helper.AdventureHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Objects;

public class VentureChatProvider extends AbstractChatMessageProvider implements Listener {

    public VentureChatProvider(CustomNameplates plugin, ChatManager manager) {
        super(plugin, manager);
    }

    @EventHandler(ignoreCancelled = true)
    public void onVentureChat(VentureChatEvent event) {
        String channelName = event.getChannel().getName();
        final MineverseChatPlayer chatPlayer = event.getMineverseChatPlayer();
        if (chatPlayer == null) {
            return;
        }
        Player player = chatPlayer.getPlayer();
        CNPlayer cnPlayer = plugin.getPlayer(player.getUniqueId());
        if (cnPlayer == null) {
            return;
        }
        String chatMessage = event.getChat();
        plugin.getScheduler().async().execute(() -> {
            manager.onChat(cnPlayer, chatMessage, channelName);
        });
    }

    @Override
    public boolean hasJoinedChannel(CNPlayer player, String channelID) {
        MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer((Player) player.player());
        return mcp.getCurrentChannel().getName().equals(channelID);
    }

    @Override
    public boolean canJoinChannel(CNPlayer player, String channelID) {
        ChatChannel channel = ChatChannel.getChannel(channelID);
        if (channel == null) {
            return false;
        }
        if (channel.hasPermission()) {
            return player.hasPermission(channel.getPermission());
        }
        return true;
    }

    @Override
    public boolean isIgnoring(CNPlayer sender, CNPlayer receiver) {
        MineverseChatPlayer mcp = MineverseChatAPI.getOnlineMineverseChatPlayer((Player) receiver.player());
        return mcp.getIgnores().contains(sender.uuid());
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
