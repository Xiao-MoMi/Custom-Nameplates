package net.momirealms.customnameplates.paper.mechanic.team.packet;

import com.comphenix.protocol.events.PacketContainer;
import net.momirealms.customnameplates.api.mechanic.team.TeamCreatePacket;
import net.momirealms.customnameplates.api.mechanic.team.TeamRemovePacket;
import net.momirealms.customnameplates.api.mechanic.team.TeamUpdatePacket;

public interface TeamPacketAdaptor {

    PacketContainer getTeamCreatePacket(TeamCreatePacket teamCreatePacket);

    PacketContainer getTeamUpdatePacket(TeamUpdatePacket teamUpdatePacket);

    PacketContainer getTeamRemovePacket(TeamRemovePacket teamRemovePacket);
}
