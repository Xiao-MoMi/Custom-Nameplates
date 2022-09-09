package net.momirealms.customnameplates.objects;

public class NMSStorage {

    public Class<?> PacketPlayOutEntity;

    public void loadClasses() throws ClassNotFoundException {
        PacketPlayOutEntity = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntity");
    }
}
