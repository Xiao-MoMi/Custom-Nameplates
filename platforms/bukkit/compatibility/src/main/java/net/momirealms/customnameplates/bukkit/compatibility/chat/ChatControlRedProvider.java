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

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.chat.AbstractChatMessageProvider;
import net.momirealms.customnameplates.api.feature.chat.ChatManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.mineacademy.chatcontrol.api.ChannelPostChatEvent;
import org.mineacademy.chatcontrol.api.ChatControlAPI;
import org.mineacademy.chatcontrol.model.Channel;
import org.mineacademy.chatcontrol.model.db.PlayerCache;

import java.util.Objects;

public class ChatControlRedProvider extends AbstractChatMessageProvider implements Listener {

    public ChatControlRedProvider(CustomNameplates plugin, ChatManager manager) {
        super(plugin, manager);
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(ChannelPostChatEvent event) {
        plugin.debug(() -> "ChatChannelEvent triggered");
        final CommandSender sender = event.getSender();
        if (!(sender instanceof Player player)) {
            return;
        }
        if (ChatControlAPI.isChatMuted()) {
            return;
        }
        if (!player.isOnline()) return;
        CNPlayer cnPlayer = plugin.getPlayer(player.getUniqueId());
        if (cnPlayer == null) return;
        plugin.getScheduler().async().execute(() -> {
            plugin.debug(() -> "Channel: " + event.getChannel().getName());
            manager.onChat(cnPlayer, event.getMessage(), event.getChannel().getName());
        });
    }

    @Override
    public boolean hasJoinedChannel(CNPlayer player, String channelID) {
        Channel channel = Channel.findChannel(channelID);
        if (channel == null) return false;
        return channel.isInChannel((Player) player.player());
    }

    @Override
    public boolean canJoinChannel(CNPlayer player, String channelID) {
        return player.hasPermission("chatcontrol.channel.join."+channelID+".read");
    }

    @Override
    public boolean isIgnoring(CNPlayer sender, CNPlayer receiver) {
        PlayerCache cache = PlayerCache.fromCached((Player) receiver.player());
        if (cache == null) return false;
        return cache.isIgnoringPlayer(sender.uuid());
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