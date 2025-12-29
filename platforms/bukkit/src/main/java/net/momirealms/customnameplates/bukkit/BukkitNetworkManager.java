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
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.helper.VersionHelper;
import net.momirealms.customnameplates.api.network.PacketEvent;
import net.momirealms.customnameplates.api.network.PacketSender;
import net.momirealms.customnameplates.api.network.PipelineInjector;
import net.momirealms.customnameplates.api.util.Vector3;
import net.momirealms.customnameplates.bukkit.util.ListMonitor;
import net.momirealms.customnameplates.bukkit.util.Reflections;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class BukkitNetworkManager implements PacketSender, PipelineInjector {

    private final BiConsumer<CNPlayer, List<Object>> packetsConsumer;
    private final BiConsumer<CNPlayer, Object> packetConsumer;
    private final BukkitCustomNameplates plugin;
    private static final String NAMEPLATES_CONNECTION_HANDLER_NAME = "nameplates_connection_handler";
    private static final String NAMEPLATES_SERVER_CHANNEL_HANDLER_NAME = "nameplates_server_channel_handler";
    private static final String NAMEPLATES_PACKET_HANDLER_NAME = "nameplates_packet_handler";
    private final ConcurrentHashMap<Object, CNPlayer> users = new ConcurrentHashMap<>();
    private final HashSet<Channel> injectedChannels = new HashSet<>();
    private boolean active;
    private boolean init;

    public BukkitNetworkManager(BukkitCustomNameplates plugin) {
        this.plugin = plugin;
        this.packetsConsumer = ((player, objects) -> {
            try {
                Object bundle = Reflections.constructor$ClientboundBundlePacket.newInstance(objects);
                sendPacket(player, bundle);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        });
        if (!VersionHelper.isFolia()) {
            packetConsumer = (player, packet) -> {
                try {
                    Reflections.method$SendPacket.invoke(
                            Reflections.field$PlayerConnection.get(
                                    Reflections.method$CraftPlayer$getHandle.invoke(player.player())), packet);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            };
        } else {
            packetConsumer = (player, packet) -> {
                Vector3 vector3 = player.position();
                Location location = new Location(Bukkit.getWorld(player.world()), vector3.x(), vector3.y(), vector3.z());
                plugin.getScheduler().executeSync(() -> {
                    try {
                        Reflections.method$SendPacket.invoke(
                                Reflections.field$PlayerConnection.get(
                                        Reflections.method$CraftPlayer$getHandle.invoke(player.player())), packet);
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException(e);
                    }
                }, location);
            };
        }
        this.active = true;
    }

    public void shutdown() {
        for (Channel channel : injectedChannels) {
            uninjectServerChannel(channel);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            handleDisconnection(getChannel(player));
        }
        injectedChannels.clear();
        active = false;
    }

    @Override
    public void setUser(Channel channel, CNPlayer user) {
        ChannelPipeline pipeline = channel.pipeline();
        users.put(pipeline, user);
    }

    @Override
    public CNPlayer getUser(Channel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        return users.get(pipeline);
    }

    @Override
    public CNPlayer removeUser(Channel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        return users.remove(pipeline);
    }

    @Override
    public CNPlayer getUser(Object player) {
        return getUser(getChannel(player));
    }

    public void init() throws ReflectiveOperationException {
        if (init) return;
        Object server = Reflections.method$MinecraftServer$getServer.invoke(null);
        Object serverConnection = Reflections.field$MinecraftServer$connection.get(server);
        @SuppressWarnings("unchecked")
        List<ChannelFuture> channels = (List<ChannelFuture>) Reflections.field$ServerConnectionListener$channels.get(serverConnection);
        ListMonitor<ChannelFuture> monitor = new ListMonitor<>(channels, (future) -> {
            if (!active) return;
            Channel channel = future.channel();
            injectServerChannel(channel);
            injectedChannels.add(channel);
        }, (object) -> {});
        Reflections.field$ServerConnectionListener$channels.set(serverConnection, monitor);
        init = true;
    }

    private void injectServerChannel(Channel serverChannel) {
        ChannelPipeline pipeline = serverChannel.pipeline();
        ChannelHandler connectionHandler = pipeline.get(NAMEPLATES_CONNECTION_HANDLER_NAME);
        if (connectionHandler != null) {
            pipeline.remove(NAMEPLATES_CONNECTION_HANDLER_NAME);
        }
        if (pipeline.get("SpigotNettyServerChannelHandler#0") != null) {
            pipeline.addAfter("SpigotNettyServerChannelHandler#0", NAMEPLATES_CONNECTION_HANDLER_NAME, new ServerChannelHandler());
        } else if (pipeline.get("floodgate-init") != null) {
            pipeline.addAfter("floodgate-init", NAMEPLATES_CONNECTION_HANDLER_NAME, new ServerChannelHandler());
        } else if (pipeline.get("MinecraftPipeline#0") != null) {
            pipeline.addAfter("MinecraftPipeline#0", NAMEPLATES_CONNECTION_HANDLER_NAME, new ServerChannelHandler());
        } else {
            pipeline.addFirst(NAMEPLATES_CONNECTION_HANDLER_NAME, new ServerChannelHandler());
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            Channel channel = getChannel(player);
            CNPlayer user = getUser(player);
            if (user == null) {
                BukkitCNPlayer cnPlayer = new BukkitCNPlayer(plugin, channel);
                cnPlayer.setPlayer(player);
                injectChannel(channel, ConnectionState.PLAY);
                plugin.handleJoin(player);
            }
        }
    }

    private void uninjectServerChannel(Channel channel) {
        if (channel.pipeline().get(NAMEPLATES_CONNECTION_HANDLER_NAME) != null) {
            channel.pipeline().remove(NAMEPLATES_CONNECTION_HANDLER_NAME);
        }
    }

    @Override
    public void sendPacket(@NotNull CNPlayer player, Object packet) {
        packetConsumer.accept(player, packet);
    }

    @Override
    public void sendPacket(@NotNull CNPlayer player, final List<Object> packet) {
        if (!player.isOnline()) return;
        if (packet.isEmpty()) return;
        packetsConsumer.accept(player, packet);
    }

    @Override
    public Channel getChannel(Object player) {
        try {
            return (Channel) Reflections.field$Channel.get(
                    Reflections.field$NetworkManager.get(
                            Reflections.field$PlayerConnection.get(
                                    Reflections.method$CraftPlayer$getHandle.invoke(player)
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

    public enum ConnectionState {
        HANDSHAKING, STATUS, LOGIN, PLAY, CONFIGURATION;
    }

    public void handleDisconnection(Channel channel) {
        if (channel.pipeline().get(NAMEPLATES_PACKET_HANDLER_NAME) != null) {
            channel.pipeline().remove(NAMEPLATES_PACKET_HANDLER_NAME);
        }
        removeUser(channel);
    }

    public void injectChannel(Channel channel, ConnectionState connectionState) {
        if (isFakeChannel(channel)) {
            return;
        }

        CNPlayer user = new BukkitCNPlayer(plugin, channel);
        if (channel.pipeline().get("splitter") == null) {
            channel.close();
            return;
        }

        ChannelPipeline pipeline = channel.pipeline();
        if (pipeline.get(NAMEPLATES_PACKET_HANDLER_NAME) != null) {
            pipeline.remove(NAMEPLATES_PACKET_HANDLER_NAME);
        }
        for (Map.Entry<String, ChannelHandler> entry : pipeline.toMap().entrySet()) {
            if (Reflections.clazz$NetworkManager.isAssignableFrom(entry.getValue().getClass())) {
                pipeline.addBefore(entry.getKey(), NAMEPLATES_PACKET_HANDLER_NAME, createHandler(user));
                break;
            }
        }

        channel.closeFuture().addListener((ChannelFutureListener) future -> handleDisconnection(user.channel()));
        setUser(channel, user);
    }

    public class ServerChannelHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(@NotNull ChannelHandlerContext context, @NotNull Object c) throws Exception {
            Channel channel = (Channel) c;
            channel.pipeline().addLast(NAMEPLATES_SERVER_CHANNEL_HANDLER_NAME, new PreChannelInitializer());
            super.channelRead(context, c);
        }
    }

    public class PreChannelInitializer extends ChannelInboundHandlerAdapter {

        private static final InternalLogger logger = InternalLoggerFactory.getInstance(io.netty.channel.ChannelInitializer.class);

        @Override
        public void channelRegistered(ChannelHandlerContext context) {
            try {
                injectChannel(context.channel(), ConnectionState.HANDSHAKING);
            } catch (Throwable t) {
                exceptionCaught(context, t);
            } finally {
                ChannelPipeline pipeline = context.pipeline();
                if (pipeline.context(this) != null) {
                    pipeline.remove(this);
                }
            }
            context.pipeline().fireChannelRegistered();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext context, Throwable t) {
            PreChannelInitializer.logger.warn("Failed to inject channel: " + context.channel(), t);
            context.close();
        }
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
            } catch (Throwable e) {
                plugin.getPluginLogger().severe("An error occurred when reading packets", e);
                super.write(context, packet, channelPromise);
            }
        }
    }

    public static boolean isFakeChannel(Object channel) {
        return channel.getClass().getSimpleName().equals("FakeChannel")
                || channel.getClass().getSimpleName().equals("SpoofedChannel");
    }
}
