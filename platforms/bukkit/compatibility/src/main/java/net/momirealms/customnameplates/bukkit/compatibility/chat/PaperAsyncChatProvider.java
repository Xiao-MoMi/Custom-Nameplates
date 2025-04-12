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

import io.papermc.paper.event.player.AbstractChatEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.chat.AbstractChatMessageProvider;
import net.momirealms.customnameplates.api.feature.chat.ChatManager;
import net.momirealms.customnameplates.common.util.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class PaperAsyncChatProvider extends AbstractChatMessageProvider implements Listener {
    private Field field$AbstractChatEvent$message;
    private Method method$ComponentSerializer$serialize;
    private Object miniMessage;

    public PaperAsyncChatProvider(CustomNameplates plugin, ChatManager manager) {
        super(plugin, manager);
        try {
            this.field$AbstractChatEvent$message = AbstractChatEvent.class.getDeclaredField("message");
            this.field$AbstractChatEvent$message.setAccessible(true);
            Class<?> clazz$ComponentSerializer = requireNonNull(ReflectionUtils.getClazz("net{}kyori{}adventure{}text{}serializer{}ComponentSerializer".replace("{}", ".")));
            Class<?> clazz$AdventureComponent = requireNonNull(ReflectionUtils.getClazz("net{}kyori{}adventure{}text{}Component".replace("{}", ".")));
            this.method$ComponentSerializer$serialize = requireNonNull(ReflectionUtils.getMethod(clazz$ComponentSerializer, Object.class, new String[] {"serialize"}, clazz$AdventureComponent));
            Class<?> clazz$MiniMessage = requireNonNull(ReflectionUtils.getClazz("net{}kyori{}adventure{}text{}minimessage{}MiniMessage".replace("{}", ".")));
            Method method$MiniMessage$builder = clazz$MiniMessage.getMethod("builder");
            Class<?> clazz$MiniMessage$Builder = requireNonNull(ReflectionUtils.getClazz("net{}kyori{}adventure{}text{}minimessage{}MiniMessage$Builder".replace("{}", ".")));
            Method method$MiniMessage$Builder$strict = clazz$MiniMessage$Builder.getMethod("strict", boolean.class);
            Method method$MiniMessage$Builder$build = clazz$MiniMessage$Builder.getMethod("build");
            Object builder = method$MiniMessage$builder.invoke(null);
            builder = method$MiniMessage$Builder$strict.invoke(builder, true);
            this.miniMessage = method$MiniMessage$Builder$build.invoke(builder);
        } catch (ReflectiveOperationException e) {
            plugin.getPluginLogger().warn("Failed to init PaperAsyncChatProvider", e);
        }
    }

    // This event is not async sometimes
    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if (!ConfigManager.chatUnsafe() && event.isCancelled()) return;
        CNPlayer player = plugin.getPlayer(event.getPlayer().getUniqueId());
        if (player == null) return;
        try {
            Object component = field$AbstractChatEvent$message.get(event);
            String miniMessage = (String) method$ComponentSerializer$serialize.invoke(this.miniMessage, component);
            plugin.getScheduler().async().execute(() -> {
                manager.onChat(player, miniMessage, "global");
            });
        } catch (Exception e) {
            plugin.getPluginLogger().warn("Failed to handle AsyncChatEvent", e);
        }
    }

    @Override
    public boolean hasJoinedChannel(CNPlayer player, String channelID) {
        return true;
    }

    @Override
    public boolean canJoinChannel(CNPlayer player, String channelID) {
        return true;
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