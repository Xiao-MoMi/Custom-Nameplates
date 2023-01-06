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

package net.momirealms.customnameplates.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.manager.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.world.WorldLoadEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

public class TeamManagePacketUtil {

    private final TeamManager teamManager;

    public TeamManagePacketUtil(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    public void createTeamToAll(Player joinPlayer) {
        PacketContainer packetToAll = getPlayerTeamPacket(joinPlayer);
        for (Player all : Bukkit.getOnlinePlayers()) {
            getPlayerTeamPacket(all);
            CustomNameplates.protocolManager.sendServerPacket(joinPlayer, getPlayerTeamPacket(all));
            if (joinPlayer != all) CustomNameplates.protocolManager.sendServerPacket(all, packetToAll);
        }
    }

    private PacketContainer getPlayerTeamPacket(Player joinPlayer) {
        PacketContainer packetToAll = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        packetToAll.getIntegers().write(0,0);
        packetToAll.getStrings().write(0, joinPlayer.getName());
        packetToAll.getModifier().write(2, Collections.singletonList(joinPlayer.getName()));
        InternalStructure internalStructure = packetToAll.getOptionalStructures().read(0).get();
        internalStructure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0,ChatColor.WHITE);
        return packetToAll;
    }

    public void destroyTeamToAll(Player quitPlayer) {
        PacketContainer packetToAll = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        packetToAll.getIntegers().write(0,1);
        packetToAll.getStrings().write(0, quitPlayer.getName());
        for (Player all : Bukkit.getOnlinePlayers()) {
            CustomNameplates.protocolManager.sendServerPacket(all, packetToAll);
        }
    }
}
