package net.momirealms.customnameplates.objects;

import com.comphenix.protocol.events.PacketContainer;

public class ASInfo {

    private final String text;
    private final PacketContainer entityPacket;
    private final PacketContainer metaPacket;
    private final int id;

    public ASInfo(int id ,String text, PacketContainer entityPacket, PacketContainer metaPacket) {
        this.id = id;
        this.text = text;
        this.entityPacket = entityPacket;
        this.metaPacket = metaPacket;
    }

    public String getText() {
        return text;
    }

    public PacketContainer getEntityPacket() {
        return entityPacket;
    }

    public PacketContainer getMetaPacket() {
        return metaPacket;
    }

    public int getId() {
        return id;
    }
}
