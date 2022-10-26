package net.momirealms.customnameplates.objects.team;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.listener.ProxyDataListener;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class TABbcHook implements TeamNameInterface {

    private final HashMap<String, String> teamNames;

    public TABbcHook() {
        this.teamNames = new HashMap<>();
        ProxyDataListener proxyDataListener = new ProxyDataListener(this);
        CustomNameplates.plugin.getServer().getMessenger().registerOutgoingPluginChannel(CustomNameplates.plugin, "customnameplates:cnp");
        CustomNameplates.plugin.getServer().getMessenger().registerIncomingPluginChannel(CustomNameplates.plugin, "customnameplates:cnp", proxyDataListener);
    }

    @Override
    public void unload() {
        this.teamNames.clear();
        CustomNameplates.plugin.getServer().getMessenger().unregisterIncomingPluginChannel(CustomNameplates.plugin, "customnameplates:cnp");
        CustomNameplates.plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(CustomNameplates.plugin, "customnameplates:cnp");
    }

    @Override
    public String getTeamName(Player player) {
        String teamName = teamNames.get(player.getName());
        if (teamName == null) {
            sendRequest(player);
            return player.getName();
        }
        else {
            return teamName;
        }
    }

    private void sendRequest(Player player) {
        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
        dataOutput.writeUTF(player.getName());
        player.sendPluginMessage(CustomNameplates.plugin, "customnameplates:cnp", dataOutput.toByteArray());
    }

    public void addPlayerToCache(String playerName, String teamName) {
        teamNames.put(playerName, teamName);
    }
}
