package net.momirealms.customnameplates.listener.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.manager.ActionBarManager;

public class SystemChatListener extends PacketAdapter {

    private final ActionBarManager actionBarManager;

    public SystemChatListener(ActionBarManager actionBarManager) {
        super(CustomNameplates.getInstance(), ListenerPriority.HIGHEST, PacketType.Play.Server.SYSTEM_CHAT);
        this.actionBarManager = actionBarManager;
    }

    public void onPacketSending(PacketEvent event) {
        actionBarManager.onReceiveSystemChatPacket(event);
    }
}
