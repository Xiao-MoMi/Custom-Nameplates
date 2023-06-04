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

package net.momirealms.customnameplates.object.team.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.manager.TeamManager;
import net.momirealms.customnameplates.object.nameplate.NameplatesTeam;
import net.momirealms.customnameplates.object.team.TeamPacketInterface;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;

public class TeamVisibilityImpl implements TeamPacketInterface {

    private final TeamManager teamManager;

    public TeamVisibilityImpl(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    // this method would only be used when a player join the server
    @Override
    public void sendUpdateToOne(Player player) {
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (player == otherPlayer) continue;
            NameplatesTeam nameplatesTeam = teamManager.getNameplateTeam(otherPlayer.getUniqueId());
            if (nameplatesTeam != null) {
                PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
                packet.getIntegers().write(0,2);
                packet.getStrings().write(0, nameplatesTeam.getTeam_name());
                Optional<InternalStructure> optionalInternalStructure = packet.getOptionalStructures().read(0);
                optionalInternalStructure.ifPresent(internalStructure -> sendPackets(player, packet, internalStructure));
            }
        }
    }

    // this method would only be used when a player join the server
    @Override
    public void sendUpdateToAll(Player player, boolean force) {
        NameplatesTeam nameplatesTeam = teamManager.getNameplateTeam(player.getUniqueId());
        if (nameplatesTeam != null) {
            for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
                packet.getStrings().write(0, nameplatesTeam.getTeam_name());
                packet.getIntegers().write(0,2);
                Optional<InternalStructure> optionalInternalStructure = packet.getOptionalStructures().read(0);
                optionalInternalStructure.ifPresent(internalStructure -> sendPackets(otherPlayer, packet, internalStructure));
            }
        }
    }

    private void sendPackets(Player player, PacketContainer packet, InternalStructure internalStructure) {
        internalStructure.getStrings().write(0, "never");
        internalStructure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0, ChatColor.WHITE);
        CustomNameplates.getProtocolManager().sendServerPacket(player, packet);
    }
}
