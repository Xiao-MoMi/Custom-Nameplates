package net.momirealms.customnameplates.nameplates.mode.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.nameplates.mode.PacketsHandler;

public class EntitySpawnListener extends PacketAdapter {

    private final PacketsHandler handler;

    public EntitySpawnListener(PacketsHandler handler) {
        super(CustomNameplates.instance, ListenerPriority.HIGHEST, PacketType.Play.Server.NAMED_ENTITY_SPAWN);
        this.handler = handler;
    }

    public synchronized void onPacketSending(PacketEvent event) {
        handler.onEntitySpawn(event.getPlayer(), event.getPacket().getIntegers().read(0));
    }
}
