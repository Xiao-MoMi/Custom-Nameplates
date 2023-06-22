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

package net.momirealms.customnameplates.object.carrier;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.object.Function;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ConcurrentHashMap;

public class VehicleChecker extends Function {

    private final ConcurrentHashMap<Player, Entity> playersOnVehicle;

    private final NamedEntityCarrier namedEntityCarrier;

    private BukkitTask updatePosTask;
    private BukkitTask vehicleCheckTask;

    public VehicleChecker(NamedEntityCarrier namedEntityCarrier) {
        this.namedEntityCarrier = namedEntityCarrier;
        this.playersOnVehicle = new ConcurrentHashMap<>();
    }

    @Override
    public void load() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            Entity vehicle = all.getVehicle();
            if (vehicle != null) {
                playersOnVehicle.put(all, vehicle);
            }
        }
        this.updatePosTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CustomNameplates.getInstance(), () -> {
            for (Player inVehicle : playersOnVehicle.keySet()) {
                if (!inVehicle.isOnline() || namedEntityCarrier.getNamedEntityManager(inVehicle) == null) continue;
                namedEntityCarrier.getNamedEntityManager(inVehicle).teleport();
            }
        }, 1, 1);
        this.vehicleCheckTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CustomNameplates.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                this.refresh(player);
            }
        },20,20);
    }

    @Override
    public void unload() {
        this.updatePosTask.cancel();
        this.vehicleCheckTask.cancel();
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
            namedEntityCarrier.getNamedEntityManager(player).teleport();
            playersOnVehicle.remove(player);
        }
        if (!playersOnVehicle.containsKey(player) && vehicle != null) {
            namedEntityCarrier.getNamedEntityManager(player).respawn();
            playersOnVehicle.put(player, vehicle);
        }
    }
}