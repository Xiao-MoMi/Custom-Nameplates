package net.momirealms.customnameplates.listener;

import mineverse.Aust1n46.chat.api.events.VentureChatEvent;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.manager.ChatBubblesManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class VentureChatListener implements Listener {

    private final ChatBubblesManager chatBubblesManager;

    public VentureChatListener(ChatBubblesManager chatBubblesManager) {
        this.chatBubblesManager = chatBubblesManager;
    }

    @EventHandler
    public void onVentureChat(VentureChatEvent event) {
        String channelName = event.getChannel().getName();
        for (String channel : ChatBubblesManager.channels) {
            if (channelName.equals(channel)) return;
        }
        Bukkit.getScheduler().runTask(CustomNameplates.plugin, () -> {
            chatBubblesManager.onChat(event.getMineverseChatPlayer().getPlayer(), ChatColor.stripColor(event.getChat()).substring(1));
        });
    }
}
