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
import net.william278.huskchat.BukkitHuskChat;
import net.william278.huskchat.api.BukkitHuskChatAPI;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.event.ChatMessageEvent;
import net.william278.huskchat.user.OnlineUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.Optional;

public class HuskChatProvider extends AbstractChatMessageProvider implements Listener {

    private final BukkitHuskChat huskChat;

    public HuskChatProvider(CustomNameplates plugin, ChatManager manager) {
        super(plugin, manager);
        this.huskChat = (BukkitHuskChat) Bukkit.getPluginManager().getPlugin("HuskChat");
    }

    @EventHandler(ignoreCancelled = true)
    public void onHuskChat(ChatMessageEvent event) {
        String channel = event.getChannelId();
        Player player = Bukkit.getPlayer(event.getSender().getUuid());
        if (player == null || !player.isOnline())
            return;
        CNPlayer cnPlayer = plugin.getPlayer(player.getUniqueId());
        if (cnPlayer == null) {
            return;
        }
        plugin.getScheduler().async().execute(() -> {
            manager.onChat(cnPlayer, event.getMessage(), channel);
        });
    }

    @Override
    public boolean hasJoinedChannel(CNPlayer player, String channelID) {
        OnlineUser onlineUser = BukkitHuskChatAPI.getInstance().adaptPlayer((Player) player.player());
        Optional<String> channel = BukkitHuskChatAPI.getInstance().getPlayerChannel(onlineUser);
        return channel.map(s -> s.equals(channelID)).orElse(false);
    }

    @Override
    public boolean canJoinChannel(CNPlayer player, String channelID) {
        Optional<Channel> channel = huskChat.getChannels().getChannel(channelID);
        if (channel.isEmpty()) {
            return false;
        }
        Optional<String> optionalReceivePerm = channel.get().getPermissions().getReceive();
        return optionalReceivePerm.map(player::hasPermission).orElse(true);
    }

    @Override
    public boolean isIgnoring(CNPlayer sender, CNPlayer receiver) {
        return false;
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