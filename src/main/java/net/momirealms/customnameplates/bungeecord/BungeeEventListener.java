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

package net.momirealms.customnameplates.bungeecord;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.tablist.SortingManager;
import me.neznamy.tab.shared.TAB;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Objects;

public class BungeeEventListener implements Listener {

    private final CustomNameplates plugin;
    private final SortingManager sortingManager;

    public BungeeEventListener (CustomNameplates plugin) {
        this.plugin = plugin;
        this.sortingManager = TAB.getInstance().getSortingManager();
    }

    @EventHandler
    @SuppressWarnings("UnstableApiUsage")
    public void onReceived(PluginMessageEvent event) {
        String channel = event.getTag();
        if (event.isCancelled() || !Objects.equals("customnameplates:cnp", channel)) {
            return;
        }
        ByteArrayDataInput dataInput = ByteStreams.newDataInput(event.getData());
        parseMessage(dataInput);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void parseMessage(ByteArrayDataInput dataInput) {
        String playerName = dataInput.readUTF();
        String teamName = playerName;
        if (plugin.getBungeeConfig().isTab()) {
            TabPlayer tabPlayer = TAB.getInstance().getPlayer(playerName);
            if (tabPlayer == null) return;
            teamName = sortingManager.getOriginalTeamName(tabPlayer);
        }
        ProxiedPlayer proxiedPlayer = plugin.getProxy().getPlayer(playerName);
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF(playerName);
        byteArrayDataOutput.writeUTF(teamName);
        proxiedPlayer.getServer().sendData("customnameplates:cnp", byteArrayDataOutput.toByteArray());
    }
}