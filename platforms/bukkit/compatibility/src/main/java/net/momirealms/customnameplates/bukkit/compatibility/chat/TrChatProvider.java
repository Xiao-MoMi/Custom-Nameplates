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

import me.arasple.mc.trchat.TrChat;
import me.arasple.mc.trchat.api.event.TrChatEvent;
import me.arasple.mc.trchat.module.display.channel.Channel;
import me.arasple.mc.trchat.module.internal.filter.FilteredObject;
import me.arasple.mc.trchat.taboolib.platform.BukkitAdapter;
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

public class TrChatProvider extends AbstractChatMessageProvider implements Listener {

    private final BukkitAdapter adapter;

    public TrChatProvider(CustomNameplates plugin, ChatManager manager) {
        super(plugin, manager);
        this.adapter = new BukkitAdapter();
    }

    @EventHandler(ignoreCancelled = true)
    public void onTrChat(TrChatEvent event) {
        if (!event.getForward()) return;
        Channel channel = event.getChannel();
        String channelName = channel.getId();
        Player player = event.getSession().getPlayer();
        CNPlayer cnPlayer = plugin.getPlayer(player.getUniqueId());
        if (cnPlayer == null || !cnPlayer.isOnline()) {
            return;
        }
        FilteredObject object = TrChat.INSTANCE.api().getFilterManager().filter(event.getMessage(), adapter.adaptPlayer(event.getPlayer()), true);
        plugin.getScheduler().async().execute(() -> {
            manager.onChat(cnPlayer, object.getFiltered(), channelName);
        });
    }

    @Override
    public boolean hasJoinedChannel(CNPlayer player, String channelID) {
        if (TrChat.INSTANCE.api().getChannelManager().getChannel(channelID) instanceof Channel channel) {
            return channel.getListeners().contains(player.name());
        } else {
            return false;
        }
    }

    @Override
    public boolean canJoinChannel(CNPlayer player, String channelID) {
        if (TrChat.INSTANCE.api().getChannelManager().getChannel(channelID) instanceof Channel channel) {
            String perm = channel.getSettings().getJoinPermission();
            return player.hasPermission(perm);
        } else {
            return false;
        }
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