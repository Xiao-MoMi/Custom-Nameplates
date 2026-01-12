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

import it.pino.zelchat.api.ZelChatAPI;
import it.pino.zelchat.api.channel.ChatChannel;
import it.pino.zelchat.api.formatter.module.external.ExternalModule;
import it.pino.zelchat.api.formatter.module.priority.ModulePriority;
import it.pino.zelchat.api.message.ChatMessage;
import it.pino.zelchat.api.message.MessageState;
import it.pino.zelchat.api.player.ChatPlayer;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.chat.AbstractChatMessageProvider;
import net.momirealms.customnameplates.api.feature.chat.ChatManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class ZelChatProvider extends AbstractChatMessageProvider {
    private final Collection<ExternalModule> modules;
    private final JavaPlugin nameplates;
    private final Method originalMessageMethod;
    private final Object miniMessageInstance;
    private final Method serializeComponentMethod;

    public ZelChatProvider(CustomNameplates plugin, ChatManager manager) {
        super(plugin, manager);
        this.modules = new CopyOnWriteArrayList<>(List.of(new CNModule()));
        this.nameplates = (JavaPlugin) Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("CustomNameplates"));
        try {
            this.originalMessageMethod = ChatMessage.class.getMethod("getMessage");
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
        this.modules.forEach(module -> {
            ZelChatAPI.get().getFormatterService().registerExternalModule(this.nameplates, module);
            module.load();
        });
    }

    @Override
    public void unregister() {
        this.modules.forEach(module -> {
            module.unload();
            ZelChatAPI.get().getFormatterService().unregisterExternalModule(this.nameplates, module);
        });
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
        ChatPlayer player = ZelChatAPI.get().getPlayerService().getOnlinePlayers().get(receiver.uuid());
        return player.getIgnoredPlayers().contains(sender.uuid());
    }

    public class CNModule extends ExternalModule {

        @Override
        public ModulePriority getPriority() {
            return ModulePriority.NORMAL;
        }

        @Override
        public ChatMessage handleChatMessage(@NotNull ChatMessage chatMessage) {
            if(chatMessage.getState() == MessageState.CANCELLED || chatMessage.getState() == MessageState.FILTERED)
                return chatMessage;
            CNPlayer cnPlayer = plugin.getPlayer(chatMessage.getChatPlayer().getUniqueId());
            if (cnPlayer == null) {
                return chatMessage;
            }
            ChatChannel channel = chatMessage.getChannel();
            try {
                Object component = getComponentFromEvent(chatMessage);
                String message = (String) serializeComponentMethod.invoke(miniMessageInstance, component);
                plugin.getScheduler().async().execute(() -> manager.onChat(cnPlayer, message, channel.getType().name()));
                return null;
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        private Object getComponentFromEvent(ChatMessage message) {
            try {
                return originalMessageMethod.invoke(message);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}