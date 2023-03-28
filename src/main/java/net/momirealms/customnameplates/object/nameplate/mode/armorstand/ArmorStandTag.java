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

package net.momirealms.customnameplates.object.nameplate.mode.armorstand;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.object.armorstand.ArmorStandManager;
import net.momirealms.customnameplates.object.nameplate.mode.EntityTag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class ArmorStandTag extends EntityTag {

    private final VehicleChecker vehicleChecker;
    private BukkitTask vehicleCheckTask;

    public ArmorStandTag(CustomNameplates plugin) {
        super(plugin);
        super.handler = new ArmorStandPacketsHandler(this);
        this.vehicleChecker = new VehicleChecker(this);
    }

    @Override
    public void load() {
        super.load();
        this.vehicleChecker.load();
        this.vehicleCheckTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CustomNameplates.getInstance(), () -> {
           for (Player player : Bukkit.getOnlinePlayers()) {
               this.vehicleChecker.refresh(player);
           }
        },20,20);
    }

    @Override
    public void unload() {
        super.unload();
        this.vehicleChecker.unload();
        this.vehicleCheckTask.cancel();
    }

    @Override
    public void onJoin(Player player) {
        super.onJoin(player);
        ArmorStandManager asm = createArmorStandManager(player);
        asm.initNameplateArmorStands();
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            spawnArmorStands(viewer, player);
            spawnArmorStands(player, viewer);
        }
    }

    @Override
    public void loadToAllPlayers() {
        super.loadToAllPlayers();
        for (Player all : Bukkit.getOnlinePlayers()) {
            ArmorStandManager asm = createArmorStandManager(all);
            asm.initNameplateArmorStands();
            for (Player player : Bukkit.getOnlinePlayers())
                spawnArmorStands(player, all);
        }
    }

    @Override
    public void arrangeRefreshTask() {
        refreshTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CustomNameplates.getInstance(), () -> {
            for (ArmorStandManager asm : armorStandManagerMap.values()) {
                asm.refresh(false);
            }
        }, 1, 1);
    }

    @Override
    public void onSneak(Player player, boolean isSneaking) {
        getArmorStandManager(player).setSneak(isSneaking, true);
    }

    @Override
    public void onRespawn(Player player) {
        getArmorStandManager(player).teleport();
    }
}
