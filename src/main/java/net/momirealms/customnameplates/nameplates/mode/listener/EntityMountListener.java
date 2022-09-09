package net.momirealms.customnameplates.nameplates.mode.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.nameplates.mode.PacketsHandler;

public class EntityMountListener extends PacketAdapter {

    private final PacketsHandler handler;

    public EntityMountListener(PacketsHandler handler) {
        super(CustomNameplates.instance, ListenerPriority.HIGHEST, PacketType.Play.Server.MOUNT);
        this.handler = handler;
    }

    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        handler.onEntityMount(packet.getIntegers().read(0), packet.getIntegerArrays().read(0));
    }
}
