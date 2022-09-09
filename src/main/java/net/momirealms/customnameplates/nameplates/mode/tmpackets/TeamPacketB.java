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

package net.momirealms.customnameplates.nameplates.mode.tmpackets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.nameplates.TeamManager;
import net.momirealms.customnameplates.nameplates.TeamPacketManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class TeamPacketB implements TeamPacketManager {

    @Override
    public void sendUpdateToOne(Player player) {
//        boolean accepted = CustomNameplates.instance.getDataManager().getCache().get(player.getUniqueId()).getAccepted();
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (player == otherPlayer) return;
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
            packet.getIntegers().write(0,2);
            String teamName = TeamManager.getTeamName(otherPlayer);
            packet.getStrings().write(0, teamName);
            Optional<InternalStructure> optional = packet.getOptionalStructures().read(0);
            if (optional.isEmpty()) return;
            InternalStructure internalStructure = optional.get();
//            if (ConfigManager.Nameplate.show_after && !accepted) {
//                internalStructure.getStrings().write(0, "always");
//                internalStructure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0,ChatColor.WHITE);
//            }
//            else {
            if (ConfigManager.Nameplate.removeTag) internalStructure.getStrings().write(0, "never");
            else internalStructure.getStrings().write(0, "always");
            internalStructure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0,ChatColor.WHITE);
//            }
            try {
                CustomNameplates.protocolManager.sendServerPacket(player, packet);
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sendUpdateToAll(Player player) {
        String teamName = TeamManager.getTeamName(player);
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
            packet.getStrings().write(0, teamName);
            packet.getIntegers().write(0,2);
            Optional<InternalStructure> optional = packet.getOptionalStructures().read(0);
            if (optional.isEmpty()) return;
            InternalStructure internalStructure = optional.get();
//            if (ConfigManager.Nameplate.show_after) {
//                PlayerData playerData = CustomNameplates.instance.getDataManager().getCache().get(otherPlayer.getUniqueId());
//                if (playerData == null || !playerData.getAccepted()) {
//                    internalStructure.getStrings().write(0, "always");
//                }
//                else {
//                    if (ConfigManager.Nameplate.removeTag) internalStructure.getStrings().write(0, "never");
//                    else internalStructure.getStrings().write(0, "always");
//                }
//            }
//            else {
            if (ConfigManager.Nameplate.removeTag) internalStructure.getStrings().write(0, "never");
            else internalStructure.getStrings().write(0, "always");
//            }
            internalStructure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0, ChatColor.WHITE);
            try {
                CustomNameplates.protocolManager.sendServerPacket(otherPlayer, packet);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
