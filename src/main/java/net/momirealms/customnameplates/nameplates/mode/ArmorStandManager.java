package net.momirealms.customnameplates.nameplates.mode;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.nameplates.ArmorStand;
import net.momirealms.customnameplates.objects.TextCache;
import net.momirealms.customnameplates.utils.AdventureUtil;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ArmorStandManager {

    private final Map<String, ArmorStand> armorStands = new LinkedHashMap<>();
    private ArmorStand[] armorStandArray = new ArmorStand[0];
    private final List<Player> nearbyPlayers = new ArrayList<>();
    private Player[] nearbyPlayerArray = new Player[0];
    private final Player owner;

    public ArmorStandManager(Player owner) {
        Set<Map.Entry<String, Double>> entries = ConfigManager.Nameplate.textMap.entrySet();
        this.owner = owner;
        entries.forEach(map -> {
            addArmorStand(map.getKey(), new FakeArmorStand(this, owner, new TextCache(owner, map.getKey()), map.getValue()));
        });
    }

    public void teleport(Player viewer) {
        for (ArmorStand a : armorStandArray) a.teleport(viewer);
    }

    public void teleport() {
        for (ArmorStand a : armorStandArray) a.teleport();
    }

    public Player[] getNearbyPlayers(){
        return nearbyPlayerArray;
    }

    public boolean hasArmorStandWithID(int entityId) {
        for (ArmorStand a : armorStandArray) {
            if (a.getEntityId() == entityId) {
                return true;
            }
        }
        return false;
    }

    public void setSneak(boolean sneaking, boolean respawn) {
        for (ArmorStand a : armorStandArray) a.setSneak(sneaking, respawn);
    }

    public void respawn() {
        for (ArmorStand a : armorStandArray) {
            for (Player viewer : nearbyPlayerArray) {
                a.respawn(viewer);
            }
        }
    }

    public void spawn(Player viewer) {
        nearbyPlayers.add(viewer);
        nearbyPlayerArray = nearbyPlayers.toArray(new Player[0]);
        for (ArmorStand a : armorStandArray) a.spawn(viewer);
    }

    public void addArmorStand(String name, ArmorStand as) {
        armorStands.put(name, as);
        armorStandArray = armorStands.values().toArray(new ArmorStand[0]);
        for (Player p : nearbyPlayerArray) as.spawn(p);
    }

    public void unregisterPlayer(Player viewer) {
        if (nearbyPlayers.remove(viewer)) nearbyPlayerArray = nearbyPlayers.toArray(new Player[0]);
    }

    public boolean isNearby(Player viewer) {
        return nearbyPlayers.contains(viewer);
    }

    public void destroy(Player viewer) {
        for (ArmorStand a : armorStandArray) a.destroy(viewer);
        unregisterPlayer(viewer);
    }

    public void destroy() {
        for (ArmorStand a : armorStandArray) a.destroy();
        nearbyPlayers.clear();
        nearbyPlayerArray = new Player[0];
    }

    public void refresh(boolean force) {
        for (ArmorStand as : armorStandArray) {
            if (as.getText().update() || force) {
                as.refresh();
            }
        }
    }

    public void mount(Player receiver) {
        int[] passengers = new int[armorStandArray.length];
        for (int i = 0; i < armorStandArray.length; i++) {
            passengers[i] = armorStandArray[i].getEntityId();
        }
        try {
            CustomNameplates.protocolManager.sendServerPacket(receiver, getMountPacket(passengers));
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
            AdventureUtil.consoleMessage("<red>[CustomNameplates] Failed to mount player");
        }
    }

    private PacketContainer getMountPacket(int[] passengers) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.MOUNT);
        packet.getIntegers().write(0, owner.getEntityId());
        packet.getIntegerArrays().write(0, passengers);
        return packet;
    }
}