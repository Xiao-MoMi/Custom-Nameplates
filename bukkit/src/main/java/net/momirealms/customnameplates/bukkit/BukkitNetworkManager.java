/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customnameplates.bukkit;

import io.netty.channel.*;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.network.PacketEvent;
import net.momirealms.customnameplates.api.network.PacketSender;
import net.momirealms.customnameplates.api.network.PipelineInjector;
import net.momirealms.customnameplates.bukkit.util.Reflections;
import net.momirealms.customnameplates.common.util.TriConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class BukkitNetworkManager implements PacketSender, PipelineInjector {

    private final TriConsumer<CNPlayer, List<Object>, Boolean> packetsConsumer;
    private final CustomNameplates plugin;

    public BukkitNetworkManager(CustomNameplates plugin) {
        this.plugin = plugin;
        this.packetsConsumer = ((player, objects, immediate) -> {
            try {
                Object bundle = Reflections.constructor$ClientboundBundlePacket.newInstance(objects);
                sendPacket(player, bundle, immediate);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void sendPacket(@NotNull CNPlayer player, Object packet) {
        sendPacket(player, packet, false);
    }

    @Override
    public void sendPacket(@NotNull CNPlayer player, final Object packet, final boolean immediately) {
        if (!player.isOnline()) return;
        if (immediately) {
            try {
                Reflections.method$SendPacketImmediately.invoke(
                        Reflections.field$NetworkManager.get(Reflections.field$PlayerConnection.get(
                                Reflections.method$CraftPlayer$getHandle.invoke(player.player()))), packet, null, true);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                Reflections.method$SendPacket.invoke(
                        Reflections.field$PlayerConnection.get(
                                Reflections.method$CraftPlayer$getHandle.invoke(player.player())), packet);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void sendPacket(@NotNull CNPlayer player, final List<Object> packet) {
        if (!player.isOnline()) return;
        sendPacket(player, packet, false);
    }

    @Override
    public void sendPacket(@NotNull CNPlayer player, final List<Object> packet, boolean immediately) {
        if (!player.isOnline()) return;
        packetsConsumer.accept(player, packet, immediately);
    }

    @Override
    public Channel getChannel(CNPlayer player) {
        try {
            return (Channel) Reflections.field$Channel.get(
                    Reflections.field$NetworkManager.get(
                            Reflections.field$PlayerConnection.get(
                                    Reflections.method$CraftPlayer$getHandle.invoke(player.player())
                            )
                    )
            );
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ChannelDuplexHandler createHandler(CNPlayer player) {
        return new CNChannelHandler(player);
    }

    @Override
    public void inject(CNPlayer player) {
        Channel channel = getChannel(player);
        try {
            ChannelPipeline pipeline = channel.pipeline();
            for (Map.Entry<String, ChannelHandler> entry : pipeline.toMap().entrySet()) {
                if (Reflections.clazz$NetworkManager.isAssignableFrom(entry.getValue().getClass())) {
                    pipeline.addBefore(entry.getKey(), "nameplates_packet_handler", createHandler(player));
                    break;
                }
            }
        } catch (NoSuchElementException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void uninject(CNPlayer player) {
        Channel channel = getChannel(player);
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove("nameplates_packet_handler");
            return null;
        });
    }

    public class CNChannelHandler extends ChannelDuplexHandler {

        private final CNPlayer player;

        public CNChannelHandler(CNPlayer player) {
            this.player = player;
        }

        @Override
        public void write(ChannelHandlerContext context, Object packet, ChannelPromise channelPromise) throws Exception {
            try {
                PacketEvent event = new PacketEvent(packet);
                plugin.getPlatform().onPacketSend(player, event);
                if (event.cancelled()) return;
                super.write(context, packet, channelPromise);
                channelPromise.addListener((p) -> {
                    for (Runnable task : event.getDelayedTasks()) {
                        task.run();
                    }
                });
            } catch (Throwable e) {
                plugin.getPluginLogger().severe("An error occurred when reading packets", e);
                super.write(context, packet, channelPromise);
            }
        }
    }
}
