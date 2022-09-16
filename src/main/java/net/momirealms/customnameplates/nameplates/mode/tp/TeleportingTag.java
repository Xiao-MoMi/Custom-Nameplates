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

import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.nameplates.ArmorStandManager;
import net.momirealms.customnameplates.nameplates.mode.EntityTag;
import net.momirealms.customnameplates.nameplates.mode.EventListenerE;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitTask;

public class TeleportingTag extends EntityTag {

    private EventListenerE listener;

    private VehicleChecker vehicleChecker;

    private TpPacketsHandler handler;

    private BukkitTask vehicleTask;

    private BukkitTask refreshTask;

    public TeleportingTag(String name) {
        super(name);
    }

    @Override
    public void load() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            ArmorStandManager asm = new ArmorStandManager(all);
            asm.addDefault();
            armorStandManagerMap.put(all, asm);
            CustomNameplates.instance.getTeamPacketManager().sendUpdateToOne(all);
            CustomNameplates.instance.getTeamPacketManager().sendUpdateToAll(all, true);
            for (Player player : Bukkit.getOnlinePlayers())
                spawnArmorStands(player, all);
        }
        listener = new EventListenerE(this);
        Bukkit.getPluginManager().registerEvents(listener, CustomNameplates.instance);
        this.handler = new TpPacketsHandler("TpHandler", this);
        this.handler.load();
        this.vehicleChecker = new VehicleChecker(this);
        this.vehicleChecker.load();
        this.vehicleTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CustomNameplates.instance, () -> {
           for (Player player : Bukkit.getOnlinePlayers()) {
               this.vehicleChecker.refresh(player);
           }
        },10,20);
        if (ConfigManager.Nameplate.update) {
            refreshTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CustomNameplates.instance, () -> {
                for (ArmorStandManager asm : armorStandManagerMap.values()) {
                    asm.refresh(false);
                }
            }, 20, ConfigManager.Nameplate.refresh);
        }
        else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(CustomNameplates.instance, () -> {
                for (ArmorStandManager asm : armorStandManagerMap.values()) {
                    asm.refresh(false);
                }
            }, 20);
        }
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(listener);
        super.unload();
        this.handler.unload();
        this.vehicleChecker.unload();
        this.vehicleTask.cancel();
        if (refreshTask != null) {
            refreshTask.cancel();
        }
    }

    @Override
    public void onSneak(Player player, boolean isSneaking) {
        getArmorStandManager(player).setSneak(isSneaking, true);
    }

    @Override
    public void onRespawn(Player player) {
        getArmorStandManager(player).teleport();
    }

    private void spawnArmorStands(Player viewer, Player target) {
        if (target == viewer) return;
        if (viewer.getWorld() != target.getWorld()) return;
        if (getDistance(target, viewer) < 48 && viewer.canSee(target))
            getArmorStandManager(target).spawn(viewer);
    }

    private double getDistance(Player player1, Player player2) {
        Location loc1 = player1.getLocation();
        Location loc2 = player2.getLocation();
        return Math.sqrt(Math.pow(loc1.getX()-loc2.getX(), 2) + Math.pow(loc1.getZ()-loc2.getZ(), 2));
    }

    @Override
    public void onJoin(Player player) {
        ArmorStandManager asm = new ArmorStandManager(player);
        asm.addDefault();
        armorStandManagerMap.put(player, asm);
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            spawnArmorStands(viewer, player);
            spawnArmorStands(player, viewer);
        }
        super.onJoin(player);
    }

    @Override
    public void onQuit(Player player) {
//        for (Player all : Bukkit.getOnlinePlayers()) {
//            if (getArmorStandManager(all) == null) continue;
//            getArmorStandManager(all) .unregisterPlayer(player);
//        }
        ArmorStandManager asm = armorStandManagerMap.remove(player);
        if (asm != null) {
            asm.destroy();
        }
        super.onQuit(player);
    }
}
