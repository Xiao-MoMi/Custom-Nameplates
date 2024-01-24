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

import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import mineverse.Aust1n46.chat.api.events.VentureChatEvent;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.paper.mechanic.bubble.BubbleManagerImpl;
import org.bukkit.event.EventHandler;

public class VentureChatListener extends AbstractChatListener {

    public VentureChatListener(BubbleManagerImpl chatBubblesManager) {
        super(chatBubblesManager);
    }

    @EventHandler
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
            chatBubblesManager.onChat(chatPlayer.getPlayer(), event.getChat());
        });
    }
}
