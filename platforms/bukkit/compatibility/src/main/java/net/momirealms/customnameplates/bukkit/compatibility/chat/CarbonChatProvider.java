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

import net.draycia.carbon.api.CarbonChat;
import net.draycia.carbon.api.channels.ChannelRegistry;
import net.draycia.carbon.api.channels.ChatChannel;
import net.draycia.carbon.api.event.CarbonEventSubscription;
import net.draycia.carbon.api.event.events.CarbonChatEvent;
import net.draycia.carbon.api.users.CarbonPlayer;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.chat.AbstractChatMessageProvider;
import net.momirealms.customnameplates.api.feature.chat.ChatManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class CarbonChatProvider extends AbstractChatMessageProvider {

    private final CarbonChat api;
    private CarbonEventSubscription<CarbonChatEvent> subscription;
    private final Method originalMessageMethod;
    private final Method channelKeyMethod;
    private final Method getChannelByKeyMethod;
    private final Method getKeyAsStringMethod;
    private final Method getKeyFromStringMethod;
    private final Object miniMessageInstance;
    private final Method serializeComponentMethod;

    public CarbonChatProvider(CustomNameplates plugin, ChatManager manager) {
        super(plugin, manager);
        api = net.draycia.carbon.api.CarbonChatProvider.carbonChat();
        try {
            this.originalMessageMethod = CarbonChatEvent.class.getMethod("originalMessage");
            this.channelKeyMethod = ChatChannel.class.getMethod("key");
            Class<?> keyClass = Class.forName("net{}kyori{}adventure{}key{}Key".replace("{}", "."));
            this.getKeyAsStringMethod = keyClass.getMethod("asString");
            this.getKeyFromStringMethod = keyClass.getMethod("key", String.class);
            this.getChannelByKeyMethod = ChannelRegistry.class.getMethod("channel", keyClass);
            Class<?> miniMessageClass = Class.forName("net{}kyori{}adventure{}text{}minimessage{}MiniMessage".replace("{}", "."));
            Method miniMessageInstanceGetMethod = miniMessageClass.getMethod("miniMessage");
            this.miniMessageInstance = miniMessageInstanceGetMethod.invoke(null);
            Class<?> componentClass = Class.forName("net{}kyori{}adventure{}text{}Component".replace("{}", "."));
            this.serializeComponentMethod = miniMessageClass.getMethod("serialize", componentClass);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register() {
        subscription = api.eventHandler().subscribe(CarbonChatEvent.class, event -> {
            if (event.cancelled())
                return;
            try {
                ChatChannel chatChannel = event.chatChannel();
                Object key = getChannelKey(chatChannel);
                if (key == null) return;
                String channel = (String) getKeyAsStringMethod.invoke(key);
                Player player = Bukkit.getPlayer(event.sender().uuid());
                if (player == null || !player.isOnline())
                    return;
                CNPlayer cnPlayer = plugin.getPlayer(player.getUniqueId());
                if (cnPlayer == null) return;
                Object component = getComponentFromEvent(event);
                String message = (String) serializeComponentMethod.invoke(miniMessageInstance, component);
                plugin.getScheduler().async().execute(() -> {
                    manager.onChat(cnPlayer, message, channel);
                });
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void unregister() {
        if (subscription != null) {
            subscription.dispose();
        }
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
            throw new RuntimeException(e);
        }
    }

    private Object getComponentFromEvent(CarbonChatEvent event) {
        try {
            return this.originalMessageMethod.invoke(event);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasJoinedChannel(CNPlayer player, String channelID) {
        CarbonPlayer cPlayer = null;
        for (CarbonPlayer carbonPlayer : api.server().players()) {
            if (carbonPlayer.uuid().equals(player.uuid())) {
                cPlayer = carbonPlayer;
                break;
            }
        }
        if (cPlayer == null) {
            return false;
        }
        try {
            ChatChannel selectedChannel = cPlayer.selectedChannel();
            ChatChannel currentChannel = selectedChannel != null ? selectedChannel : api.channelRegistry().defaultChannel();
            Object key = getChannelKey(currentChannel);
            String str = (String) getKeyAsStringMethod.invoke(key);
            return str.equals(channelID);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean canJoinChannel(CNPlayer player, String channelID) {
        ChannelRegistry registry = api.channelRegistry();
        Object key;
        try {
            key = getKeyFromStringMethod.invoke(null, channelID);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        if (key == null) {
            return false;
        }
        ChatChannel channel;
        try {
            channel = (ChatChannel) getChannelByKeyMethod.invoke(registry, key);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        if (channel == null) {
            return false;
        }
        CarbonPlayer carbonPlayer = this.api.userManager().user(player.uuid()).join();
        return channel.permissions().joinPermitted(carbonPlayer).permitted();
    }

    @Override
    public boolean isIgnoring(CNPlayer sender, CNPlayer receiver) {
        CarbonPlayer sPlayer = carbonPlayer(sender.uuid());
        CarbonPlayer rPlayer = carbonPlayer(receiver.uuid());
        if (sPlayer == null || rPlayer == null) {
            return false;
        }
        return rPlayer.ignoring(sPlayer.uuid());
    }
}