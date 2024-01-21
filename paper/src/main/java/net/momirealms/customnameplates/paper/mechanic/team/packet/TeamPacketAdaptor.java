package net.momirealms.customnameplates.paper.mechanic.team.packet;

import com.comphenix.protocol.events.PacketContainer;

public interface TeamPacketAdaptor {

    PacketContainer getTeamCreatePacket(TeamCreate teamCreate);

    PacketContainer getTeamUpdatePacket(TeamUpdate teamUpdate);

    PacketContainer getTeamRemovePacket(TeamRemove teamRemove);
}
