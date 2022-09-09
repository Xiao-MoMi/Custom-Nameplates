package net.momirealms.customnameplates.nameplates.mode.tp;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.Function;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class VehicleChecker extends Function {

    private final WeakHashMap<Player, Entity> playersInVehicle = new WeakHashMap<>();

    private final TeleportingTag teleportingTag;

    private BukkitTask task;

    public VehicleChecker(TeleportingTag teleportingTag) {
        super("VEHICLE");
        this.teleportingTag = teleportingTag;
    }

    @Override
    public void load() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            Entity vehicle = all.getVehicle();
            if (vehicle != null) {
                playersInVehicle.put(all, vehicle);
            }
        }
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(CustomNameplates.instance, () -> {
            for (Player inVehicle : playersInVehicle.keySet()) {
                if (!inVehicle.isOnline() || teleportingTag.getArmorStandManager(inVehicle) == null) continue;
                teleportingTag.getArmorStandManager(inVehicle).teleport();
            }
        }, 1, 1);
    }

    @Override
    public void unload() {
        this.task.cancel();
        playersInVehicle.clear();
    }

    public void onJoin(Player player) {
        Entity vehicle = player.getVehicle();
        if (vehicle != null) playersInVehicle.put(player, vehicle);
    }

    public void onQuit(Player player) {
        playersInVehicle.remove(player);
    }

    public void refresh(Player player) {
        Entity vehicle = player.getVehicle();
        if (playersInVehicle.containsKey(player) && vehicle == null) {
            teleportingTag.getArmorStandManager(player).teleport();
            playersInVehicle.remove(player);
        }
        if (!playersInVehicle.containsKey(player) && vehicle != null) {
            teleportingTag.getArmorStandManager(player).respawn();
            playersInVehicle.put(player, vehicle);
        }
    }
}