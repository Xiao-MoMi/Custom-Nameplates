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

package net.momirealms.customnameplates.nameplates.mode.rd;

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

public class RidingTag extends EntityTag {

    private RdPacketsHandler handler;

    private EventListenerE listener;

    private BukkitTask refreshTask;

    public RidingTag(String name) {
        super(name);
    }

    @Override
    public void load() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            ArmorStandManager asm = new ArmorStandManager(all);
            asm.addDefault();
            armorStandManagerMap.put(all, asm);
            CustomNameplates.instance.getTeamPacketManager().sendUpdateToOne(all);
            CustomNameplates.instance.getTeamPacketManager().sendUpdateToAll(all);
            for (Player player : Bukkit.getOnlinePlayers())
                ridingArmorStands(player, all);
        }
        this.handler = new RdPacketsHandler("RdHandler", this);
        this.handler.load();
        listener = new EventListenerE(this);
        Bukkit.getPluginManager().registerEvents(listener, CustomNameplates.instance);
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
        if (refreshTask != null) {
            refreshTask.cancel();
        }
        this.handler.unload();
        HandlerList.unregisterAll(listener);
        super.unload();
    }


//    @Override
//    public void onRP(Player player, PlayerResourcePackStatusEvent.Status status) {
//        super.onRP(player, status);
//    }

    @Override
    public ArmorStandManager getArmorStandManager(Player player) {
        return super.getArmorStandManager(player);
    }

    @Override
    public void onSneak(Player player, boolean isSneaking) {
        getArmorStandManager(player).setSneak(isSneaking, false);
    }

    @Override
    public void onRespawn(Player player) {
        //getArmorStandManager(player).teleport();
    }

    private void ridingArmorStands(Player viewer, Player target) {
        if (target == viewer) return;
        if (viewer.getWorld() != target.getWorld()) return;
        if (getDistance(target, viewer) < 48 && viewer.canSee(target)) {
            ArmorStandManager asm = getArmorStandManager(target);
            asm.spawn(viewer);
            Bukkit.getScheduler().runTaskAsynchronously(CustomNameplates.instance, () -> {
                asm.mount(viewer);
            });
        }
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

    private void spawnArmorStands(Player viewer, Player target) {
        if (target == viewer) return;
        if (viewer.getWorld() != target.getWorld()) return;
        if (getDistance(target, viewer) < 48 && viewer.canSee(target))
            getArmorStandManager(target).spawn(viewer);
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
