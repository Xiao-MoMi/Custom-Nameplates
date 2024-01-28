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

import me.arasple.mc.trchat.TrChat;
import me.arasple.mc.trchat.api.event.TrChatEvent;
import me.arasple.mc.trchat.module.display.channel.Channel;
import me.arasple.mc.trchat.module.internal.filter.FilteredObject;
import me.arasple.mc.trchat.taboolib.platform.BukkitAdapter;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.paper.mechanic.bubble.BubbleManagerImpl;
import org.bukkit.event.EventHandler;

import java.util.Arrays;

public class TrChatListener extends AbstractChatListener {

    private BukkitAdapter adapter;

    public TrChatListener(BubbleManagerImpl chatBubblesManager) {
        super(chatBubblesManager);
        this.adapter = new BukkitAdapter();
    }

    @EventHandler (ignoreCancelled = true)
    public void onTrChat(TrChatEvent event) {
        if (!event.getForward()) return;
        Channel channel = event.getChannel();
        String channelName = channel.getId();
        for (String black : chatBubblesManager.getBlacklistChannels()) {
            if (channelName.equals(black)) return;
        }
        FilteredObject object = TrChat.INSTANCE.api().getFilterManager().filter(event.getMessage(), adapter.adaptPlayer(event.getPlayer()), true);
        CustomNameplatesPlugin.get().getScheduler().runTaskAsync(() -> {
            chatBubblesManager.onChat(event.getSession().getPlayer(), object.getFiltered());
        });
    }
}