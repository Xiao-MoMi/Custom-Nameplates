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

package net.momirealms.customnameplates.listener;

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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Optional;

public record PlayerListener(CustomNameplates plugin) implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.plugin.getDataManager().loadData(event.getPlayer());
        Bukkit.getScheduler().runTaskLaterAsynchronously(CustomNameplates.instance, ()-> {
            sendPacketsToPlayer(event.getPlayer());
        }, 40);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.plugin.getDataManager().unloadPlayer(event.getPlayer().getUniqueId());
        String teamName;
        if (ConfigManager.MainConfig.tab){
            teamName = TABHook.getTABTeam(event.getPlayer().getName());
        }else {
            teamName = event.getPlayer().getName();
        }
        ScoreBoardManager.teams.remove(teamName);
    }

    @EventHandler
    public void onAccept(PlayerResourcePackStatusEvent event) {
        PlayerData playerData = DataManager.cache.get(event.getPlayer().getUniqueId());
        Bukkit.getScheduler().runTaskLaterAsynchronously(CustomNameplates.instance, ()-> {
            if (playerData == null) {
                return;
            }
            if (event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
                playerData.setAccepted(1);
            }
            else if(event.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED || event.getStatus() == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
                playerData.setAccepted(0);
            }
            sendPacketsToPlayer(event.getPlayer());
        }, 20);
    }

    private void sendPacketsToPlayer(Player player){

        if (ConfigManager.MainConfig.show_after && DataManager.cache.get(player.getUniqueId()).getAccepted() != 1) return;

        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {

            String teamName;

            if (ConfigManager.MainConfig.tab){
                teamName = TABHook.getTABTeam(onlinePlayer.getName());
            }else {
                teamName = onlinePlayer.getName();
            }

            NameplatesTeam team = ScoreBoardManager.teams.get(teamName);

            if (team == null) return;

            PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
            packetContainer.getStrings().write(0, teamName);

            Optional<InternalStructure> optional = packetContainer.getOptionalStructures().read(0);
            if (optional.isEmpty()) {
                return;
            }
            InternalStructure internalStructure1 = optional.get();
            internalStructure1.getChatComponents().write(0, WrappedChatComponent.fromJson("{\"text\":\" "+ onlinePlayer.getName() +" \"}"));

            if (team.getPrefix() != null){
                internalStructure1.getChatComponents().write(1, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(team.getPrefix())));
            }

            if (team.getSuffix() != null){
                internalStructure1.getChatComponents().write(2, WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(team.getSuffix())));
            }

            internalStructure1.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0,team.getColor());
            packetContainer.getModifier().write(2, Collections.singletonList(onlinePlayer.getName()));

            try {
                CustomNameplates.protocolManager.sendServerPacket(player, packetContainer);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }
}
