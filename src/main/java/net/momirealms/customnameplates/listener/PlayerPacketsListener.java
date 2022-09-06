package net.momirealms.customnameplates.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.utils.ArmorStandPacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class PlayerPacketsListener extends PacketAdapter {

    public PlayerPacketsListener(CustomNameplates plugin) {
        super(plugin, ListenerPriority.HIGHEST, PacketType.Play.Server.NAMED_ENTITY_SPAWN);
        this.plugin = plugin;
    }

    public synchronized void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        Bukkit.getScheduler().runTaskAsynchronously(CustomNameplates.instance, ()-> {
            Player player = event.getPlayer();
            Player otherPlayer = Bukkit.getPlayer(packet.getUUIDs().read(0));
            if (otherPlayer != null) {
                ArmorStandPacketUtil.sendSummonOneToOne(player, otherPlayer);
                if (ConfigManager.Nameplate.update) {
                    BukkitTask bukkitTask = new BukkitRunnable() {
                        @Override
                        public void run() {
                            ArmorStandPacketUtil.sendUpdateOneToOne(otherPlayer, player);
                        }
                    }.runTaskTimerAsynchronously(CustomNameplates.instance, 20, ConfigManager.Nameplate.refresh);
                    HashMap<Integer, BukkitTask> inner = ArmorStandPacketUtil.taskCache.get(player.getEntityId());
                    if (inner == null) {
                        inner = new HashMap<>();
                        inner.put(otherPlayer.getEntityId(), bukkitTask);
                        ArmorStandPacketUtil.taskCache.put(player.getEntityId(), inner);
                    }
                    else {
                        int other = otherPlayer.getEntityId();
                        BukkitTask bukkitTask2 = inner.get(other);
                        if (bukkitTask2 != null) {
                            bukkitTask2.cancel();
                        }
                        inner.put(other, bukkitTask);
                    }
                }
            }
        });
    }
}
