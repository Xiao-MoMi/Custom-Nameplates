package net.momirealms.customnameplates.nameplates.mode.bubbles;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public record ChatListener(
        ChatBubblesManager chatBubblesManager) implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!event.isCancelled())
            chatBubblesManager.onChat(event.getPlayer(), MiniMessage.miniMessage().stripTags(ChatColor.stripColor(event.getMessage())));
    }
}
