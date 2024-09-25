package net.momirealms.customnameplates.bukkit;

import io.netty.channel.*;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.network.PacketEvent;
import net.momirealms.customnameplates.api.network.PacketSender;
import net.momirealms.customnameplates.api.network.PipelineInjector;
import net.momirealms.customnameplates.bukkit.util.Reflections;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;

public class BukkitNetworkManager implements PacketSender, PipelineInjector {

    private final BiConsumer<CNPlayer, List<Object>> packetsConsumer;
    private final CustomNameplates plugin;

    public BukkitNetworkManager(CustomNameplates plugin) {
        this.plugin = plugin;
        this.packetsConsumer = ((player, objects) -> {
            try {
                Object bundle = Reflections.constructor$ClientboundBundlePacket.newInstance(objects);
                sendPacket(player, bundle);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void sendPacket(@NotNull CNPlayer player, final Object packet) {
        try {
            Reflections.method$SendPacket.invoke(
                    Reflections.field$PlayerConnection.get(
                            Reflections.method$CraftPlayer$getHandle.invoke(player.player())), packet);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendPacket(@NotNull CNPlayer player, final List<Object> packet) {
        packetsConsumer.accept(player, packet);
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
        public void write(ChannelHandlerContext context, Object packet, ChannelPromise channelPromise) {
            try {
                PacketEvent event = new PacketEvent(packet);
                plugin.getPlatform().onPacketSend(player, event);
                if (event.isCancelled()) return;
                super.write(context, packet, channelPromise);
                channelPromise.addListener((p) -> {
                    for (Runnable task : event.getDelayedTasks()) {
                        task.run();
                    }
                });
            } catch (Throwable e) {
                plugin.getPluginLogger().severe("An error occurred when reading packets", e);
            }
        }
    }
}
