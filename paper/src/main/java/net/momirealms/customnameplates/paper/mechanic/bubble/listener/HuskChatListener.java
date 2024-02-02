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

package net.momirealms.customnameplates.paper.mechanic.bubble.listener;

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.BubbleManager;
import net.momirealms.customnameplates.api.mechanic.bubble.listener.AbstractChatListener;
import net.william278.huskchat.bukkit.event.ChatMessageEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;

public class HuskChatListener extends AbstractChatListener {

    public HuskChatListener(BubbleManager chatBubblesManager) {
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

    @EventHandler (ignoreCancelled = true)
    public void onHuskChat(ChatMessageEvent event) {
        String channel = event.getChannelId();
        for (String black : chatBubblesManager.getBlacklistChannels()) {
            if (channel.equals(black)) return;
        }
        Player player = Bukkit.getPlayer(event.getSender().getUuid());
        if (player == null || !player.isOnline())
            return;
        CustomNameplatesPlugin.get().getScheduler().runTaskAsync(() -> {
            chatBubblesManager.onChat(player, event.getMessage());
        });
    }
}