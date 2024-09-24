package net.momirealms.customnameplates.api.network;

import net.momirealms.customnameplates.api.CNPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface PacketSender {

    void sendPacket(CNPlayer player, Object packet);

    void sendPacket(@NotNull CNPlayer player, List<Object> packet);
}
