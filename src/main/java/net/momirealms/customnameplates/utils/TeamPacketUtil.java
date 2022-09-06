package net.momirealms.customnameplates.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.data.DataManager;
import net.momirealms.customnameplates.data.PlayerData;
import net.momirealms.customnameplates.hook.TABHook;
import net.momirealms.customnameplates.scoreboard.NameplatesTeam;
import net.momirealms.customnameplates.scoreboard.ScoreBoardManager;
import net.momirealms.customnameplates.scoreboard.TeamInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Optional;

public class TeamPacketUtil {

    public static HashMap<Player, TeamInfo> teamInfoCache = new HashMap<>();

    public static void sendUpdateToOne(Player player) {
        boolean accepted = DataManager.cache.get(player.getUniqueId()).getAccepted() == 1;
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (player == otherPlayer) return;
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
            packet.getIntegers().write(0,2);
            String teamName = otherPlayer.getName();
            if (ConfigManager.MainConfig.tab) teamName = TABHook.getTABTeam(teamName);
            packet.getStrings().write(0, teamName);
            NameplatesTeam nameplatesTeam = ScoreBoardManager.teams.get(teamName);
            if (nameplatesTeam != null) {
                Optional<InternalStructure> optional = packet.getOptionalStructures().read(0);
                if (optional.isPresent()) {
                    InternalStructure internalStructure = optional.get();
                    if (ConfigManager.Nameplate.show_after && !accepted) {
                        internalStructure.getChatComponents().write(1, WrappedChatComponent.fromJson("{\"text\":\"\"}"));
                        internalStructure.getChatComponents().write(2, WrappedChatComponent.fromJson("{\"text\":\"\"}"));
                        internalStructure.getStrings().write(0, "always");
                        internalStructure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0,ChatColor.WHITE);
                    }
                    else {
                        if (ConfigManager.Nameplate.removeTag) {
                            internalStructure.getStrings().write(0, "never");
                        }
                        else {
                            internalStructure.getStrings().write(0, "always");
                        }
                        internalStructure.getChatComponents().write(1, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(nameplatesTeam.getPrefix())));
                        internalStructure.getChatComponents().write(2, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(nameplatesTeam.getSuffix())));
                        internalStructure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0,nameplatesTeam.getColor());
                    }
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

    public static void sendUpdateToAll(Player player) {
        String teamName = player.getName();
        if (ConfigManager.MainConfig.tab) teamName = TABHook.getTABTeam(teamName);
        NameplatesTeam nameplatesTeam = ScoreBoardManager.teams.get(teamName);
        TeamInfo newInfo = new TeamInfo(nameplatesTeam.getPrefixText(), nameplatesTeam.getSuffixText());
        TeamInfo oldInfo = teamInfoCache.put(player, newInfo);
        if (oldInfo == null || !oldInfo.equals(newInfo)) {
            for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
                packet.getStrings().write(0, teamName);
                packet.getIntegers().write(0,2);
                Optional<InternalStructure> optional = packet.getOptionalStructures().read(0);
                if (optional.isPresent()) {
                    InternalStructure internalStructure = optional.get();

                    if (ConfigManager.Nameplate.show_after) {
                        PlayerData playerData = DataManager.cache.get(otherPlayer.getUniqueId());
                        if (playerData == null || playerData.getAccepted() == 0) {
                            internalStructure.getChatComponents().write(1, WrappedChatComponent.fromJson("{\"text\":\"\"}"));
                            internalStructure.getChatComponents().write(2, WrappedChatComponent.fromJson("{\"text\":\"\"}"));
                            internalStructure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0,ChatColor.WHITE);
                        }
                        else {
                            if (ConfigManager.Nameplate.removeTag) {
                                internalStructure.getStrings().write(0, "never");
                            }
                            else {
                                internalStructure.getStrings().write(0, "always");
                                internalStructure.getChatComponents().write(1, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(nameplatesTeam.getPrefix())));
                                internalStructure.getChatComponents().write(2, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(nameplatesTeam.getSuffix())));
                                internalStructure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0,nameplatesTeam.getColor());
                            }
                        }
                    }
                    else {
                        if (ConfigManager.Nameplate.removeTag) {
                            internalStructure.getStrings().write(0, "never");
                        }
                        else {
                            internalStructure.getStrings().write(0, "always");
                            internalStructure.getChatComponents().write(1, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(nameplatesTeam.getPrefix())));
                            internalStructure.getChatComponents().write(2, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(nameplatesTeam.getSuffix())));
                            internalStructure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0,nameplatesTeam.getColor());
                        }
                    }
                }
                try {
                    CustomNameplates.protocolManager.sendServerPacket(otherPlayer, packet);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
