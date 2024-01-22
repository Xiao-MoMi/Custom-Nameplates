/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customnameplates.paper.mechanic.team.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import net.momirealms.customnameplates.common.team.TeamColor;

import java.util.Optional;

@SuppressWarnings("DuplicatedCode")
public class TeamPacket_1_17 implements TeamPacketAdaptor {

    @Override
    public PacketContainer getTeamCreatePacket(TeamCreate teamCreate) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        // 0 = create team
        packet.getModifier().write(0,0);
        packet.getModifier().write(1, teamCreate.getTeamName());
        packet.getModifier().write(2, teamCreate.getMembers());
        Optional<InternalStructure> optionalInternalStructure = packet.getOptionalStructures().read(0);
        if (optionalInternalStructure.isPresent()) {
            InternalStructure is = optionalInternalStructure.get();
            // Team
            is.getModifier().write(0, teamCreate.getTeamDisplay());
            is.getModifier().write(1, teamCreate.getTeamPrefix());
            is.getModifier().write(2, teamCreate.getTeamSuffix());
            is.getModifier().write(3, teamCreate.getTagVisibility().getId());
            is.getModifier().write(4, teamCreate.getCollisionRule().getId());
            is.getEnumModifier(TeamColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0, teamCreate.getTeamColor());
            is.getModifier().write(6, teamCreate.getMembers().size());
        }
        return packet;
    }

    @Override
    public PacketContainer getTeamRemovePacket(TeamRemove teamRemove) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        // 1 = remove team
        packet.getModifier().write(0,1);
        packet.getStrings().write(0, teamRemove.getTeamName());
        return packet;
    }

    @Override
    public PacketContainer getTeamUpdatePacket(TeamUpdate teamUpdate) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        // 2 = update team
        packet.getModifier().write(0,2);
        packet.getModifier().write(1, teamUpdate.getTeamName());
        Optional<InternalStructure> optionalInternalStructure = packet.getOptionalStructures().read(0);
        if (optionalInternalStructure.isPresent()) {
            InternalStructure is = optionalInternalStructure.get();
            // Team
            is.getModifier().write(0, teamUpdate.getTeamDisplay());
            is.getModifier().write(1, teamUpdate.getTeamPrefix());
            is.getModifier().write(2, teamUpdate.getTeamSuffix());
            is.getModifier().write(3, teamUpdate.getTagVisibility().getId());
            is.getModifier().write(4, teamUpdate.getCollisionRule().getId());
            is.getEnumModifier(TeamColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0, teamUpdate.getTeamColor());
        }
        return packet;
    }
}
