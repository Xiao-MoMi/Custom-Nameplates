package net.momirealms.customnameplates.api.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import net.momirealms.customnameplates.api.CNPlayer;

public interface PipelineInjector {

    Channel getChannel(CNPlayer player);

    ChannelDuplexHandler createHandler(CNPlayer player);

    void inject(CNPlayer player);

    void uninject(CNPlayer player);
}
