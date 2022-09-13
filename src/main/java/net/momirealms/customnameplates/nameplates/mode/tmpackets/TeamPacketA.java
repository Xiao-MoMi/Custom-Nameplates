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
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.hook.TABTeamHook;
import net.momirealms.customnameplates.nameplates.NameplatesTeam;
import net.momirealms.customnameplates.nameplates.TeamInfo;
import net.momirealms.customnameplates.nameplates.TeamManager;
import net.momirealms.customnameplates.nameplates.TeamPacketManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Optional;

public class TeamPacketA implements TeamPacketManager {

    private final HashMap<Player, TeamInfo> teamInfoCache = new HashMap<>();

    private final TeamManager teamManager;

    public TeamPacketA(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    public void sendUpdateToOne(Player player) {
//        boolean accepted = CustomNameplates.instance.getDataManager().getCache().get(player.getUniqueId()).getAccepted();
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (player == otherPlayer) return;
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
            packet.getIntegers().write(0,2);
            String teamName = TeamManager.getTeamName(otherPlayer);
            packet.getStrings().write(0, teamName);
            NameplatesTeam nameplatesTeam = teamManager.getTeams().get(teamName);
            if (nameplatesTeam == null) return;
            Optional<InternalStructure> optional = packet.getOptionalStructures().read(0);
            if (optional.isEmpty()) return;
            InternalStructure internalStructure = optional.get();
            internalStructure.getStrings().write(0, "always");
//            if (ConfigManager.Nameplate.show_after && !accepted) {
//                internalStructure.getChatComponents().write(1, WrappedChatComponent.fromJson("{\"text\":\"\"}"));
//                internalStructure.getChatComponents().write(2, WrappedChatComponent.fromJson("{\"text\":\"\"}"));
//                internalStructure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0,ChatColor.WHITE);
//            }
//            else {
            internalStructure.getChatComponents().write(1, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(nameplatesTeam.getPrefix())));
            internalStructure.getChatComponents().write(2, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(nameplatesTeam.getSuffix())));
            internalStructure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0,nameplatesTeam.getColor());
//            }
            try {
                CustomNameplates.protocolManager.sendServerPacket(player, packet);
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendUpdateToAll(Player player) {
        String teamName = TeamManager.getTeamName(player);
        NameplatesTeam nameplatesTeam = teamManager.getTeams().get(teamName);
        TeamInfo newInfo = new TeamInfo(nameplatesTeam.getPrefixText(), nameplatesTeam.getSuffixText());
        TeamInfo oldInfo = teamInfoCache.put(player, newInfo);
        if (oldInfo != null && oldInfo.equals(newInfo)) return;
        teamInfoCache.put(player, newInfo);
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
            packet.getStrings().write(0, teamName);
            packet.getIntegers().write(0,2);
            Optional<InternalStructure> optional = packet.getOptionalStructures().read(0);
            if (optional.isEmpty()) return;
            InternalStructure internalStructure = optional.get();
            internalStructure.getStrings().write(0, "always");
//            if (ConfigManager.Nameplate.show_after) {
//                PlayerData playerData = CustomNameplates.instance.getDataManager().getCache().get(otherPlayer.getUniqueId());
//                if (playerData == null || !playerData.getAccepted()) {
//                    internalStructure.getChatComponents().write(1, WrappedChatComponent.fromJson("{\"text\":\"\"}"));
//                    internalStructure.getChatComponents().write(2, WrappedChatComponent.fromJson("{\"text\":\"\"}"));
//                    internalStructure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0,ChatColor.WHITE);
//                }
//                else {
//                    internalStructure.getChatComponents().write(1, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(nameplatesTeam.getPrefix())));
//                    internalStructure.getChatComponents().write(2, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(nameplatesTeam.getSuffix())));
//                    internalStructure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0,nameplatesTeam.getColor());
//                }
//            }
//            else {
            internalStructure.getChatComponents().write(1, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(nameplatesTeam.getPrefix())));
            internalStructure.getChatComponents().write(2, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(nameplatesTeam.getSuffix())));
            internalStructure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0,nameplatesTeam.getColor());
//            }
            try {
                CustomNameplates.protocolManager.sendServerPacket(otherPlayer, packet);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
