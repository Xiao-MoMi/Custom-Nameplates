package net.momirealms.customnameplates.listener;

import mineverse.Aust1n46.chat.api.events.VentureChatEvent;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.manager.ChatBubblesManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

public class VentureChatListener extends AbstractChatListener {

    public VentureChatListener(ChatBubblesManager chatBubblesManager) {
        super(chatBubblesManager);
    }

    @EventHandler
    public void onVentureChat(VentureChatEvent event) {
        String channelName = event.getChannel().getName();
        for (String channel : chatBubblesManager.getChannels()) {
            if (channelName.equals(channel)) return;
        }
        Bukkit.getScheduler().runTask(CustomNameplates.getInstance(), () -> chatBubblesManager.onChat(event.getMineverseChatPlayer().getPlayer(), event.getChat().substring(1)));
    }
}
