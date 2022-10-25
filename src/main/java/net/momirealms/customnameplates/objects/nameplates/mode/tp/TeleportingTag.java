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

package net.momirealms.customnameplates.objects.nameplates.mode.tp;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.manager.TeamManager;
import net.momirealms.customnameplates.objects.nameplates.ArmorStandManager;
import net.momirealms.customnameplates.objects.nameplates.mode.EntityTag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class TeleportingTag extends EntityTag {

    private VehicleChecker vehicleChecker;
    private TpPacketsHandler handler;
    private BukkitTask vehicleTask;

    public TeleportingTag(TeamManager teamManager) {
        super(teamManager);
    }

    @Override
    public void load() {
        super.load();
        this.handler = new TpPacketsHandler(this);
        this.handler.load();
        this.vehicleChecker = new VehicleChecker(this);
        this.vehicleChecker.load();
        this.vehicleTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CustomNameplates.plugin, () -> {
           for (Player player : Bukkit.getOnlinePlayers()) {
               this.vehicleChecker.refresh(player);
           }
        },20,20);
    }

    @Override
    public void unload() {
        super.unload();
        this.handler.unload();
        this.vehicleChecker.unload();
        this.vehicleTask.cancel();
    }

    @Override
    public void loadToAllPlayers() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            ArmorStandManager asm = new ArmorStandManager(all);
            asm.addDefault();
            armorStandManagerMap.put(all, asm);
            teamManager.sendUpdateToOne(all);
            teamManager.sendUpdateToAll(all, true);
            for (Player player : Bukkit.getOnlinePlayers())
                spawnArmorStands(player, all, false);
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
}
