package net.momirealms.customnameplates.nameplates.mode.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.nameplates.mode.PacketsHandler;

public class EntityLookListener extends PacketAdapter {

    private final PacketsHandler handler;

    public EntityLookListener(PacketsHandler handler) {
        super(CustomNameplates.instance, ListenerPriority.HIGHEST, PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);
        this.handler = handler;
    }

    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        handler.onEntityMove(event.getPlayer(), packet.getIntegers().read(0));
    }
}
