package net.momirealms.customnameplates.bukkit.compatibility.chat;

import com.alessiodp.parties.api.events.bukkit.player.BukkitPartiesPlayerPreChatEvent;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.chat.AbstractChatMessageProvider;
import net.momirealms.customnameplates.api.feature.chat.ChatManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PartiesChatProvider extends AbstractChatMessageProvider implements Listener {

    public PartiesChatProvider(CustomNameplates plugin, ChatManager manager) {
        super(plugin, manager);
    }

    @Override
    public boolean hasJoinedChannel(CNPlayer player, String channelID) {
        return false;
    }

    @Override
    public boolean canJoinChannel(CNPlayer player, String channelID) {
        return false;
    }

    @Override
    public boolean isIgnoring(CNPlayer sender, CNPlayer receiver) {
        return false;
    }

    @EventHandler
    public void onPlayerChat(BukkitPartiesPlayerPreChatEvent event) {

    }
}
