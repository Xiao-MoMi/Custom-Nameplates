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

import me.arasple.mc.trchat.api.event.TrChatEvent;
import me.arasple.mc.trchat.module.display.channel.Channel;
import net.momirealms.customnameplates.paper.mechanic.bubble.BubbleManagerImpl;
import org.bukkit.event.EventHandler;

public class TrChatListener extends AbstractChatListener {

    public TrChatListener(BubbleManagerImpl chatBubblesManager) {
        super(chatBubblesManager);
    }

    @EventHandler (ignoreCancelled = true)
    public void onTrChat(TrChatEvent event) {
        if (!event.getForward()) return;
        Channel channelName = event.getChannel();
        for (String channel : chatBubblesManager.getBlacklistChannels()) {
            if (channelName.equals(channel)) return;
        }
        chatBubblesManager.onChat(event.getSession().getPlayer(), event.getMessage());
    }
}