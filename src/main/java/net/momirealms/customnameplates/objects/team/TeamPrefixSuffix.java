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

package net.momirealms.customnameplates.objects.team;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.manager.TeamManager;
import net.momirealms.customnameplates.objects.nameplates.NameplatesTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Optional;

public class TeamPrefixSuffix implements TeamPacketInterface {

    private final HashMap<Player, String> teamInfoCache = new HashMap<>();

    private final TeamManager teamManager;

    public TeamPrefixSuffix(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    public void sendUpdateToOne(Player player) {
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (player == otherPlayer) continue;
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
            packet.getIntegers().write(0,2);
            String teamName = teamManager.getTeamName(otherPlayer);
            packet.getStrings().write(0, teamName);
            NameplatesTeam nameplatesTeam = teamManager.getTeams().get(teamName);
            if (nameplatesTeam == null) continue;
            InternalStructure internalStructure = packet.getOptionalStructures().read(0).get();
            sendPacket(player, packet, nameplatesTeam, internalStructure);
        }
    }
    public void sendUpdateToAll(Player player, boolean force) {
        String teamName = teamManager.getTeamName(player);
        NameplatesTeam nameplatesTeam = teamManager.getTeams().get(teamName);
        String newInfo = nameplatesTeam.getDynamic();
        String oldInfo = teamInfoCache.get(player);
        if (newInfo != null && newInfo.equals(oldInfo) && !force) {
            return;
        }
        teamInfoCache.put(player, newInfo);
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
            packet.getStrings().write(0, teamName);
            packet.getIntegers().write(0,2);
            Optional<InternalStructure> optional = packet.getOptionalStructures().read(0);
            if (optional.isEmpty()) return;
            InternalStructure internalStructure = optional.get();
            sendPacket(otherPlayer, packet, nameplatesTeam, internalStructure);
        }
    }

    private void sendPacket(Player player, PacketContainer packet, NameplatesTeam nameplatesTeam, InternalStructure internalStructure) {
        internalStructure.getStrings().write(0, "always");
        internalStructure.getChatComponents().write(1, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(nameplatesTeam.getPrefix())));
        internalStructure.getChatComponents().write(2, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(nameplatesTeam.getSuffix())));
        internalStructure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0, nameplatesTeam.getColor());
        CustomNameplates.protocolManager.sendServerPacket(player, packet);
    }
}
