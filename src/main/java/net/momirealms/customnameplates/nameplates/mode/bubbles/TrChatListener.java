package net.momirealms.customnameplates.nameplates.mode.bubbles;

import me.arasple.mc.trchat.api.event.TrChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public record TrChatListener(
        ChatBubblesManager chatBubblesManager) implements Listener {

    @EventHandler
    public void onTrChat(TrChatEvent event) {
        if (event.isCancelled()) return;
        if (!event.getForward()) return;
        chatBubblesManager.onChat(event.getSession().getPlayer(), event.getMessage());
    }
}