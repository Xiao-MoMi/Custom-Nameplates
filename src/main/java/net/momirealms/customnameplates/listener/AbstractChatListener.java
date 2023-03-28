package net.momirealms.customnameplates.listener;

import net.momirealms.customnameplates.manager.ChatBubblesManager;
import org.bukkit.event.Listener;

public abstract class AbstractChatListener implements Listener {

    protected ChatBubblesManager chatBubblesManager;

    public AbstractChatListener(ChatBubblesManager chatBubblesManager) {
        this.chatBubblesManager = chatBubblesManager;
    }
}
