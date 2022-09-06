package net.momirealms.customnameplates.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.utils.ArmorStandPacketUtil;

public class MountPacketListener extends PacketAdapter {

    public MountPacketListener(CustomNameplates plugin) {
        super(plugin, ListenerPriority.HIGHEST, PacketType.Play.Server.MOUNT);
        this.plugin = plugin;
    }

    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        int[] ids = ArmorStandPacketUtil.id2ids.get(packet.getIntegers().read(0));
        if (ids != null) {
            int[] old = packet.getIntegerArrays().read(0);
            int[] idArray = new int[ids.length + old.length];
            int i = 0;
            while (i < ids.length) {
                idArray[i] = ids[i];
                i ++;
            }
            while (i < ids.length + old.length) {
                idArray[i] = old[i - ids.length];
                i ++;
            }
            packet.getModifier().write(1, idArray);
        }
    }
}
