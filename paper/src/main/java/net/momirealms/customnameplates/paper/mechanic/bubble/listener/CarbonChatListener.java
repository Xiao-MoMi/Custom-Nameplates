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

import net.draycia.carbon.api.CarbonChat;
import net.draycia.carbon.api.CarbonChatProvider;
import net.draycia.carbon.api.channels.ChatChannel;
import net.draycia.carbon.api.event.CarbonEventSubscription;
import net.draycia.carbon.api.event.events.CarbonChatEvent;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.BubbleManager;
import net.momirealms.customnameplates.api.mechanic.bubble.listener.AbstractChatListener;
import net.momirealms.customnameplates.paper.util.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CarbonChatListener extends AbstractChatListener {

    private final CarbonChat api;
    private CarbonEventSubscription<CarbonChatEvent> subscription;
    private Method originalMessageMethod;
    private Method channelKeyMethod;

    public CarbonChatListener(BubbleManager chatBubblesManager) {
        super(chatBubblesManager);
        this.api = CarbonChatProvider.carbonChat();
        try {
            this.originalMessageMethod = CarbonChatEvent.class.getMethod("originalMessage");
            this.channelKeyMethod = ChatChannel.class.getMethod("key");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register() {
        subscription = api.eventHandler().subscribe(CarbonChatEvent.class, event -> {
            if (event.cancelled())
                return;
            ChatChannel chatChannel = event.chatChannel();
            Object key = getChannelKey(chatChannel);
            if (key == null) return;
            String channel = ReflectionUtils.getKeyAsString(key);
            for (String black : chatBubblesManager.getBlacklistChannels()) {
                if (channel.equals(black)) return;
            }
            Player player = Bukkit.getPlayer(event.sender().uuid());
            if (player == null || !player.isOnline())
                return;
            Object component = getComponentFromEvent(event);
            String message = ReflectionUtils.getMiniMessageTextFromNonShadedComponent(component);
            CustomNameplatesPlugin.get().getScheduler().runTaskAsync(() -> chatBubblesManager.onChat(player, message));
        });
    }

    @Override
    public void unregister() {
        if (subscription != null) {
            subscription.dispose();
        }
    }

    private Object getChannelKey(ChatChannel channel) {
        try {
            return this.channelKeyMethod.invoke(channel);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object getComponentFromEvent(CarbonChatEvent event) {
        try {
            return this.originalMessageMethod.invoke(event);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return ReflectionUtils.getEmptyComponent();
    }
}