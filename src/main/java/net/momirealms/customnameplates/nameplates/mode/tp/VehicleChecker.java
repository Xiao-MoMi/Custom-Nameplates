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

package net.momirealms.customnameplates.nameplates.mode.tp;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.Function;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class VehicleChecker extends Function {

    private final WeakHashMap<Player, Entity> playersOnVehicle = new WeakHashMap<>();

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
                playersOnVehicle.put(all, vehicle);
            }
        }
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(CustomNameplates.instance, () -> {
            for (Player inVehicle : playersOnVehicle.keySet()) {
                if (!inVehicle.isOnline() || teleportingTag.getArmorStandManager(inVehicle) == null) continue;
                teleportingTag.getArmorStandManager(inVehicle).teleport();
            }
        }, 1, 1);
    }

    @Override
    public void unload() {
        this.task.cancel();
        playersOnVehicle.clear();
    }

    public void onJoin(Player player) {
        Entity vehicle = player.getVehicle();
        if (vehicle != null) playersOnVehicle.put(player, vehicle);
    }

    public void onQuit(Player player) {
        playersOnVehicle.remove(player);
    }

    public void refresh(Player player) {
        Entity vehicle = player.getVehicle();
        if (playersOnVehicle.containsKey(player) && vehicle == null) {
            teleportingTag.getArmorStandManager(player).teleport();
            playersOnVehicle.remove(player);
        }
        if (!playersOnVehicle.containsKey(player) && vehicle != null) {
            teleportingTag.getArmorStandManager(player).respawn();
            playersOnVehicle.put(player, vehicle);
        }
    }
}