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

package net.momirealms.customnameplates.paper.mechanic.team;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.TeamManager;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.common.message.MessageType;
import net.momirealms.customnameplates.common.team.TeamCollisionRule;
import net.momirealms.customnameplates.common.team.TeamColor;
import net.momirealms.customnameplates.common.team.TeamTagVisibility;
import net.momirealms.customnameplates.paper.mechanic.misc.PacketManager;
import net.momirealms.customnameplates.paper.mechanic.team.packet.*;
import net.momirealms.customnameplates.paper.mechanic.team.provider.*;
import net.momirealms.customnameplates.paper.setting.CNConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

public class TeamManagerImpl implements TeamManager, PluginMessageListener {

    private final CustomNameplatesPlugin plugin;
    private final TeamPacketAdaptor teamPacketAdaptor;
    private TeamProvider teamProvider;
    private static final String CHANNEL = "customnameplates:cnp";

    public TeamManagerImpl(CustomNameplatesPlugin plugin) {
        this.plugin = plugin;
        this.teamPacketAdaptor = new TeamPacket_1_17();
    }

    /**
     * This method would only be called when CustomNameplates manage the team
     *
     * @param player player
     */
    @Override
    public void createTeam(Player player) {
        if (CNConfig.disableTeamManage) return;
        if (CNConfig.isOtherTeamPluginHooked()) return;
        String team = teamProvider.getTeam(player, null);
        if (!team.equals(player.getName())) return;
        if (CNConfig.createRealTeam) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            Team playerTeam = scoreboard.getTeam(team);
            if (playerTeam == null) {
                playerTeam = scoreboard.registerNewTeam(team);
            }
            playerTeam.addPlayer(player);
        } else {
            PacketContainer createOwner = teamPacketAdaptor.getTeamCreatePacket(
                    TeamCreate.builder()
                            .teamName(team)
                            .color(TeamColor.WHITE)
                            .display("")
                            .prefix("")
                            .suffix("")
                            .members(Collections.singletonList(player.getName()))
                            .collisionRule(TeamCollisionRule.ALWAYS)
                            .tagVisibility(TeamTagVisibility.ALWAYS)
                            .build()
            );
            for (Player online : Bukkit.getOnlinePlayers()) {
                PacketManager.getInstance().send(online, createOwner);
                if (online == player) continue;
                String onlineTeam = teamProvider.getTeam(online, null);
                PacketContainer createOther = teamPacketAdaptor.getTeamCreatePacket(
                        TeamCreate.builder()
                                .teamName(onlineTeam)
                                .color(TeamColor.WHITE)
                                .display("")
                                .prefix("")
                                .suffix("")
                                .members(Collections.singletonList(online.getName()))
                                .collisionRule(TeamCollisionRule.ALWAYS)
                                .tagVisibility(TeamTagVisibility.ALWAYS)
                                .build()
                );
                PacketManager.getInstance().send(player, createOther);
            }
        }
    }

    /**
     * This method would only be called when CustomNameplates manage the team
     *
     * @param player player
     */
    @Override
    public void removeTeam(Player player) {
        if (CNConfig.disableTeamManage) return;
        if (CNConfig.isOtherTeamPluginHooked()) return;
        String team = teamProvider.getTeam(player, null);
        // If the team is created by other plugins
        if (!team.equals(player.getName())) return;
        if (CNConfig.createRealTeam) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            Optional.ofNullable(scoreboard.getTeam(team)).ifPresent(Team::unregister);
        } else {
            PacketContainer packet = teamPacketAdaptor.getTeamRemovePacket(
                    TeamRemove.builder()
                            .teamName(team)
                            .build()
            );
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (player == online) continue;
                PacketManager.getInstance().send(online, packet);
            }
        }
    }

    @Override
    public void updateTeam(Player owner, Player viewer, String prefix, String suffix, TeamColor color, TeamTagVisibility visibility) {
        if (CNConfig.disableTeamManage) return;
        String team = teamProvider.getTeam(owner, viewer);
        if (team == null) {
            LogUtils.warn("Failed to get player " + owner.getName() + "'s team for viewer " + viewer.getName());
            return;
        }
        if (color == TeamColor.NONE || color == TeamColor.CUSTOM)
            color = TeamColor.WHITE;
        if (plugin.getNameplateManager().isProxyMode()) {
            this.sendPluginMessage(
                    MessageType.UPDATE,
                    owner.getName(),
                    viewer.getName(),
                    prefix,
                    suffix,
                    color.name(),
                    visibility.name()
            );
        } else {
            PacketContainer packet = teamPacketAdaptor.getTeamUpdatePacket(
                    TeamUpdate.builder()
                            .teamName(team)
                            .color(color)
                            .display("")
                            .prefix(prefix)
                            .suffix(suffix)
                            .tagVisibility(visibility)
                            .collisionRule(TeamCollisionRule.ALWAYS)
                            .build()
            );
            PacketManager.getInstance().send(viewer, packet);
        }
    }

    public void reload() {
        unload();
        load();
    }

    public void unload() {
        if (teamProvider instanceof Listener listener) {
            HandlerList.unregisterAll(listener);
        }
        if (teamProvider instanceof PacketAdapter adapter) {
            ProtocolLibrary.getProtocolManager().removePacketListener(adapter);
        }
        Bukkit.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, CHANNEL);
        Bukkit.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin, CHANNEL);
    }

    public void load() {
        if (CNConfig.tabTeam) {
            teamProvider = new TABProvider();
        } else if (CNConfig.cmiTeam) {
            teamProvider = new CMIProvider();
        } else if (CNConfig.unknownTeam) {
            teamProvider = new UnknownProvider();
        } else {
            teamProvider = new DefaultProvider();
        }
        if (teamProvider instanceof Listener listener) {
            Bukkit.getPluginManager().registerEvents(listener, plugin);
        }
        if (teamProvider instanceof PacketAdapter adapter) {
            ProtocolLibrary.getProtocolManager().addPacketListener(adapter);
        }
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL);
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(plugin, CHANNEL, this);
    }

    @Override
    public String getTeamName(Player player, Player viewer) {
        return teamProvider.getTeam(player, viewer);
    }

    private void handleMessage(String... message) {

    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!Objects.equals(CHANNEL, channel)) {
            return;
        }
        ByteArrayDataInput dataInput = ByteStreams.newDataInput(message);
        byte args = dataInput.readByte();
        String[] messages = new String[args];
        for (int i = 0; i < args; i++) {
            messages[i] = dataInput.readUTF();
        }
        handleMessage(messages);
    }

    @SuppressWarnings("UnstableApiUsage")
    public void sendPluginMessage(String... messages) {
        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
        dataOutput.writeByte(messages.length);
        for (String message : messages) {
            dataOutput.writeUTF(message);
        }
        Bukkit.getOnlinePlayers().stream().findAny().ifPresent(player -> {
            player.sendPluginMessage(plugin, CHANNEL, dataOutput.toByteArray());
        });
    }

    public void disable() {
        unload();
        if (CNConfig.disableTeamManage) return;
        if (CNConfig.isOtherTeamPluginHooked()) return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            removeTeam(player);
        }
    }
}
