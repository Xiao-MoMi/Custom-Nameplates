package net.momirealms.customnameplates.paper.mechanic.bubble.provider;

import net.advancedplugins.chat.api.AdvancedChatAPI;
import net.advancedplugins.chat.channel.ChatChannel;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.BubbleManager;
import net.momirealms.customnameplates.api.mechanic.bubble.provider.AbstractChatProvider;
import net.momirealms.customnameplates.api.util.LogUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AdvancedChatProvider extends AbstractChatProvider {
    private static final String joinPermission = "advancedchat.channel.join.";
    private final AdvancedChatAPI api;

    public AdvancedChatProvider(BubbleManager chatBubblesManager) {
        super(chatBubblesManager);
        this.api = AdvancedChatAPI.getApi();
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
        ChatChannel channel = api.getChatChannel(channelID);
        if (channel == null) {
            LogUtils.warn("Channel " + channelID + " doesn't exist");
            return false;
        }
        return player.hasPermission(joinPermission + channelID);
    }

    @SuppressWarnings("deprecation")
    @EventHandler (ignoreCancelled = true)
    public void onAdvancedChat(AsyncPlayerChatEvent event) {
        // Proper API pending
        Player player = event.getPlayer();
        if (!player.isOnline()) return;

        ChatChannel channel = api.getPlayerChatChannel(player.getUniqueId());
        if (channel == null) return;
        for (String blacklisted : chatBubblesManager.getBlacklistChannels()) {
            if (channel.getSectionName().equals(blacklisted)) return;
        }

        CustomNameplatesPlugin.get().getScheduler().runTaskAsync(() ->
                chatBubblesManager.onChat(player, event.getMessage(), channel.getSectionName())
        );

    }
}
