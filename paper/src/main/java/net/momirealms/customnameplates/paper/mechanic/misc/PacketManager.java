package net.momirealms.customnameplates.paper.mechanic.misc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.util.LogUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public void send(Player player, PacketContainer... packets) {
        if (plugin.getVersionManager().isVersionNewerThan1_19_R3()) {
            List<PacketContainer> bundle = new ArrayList<>(Arrays.asList(packets));
            PacketContainer bundlePacket = new PacketContainer(PacketType.Play.Server.BUNDLE);
            bundlePacket.getPacketBundles().write(0, bundle);
            send(player, bundlePacket);
        } else {
            for (PacketContainer packet : packets) {
                send(player, packet);
            }
        }
    }
}
