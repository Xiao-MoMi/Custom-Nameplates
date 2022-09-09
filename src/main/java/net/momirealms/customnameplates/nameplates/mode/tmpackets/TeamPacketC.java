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
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.nameplates.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class TeamPacketC {

    public static void clearTeamInfo() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
                packet.getIntegers().write(0,2);
                String teamName = TeamManager.getTeamName(all);
                packet.getStrings().write(0, teamName);
                Optional<InternalStructure> optional = packet.getOptionalStructures().read(0);
                if (optional.isEmpty()) return;
                InternalStructure internalStructure = optional.get();
                internalStructure.getStrings().write(0, "always");
                internalStructure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0,ChatColor.WHITE);
                try {
                    CustomNameplates.protocolManager.sendServerPacket(player, packet);
                }
                catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
