package net.momirealms.customnameplates.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.utils.ArmorStandPacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;

public class EntityDestroyListener extends PacketAdapter {

    public EntityDestroyListener(CustomNameplates plugin) {
        super(plugin, ListenerPriority.HIGHEST, PacketType.Play.Server.ENTITY_DESTROY);
        this.plugin = plugin;
    }

    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        List<Integer> ids = packet.getIntLists().read(0);
        Player player = event.getPlayer();
        for (int id : ids) {
            int[] armor = ArmorStandPacketUtil.id2ids.get(id);
            if (armor != null) {
                ArmorStandPacketUtil.sendDestroyToOne(player, armor);
                HashMap<Integer, BukkitTask> taskMap = ArmorStandPacketUtil.taskCache.get(player.getEntityId());
                if (taskMap != null) {
                    BukkitTask task = taskMap.remove(id);
                    if (task != null) {
                        task.cancel();
                    }
                }
            }
        }
    }
}
