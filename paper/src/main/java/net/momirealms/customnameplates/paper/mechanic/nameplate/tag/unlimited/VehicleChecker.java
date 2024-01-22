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

package net.momirealms.customnameplates.paper.mechanic.nameplate.tag.unlimited;

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.scheduler.CancellableTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class VehicleChecker implements Listener {

    private final Set<UUID> entitiesOnVehicle;
    private final UnlimitedTagManagerImpl unlimitedTagManager;
    private CancellableTask updatePosTask;

    public VehicleChecker(UnlimitedTagManagerImpl unlimitedTagManager) {
        this.unlimitedTagManager = unlimitedTagManager;
        this.entitiesOnVehicle = Collections.synchronizedSet(new HashSet<>());
    }

    public void load() {
        Bukkit.getPluginManager().registerEvents(this, CustomNameplatesPlugin.get());
        for (Player all : Bukkit.getOnlinePlayers()) {
            Entity vehicle = all.getVehicle();
            if (vehicle != null) {
                entitiesOnVehicle.add(all.getUniqueId());
            }
        }
        this.updatePosTask = CustomNameplatesPlugin.getInstance().getScheduler().runTaskAsyncTimer(() -> {
            for (UUID inVehicle : entitiesOnVehicle) {
                UnlimitedEntity unlimitedEntity = unlimitedTagManager.getUnlimitedObject(inVehicle);
                if (unlimitedEntity != null) {
                    unlimitedEntity.teleport();
                }
            }
        }, 100, 100, TimeUnit.MILLISECONDS);
    }

    public void unload() {
        HandlerList.unregisterAll(this);
        if (this.updatePosTask != null && !this.updatePosTask.isCancelled())
            this.updatePosTask.cancel();
        this.entitiesOnVehicle.clear();
    }

    @EventHandler (ignoreCancelled = true)
    public void onMount(EntityMountEvent event) {
        final Entity passenger = event.getEntity();
        final UUID uuid = passenger.getUniqueId();
        UnlimitedEntity unlimitedEntity = unlimitedTagManager.getUnlimitedObject(uuid);
        if (unlimitedEntity != null) {
            unlimitedEntity.teleport();
            entitiesOnVehicle.add(uuid);
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onDeath(EntityDeathEvent event) {
        final UUID uuid = event.getEntity().getUniqueId();
        entitiesOnVehicle.remove(uuid);
    }

    @EventHandler (ignoreCancelled = true)
    public void onDismount(EntityDismountEvent event) {
        final Entity passenger = event.getEntity();
        final UUID uuid = passenger.getUniqueId();
        UnlimitedEntity unlimitedEntity = unlimitedTagManager.getUnlimitedObject(uuid);
        if (unlimitedEntity != null) {
            unlimitedEntity.respawn();
            entitiesOnVehicle.remove(uuid);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        Entity vehicle = player.getVehicle();
        if (vehicle != null) {
            entitiesOnVehicle.add(player.getUniqueId());
        }
    }

    @EventHandler
    public void onQuit(PlayerJoinEvent event) {
        entitiesOnVehicle.remove(event.getPlayer().getUniqueId());
    }
}