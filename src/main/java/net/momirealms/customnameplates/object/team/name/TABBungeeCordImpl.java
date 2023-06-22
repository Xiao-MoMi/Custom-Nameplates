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

package net.momirealms.customnameplates.object.team.name;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.object.team.TeamNameInterface;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class TABBungeeCordImpl implements TeamNameInterface {

    private final ConcurrentHashMap<String, String> teamNameMap;
    private final ProxyDataListener proxyDataListener;

    public TABBungeeCordImpl() {
        this.teamNameMap = new ConcurrentHashMap<>();
        this.proxyDataListener = new ProxyDataListener(this);
    }

    @Override
    public void load() {
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(CustomNameplates.getInstance(), "customnameplates:cnp");
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(CustomNameplates.getInstance(), "customnameplates:cnp", proxyDataListener);
    }

    @Override
    public void unload() {
        this.teamNameMap.clear();
        Bukkit.getServer().getMessenger().unregisterIncomingPluginChannel(CustomNameplates.getInstance(), "customnameplates:cnp");
        Bukkit.getServer().getMessenger().unregisterOutgoingPluginChannel(CustomNameplates.getInstance(), "customnameplates:cnp");
    }

    @Override
    public String getTeamName(Player player) {
        String teamName = teamNameMap.get(player.getName());
        if (teamName == null) {
            sendRequest(player);
            return player.getName();
        } else {
            return teamName;
        }
    }

    @Override
    public void onJoin(Player player) {
        sendRequest(player);
    }

    @Override
    public void onQuit(Player player) {
        teamNameMap.remove(player.getName());
    }

    @SuppressWarnings("UnstableApiUsage")
    private void sendRequest(Player player) {
        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
        dataOutput.writeUTF(player.getName());
        player.sendPluginMessage(CustomNameplates.getInstance(), "customnameplates:cnp", dataOutput.toByteArray());
    }

    public void addPlayerToCache(String playerName, String teamName) {
        teamNameMap.put(playerName, teamName);
    }

    public static class ProxyDataListener implements PluginMessageListener {

        private final TABBungeeCordImpl TABBungeeCordImpl;

        public ProxyDataListener(TABBungeeCordImpl TABBungeeCordImpl) {
            this.TABBungeeCordImpl = TABBungeeCordImpl;
        }

        @Override
        @SuppressWarnings("UnstableApiUsage")
        public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
            if (!Objects.equals("customnameplates:cnp", channel)) {
                return;
            }
            ByteArrayDataInput dataInput = ByteStreams.newDataInput(message);
            String playerName = dataInput.readUTF();
            String teamName = dataInput.readUTF();
            TABBungeeCordImpl.addPlayerToCache(playerName, teamName);
        }
    }
}
