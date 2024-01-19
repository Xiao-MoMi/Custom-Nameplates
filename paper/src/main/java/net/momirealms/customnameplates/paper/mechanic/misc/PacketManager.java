package net.momirealms.customnameplates.paper.mechanic.misc;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.util.LogUtils;
import org.bukkit.entity.Player;

public class PacketManager {

    private static PacketManager instance;
    private final ProtocolManager protocolManager;
    private final CustomNameplatesPlugin plugin;

    public PacketManager(CustomNameplatesPlugin plugin) {
        this.plugin = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        instance = this;
    }

    public static PacketManager getInstance() {
        return instance;
    }

    public void send(Player player, PacketContainer packet) {
        if (!player.isOnline()) {
            LogUtils.warn(player.getName() + " not online");
        }
        this.plugin.debug("Packet sent: " + packet.getType() + " to " + player.getName());
        this.protocolManager.sendServerPacket(player, packet);
    }
}
