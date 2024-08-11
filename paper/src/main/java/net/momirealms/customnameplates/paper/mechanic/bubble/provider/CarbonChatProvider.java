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

package net.momirealms.customnameplates.paper.mechanic.bubble.provider;

import net.draycia.carbon.api.CarbonChat;
import net.draycia.carbon.api.channels.ChannelRegistry;
import net.draycia.carbon.api.channels.ChatChannel;
import net.draycia.carbon.api.event.CarbonEventSubscription;
import net.draycia.carbon.api.event.events.CarbonChatEvent;
import net.draycia.carbon.api.users.CarbonPlayer;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.BubbleManager;
import net.momirealms.customnameplates.api.mechanic.bubble.provider.AbstractChatProvider;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.paper.util.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class CarbonChatProvider extends AbstractChatProvider {

    private final CarbonChat api;
    private CarbonEventSubscription<CarbonChatEvent> subscription;
    private Method originalMessageMethod;
    private Method channelKeyMethod;
    private Method getChannelByKeyMethod;

    public CarbonChatProvider(BubbleManager chatBubblesManager) {
        super(chatBubblesManager);
        this.api = net.draycia.carbon.api.CarbonChatProvider.carbonChat();
        try {
            this.originalMessageMethod = CarbonChatEvent.class.getMethod("originalMessage");
            this.channelKeyMethod = ChatChannel.class.getMethod("key");
            this.getChannelByKeyMethod = ChannelRegistry.class.getMethod("channel", ReflectionUtils.getKeyClass());
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
            CustomNameplatesPlugin.get().getScheduler().runTaskAsync(() -> chatBubblesManager.onChat(player, message, channel));
        });
    }

    @Override
    public void unregister() {
        if (subscription != null) {
            subscription.dispose();
        }
    }

    @Override
    public boolean hasJoinedChannel(Player player, String channelID) {
        CarbonPlayer cPlayer = null;
        for (CarbonPlayer carbonPlayer : api.server().players()) {
            if (carbonPlayer.uuid().equals(player.getUniqueId())) {
                cPlayer = carbonPlayer;
                break;
            }
        }
        if (cPlayer == null) {
            return false;
        }
        ChatChannel selectedChannel = cPlayer.selectedChannel();
        ChatChannel currentChannel = selectedChannel != null ? selectedChannel : api.channelRegistry().defaultChannel();
        Object key = getChannelKey(currentChannel);
        String str = ReflectionUtils.getKeyAsString(key);
        return str.equals(channelID);
    }

    @Override
    public boolean canJoinChannel(Player player, String channelID) {
        ChannelRegistry registry = api.channelRegistry();
        Object key = ReflectionUtils.getKerFromString(channelID);
        if (key == null) {
            return false;
        }
        ChatChannel channel = null;
        try {
            channel = (ChatChannel) getChannelByKeyMethod.invoke(registry, key);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        if (channel == null) {
            LogUtils.warn("Channel " + channelID + " doesn't exist.");
            return false;
        }
        String perm = channel.permission();
        if (perm == null) {
            return true;
        }
        return player.hasPermission(perm);
    }

    @Override
    public boolean isIgnoring(Player sender, Player receiver) {
        CarbonPlayer sPlayer = carbonPlayer(sender.getUniqueId());
        CarbonPlayer rPlayer = carbonPlayer(receiver.getUniqueId());
        if (sPlayer == null || rPlayer == null) {
            return false;
        }
        return rPlayer.ignoring(sPlayer.uuid());
    }

    @Nullable
    private CarbonPlayer carbonPlayer(UUID uuid) {
        CarbonPlayer carbonPlayer = null;
        for (CarbonPlayer cPlayer : api.server().players()) {
            if (cPlayer.uuid().equals(uuid)) {
                carbonPlayer = cPlayer;
                break;
            }
        }
        return carbonPlayer;
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