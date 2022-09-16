package net.momirealms.customnameplates.bungeecord;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.momirealms.customnameplates.hook.TABTeamHook;

import java.util.Objects;

public class BungeeEventListener implements Listener {

    private final Main plugin;

    public BungeeEventListener (Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onReceived(PluginMessageEvent event) {
        String channel = event.getTag();
        if (event.isCancelled() || !Objects.equals("customnameplates:cnp", channel)) {
            return;
        }
        ByteArrayDataInput dataInput = ByteStreams.newDataInput(event.getData());
        parseMessage(dataInput);
    }

    private void parseMessage(ByteArrayDataInput dataInput) {
        String playerName = dataInput.readUTF();
        String teamName = playerName;
        if (plugin.getBungeeConfig().isTab()) {
            teamName = TABTeamHook.getTABTeam(playerName);
        }
        ProxiedPlayer proxiedPlayer = plugin.getProxy().getPlayer(playerName);
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF(playerName);
        byteArrayDataOutput.writeUTF(teamName);
        proxiedPlayer.getServer().sendData("customnameplates:cnp", byteArrayDataOutput.toByteArray());
    }
}
