package net.momirealms.customnameplates.paper.mechanic.bubble.provider;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.advancedplugins.chat.api.AdvancedChatAPI;
import net.advancedplugins.chat.channel.ChatChannel;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.BubbleManager;
import net.momirealms.customnameplates.api.mechanic.bubble.provider.AbstractChatProvider;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.paper.util.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;

public class AdvancedChatProvider extends AbstractChatProvider {
    private static final AdvancedChatAPI api = AdvancedChatAPI.getApi();
    private static final String joinPermission = "advancedchat.channel.join.";

    public AdvancedChatProvider(BubbleManager chatBubblesManager) {
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
        ChatChannel channel = api.getPlayerChatChannel(player.getUniqueId());
        if (channel == null) {
            LogUtils.warn("Channel " + channelID + " doesn't exist");
            return false;
        }
        return channel.getSectionName().equals(channelID);
    }

    @Override
    public boolean canJoinChannel(Player player, String channelID) {
        ChatChannel channel = api.getChatChannels()
                .stream()
                .filter(i -> i.getSectionName().equals(channelID))
                .findFirst()
                .orElse(null);
        if (channel == null) {
            LogUtils.warn("Channel " + channelID + " doesn't exist");
            return false;
        }
        return player.hasPermission(joinPermission + channelID);
    }

    @EventHandler (ignoreCancelled = true)
    public void onAdvancedChat(AsyncChatEvent event) {
        // Pending
        Player player = event.getPlayer();
        if (!player.isOnline()) return;

        ChatChannel channel = api.getPlayerChatChannel(player.getUniqueId());
        if (channel == null) return;
        for (String blacklisted : chatBubblesManager.getBlacklistChannels()) {
            if (channel.getSectionName().equals(blacklisted)) return;
        }

        String message = ReflectionUtils.getMiniMessageTextFromNonShadedComponent(event.message());
        CustomNameplatesPlugin.get().getScheduler().runTaskAsync(() ->
                chatBubblesManager.onChat(player, message, channel.getSectionName())
        );
    }
}
