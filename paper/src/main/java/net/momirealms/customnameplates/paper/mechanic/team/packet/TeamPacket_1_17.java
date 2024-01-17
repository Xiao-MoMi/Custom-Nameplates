package net.momirealms.customnameplates.paper.mechanic.team.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customnameplates.api.mechanic.team.TeamColor;
import net.momirealms.customnameplates.api.mechanic.team.TeamCreatePacket;
import net.momirealms.customnameplates.api.mechanic.team.TeamRemovePacket;
import net.momirealms.customnameplates.api.mechanic.team.TeamUpdatePacket;

import java.util.Optional;

@SuppressWarnings("DuplicatedCode")
public class TeamPacket_1_17 implements TeamPacketAdaptor {

    @Override
    public PacketContainer getTeamCreatePacket(TeamCreatePacket teamCreatePacket) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        // 0 = create team
        packet.getModifier().write(0,0);
        packet.getModifier().write(1, teamCreatePacket.getTeamName());
        packet.getModifier().write(2, teamCreatePacket.getMembers());
        Optional<InternalStructure> optionalInternalStructure = packet.getOptionalStructures().read(0);
        if (optionalInternalStructure.isPresent()) {
            InternalStructure is = optionalInternalStructure.get();
            // Team
            is.getChatComponents().write(0, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(teamCreatePacket.getTeamDisplay())));
            is.getChatComponents().write(1, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(teamCreatePacket.getTeamPrefix())));
            is.getChatComponents().write(2, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(teamCreatePacket.getTeamSuffix())));
            is.getModifier().write(3, teamCreatePacket.getTagVisibility().getId());
            is.getModifier().write(4, teamCreatePacket.getCollisionRule().getId());
            is.getEnumModifier(TeamColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0, teamCreatePacket.getTeamColor());
            is.getModifier().write(6, teamCreatePacket.getMembers().size());
        }
        return packet;
    }

    @Override
    public PacketContainer getTeamRemovePacket(TeamRemovePacket teamRemovePacket) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        // 1 = remove team
        packet.getModifier().write(0,1);
        packet.getStrings().write(0, teamRemovePacket.getTeamName());
        return packet;
    }

    @Override
    public PacketContainer getTeamUpdatePacket(TeamUpdatePacket teamUpdatePacket) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        // 2 = update team
        packet.getModifier().write(0,2);
        packet.getModifier().write(1, teamUpdatePacket.getTeamName());
        Optional<InternalStructure> optionalInternalStructure = packet.getOptionalStructures().read(0);
        if (optionalInternalStructure.isPresent()) {
            InternalStructure is = optionalInternalStructure.get();
            // Team
            is.getChatComponents().write(0, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(teamUpdatePacket.getTeamDisplay())));
            is.getChatComponents().write(1, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(teamUpdatePacket.getTeamPrefix())));
            is.getChatComponents().write(2, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(teamUpdatePacket.getTeamSuffix())));
            is.getModifier().write(3, teamUpdatePacket.getTagVisibility().getId());
            is.getModifier().write(4, teamUpdatePacket.getCollisionRule().getId());
            is.getEnumModifier(TeamColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0, teamUpdatePacket.getTeamColor());
        }
        return packet;
    }
}
