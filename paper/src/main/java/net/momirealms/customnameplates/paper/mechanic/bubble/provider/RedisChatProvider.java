package net.momirealms.customnameplates.paper.mechanic.bubble.provider;

import dev.unnm3d.redischat.api.AsyncRedisChatMessageEvent;
import dev.unnm3d.redischat.channels.Channel;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.BubbleManager;
import net.momirealms.customnameplates.api.mechanic.bubble.provider.AbstractChatProvider;
import net.momirealms.customnameplates.api.util.LogUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import dev.unnm3d.redischat.RedisChat;

import java.util.Optional;


public class RedisChatProvider extends AbstractChatProvider {

    public RedisChatProvider(BubbleManager chatBubblesManager) {
        super(chatBubblesManager);
    }

    @Override
    public void register() {
        Bukkit.getPluginManager().registerEvents(this, CustomNameplatesPlugin.get());
    }

    @Override
    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public boolean hasJoinedChannel(Player player, String channelID) {
        return false;
    }

    @Override
    public boolean canJoinChannel(Player player, String channelID) {
        Optional<Channel> channelOptional = RedisChat.getInstance().getChannelManager().getChannel(channelID);
        if (channelOptional.isPresent()) {
            return player.hasPermission("redischat.channel." + channelOptional.get().getName());
        } else {
            LogUtils.warn("Channel " + channelID + " doesn't exist.");
            return false;
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onRedisChat(AsyncRedisChatMessageEvent event) {
        String channel = event.getChannel().getName();
        for (String black : chatBubblesManager.getBlacklistChannels()) {
            if (channel.equals(black)) return;
        }
        Player player = Bukkit.getPlayerExact(event.getSender().getName());
        if (player == null || !player.isOnline())
            return;
        CustomNameplatesPlugin.get().getScheduler().runTaskAsync(() -> {
            chatBubblesManager.onChat(player, event.getMessage(), channel);
        });
    }
}
