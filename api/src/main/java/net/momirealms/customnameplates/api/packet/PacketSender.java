package net.momirealms.customnameplates.api.packet;

import net.momirealms.customnameplates.api.CNPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface PacketSender {

    void sendPacket(CNPlayer<?> player, Object packet);

    void sendPackets(@NotNull CNPlayer<?> player, List<Object> packet);
}
