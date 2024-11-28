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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import ru.brikster.chatty.api.ChattyApi;
import ru.brikster.chatty.api.chat.Chat;
import ru.brikster.chatty.api.event.ChattyMessageEvent;

import java.util.Objects;

public class ChattyProvider extends AbstractChatMessageProvider implements Listener {

    public ChattyProvider(CustomNameplates plugin, ChatManager manager) {
        super(plugin, manager);
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(ChattyMessageEvent event) {
        final String message = event.getPlainMessage();
        final Player player = event.getSender();
        if (!player.isOnline()) return;
        CNPlayer cnPlayer = plugin.getPlayer(player.getUniqueId());
        if (cnPlayer == null) return;
        plugin.getScheduler().async().execute(() -> {
            manager.onChat(cnPlayer, message, event.getChat().getId());
        });
    }

    @Override
    public boolean hasJoinedChannel(CNPlayer player, String channelID) {
        return true;
    }

    @Override
    public boolean canJoinChannel(CNPlayer player, String channelID) {
        Chat chat = ChattyApi.instance().getChats().get(channelID);
        if (chat == null) {
            return false;
        }
        if (!chat.isPermissionRequired()) return true;
        return chat.hasReadPermission((Player) player.player());
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