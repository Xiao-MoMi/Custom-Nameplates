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

package net.momirealms.customnameplates.nameplates.mode;

import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public abstract class EntityTag extends NameplateManager {

    protected final HashMap<Player, ArmorStandManager> armorStandManagerMap = new HashMap<>();

    private BukkitTask bukkitTask;

    protected EntityTag(String name) {
        super(name);
    }

    @Override
    public void load(){
        if (ConfigManager.Nameplate.update) {
            this.bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CustomNameplates.instance, () -> {
                for (ArmorStandManager asm : armorStandManagerMap.values()) {
                    asm.refresh(false);
                }
            }, 20, ConfigManager.Nameplate.refresh);
        }
    }

    @Override
    public void unload(){
        if (bukkitTask != null) {
            this.bukkitTask.cancel();
        }
        for (Player all : Bukkit.getOnlinePlayers()) {
            getArmorStandManager(all).destroy();
        }
        armorStandManagerMap.clear();
    }

    @Override
    public void onJoin(Player player) {
        super.onJoin(player);
        armorStandManagerMap.put(player, new ArmorStandManager(player));
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            spawnArmorStands(viewer, player);
            spawnArmorStands(player, viewer);
        }
    }

    @Override
    public void onQuit(Player player) {
        super.onQuit(player);
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (getArmorStandManager(all) == null) continue;
            getArmorStandManager(all) .unregisterPlayer(player);
        }
        ArmorStandManager asm = armorStandManagerMap.remove(player);
        if (asm != null) {
            asm.destroy();
        }
    }

//    @Override
//    public void onRP(Player player, PlayerResourcePackStatusEvent.Status status) {
//        super.onRP(player, status);
//    }

    public void onSneak(Player player, boolean isSneaking) {
    }

    public void onRespawn(Player player) {
    }

    public ArmorStandManager getArmorStandManager(Player player) {
        return armorStandManagerMap.get(player);
    }

    public void refresh(Player refreshed, boolean force) {
        getArmorStandManager(refreshed).refresh(force);
    }

    private void spawnArmorStands(Player viewer, Player target) {
        if (target == viewer) return;
        if (viewer.getWorld() != target.getWorld()) return;
        if (viewer.canSee(target))
            getArmorStandManager(target).spawn(viewer);
    }

//    private double getDistance(Player player1, Player player2) {
//        Location loc1 = player1.getLocation();
//        Location loc2 = player2.getLocation();
//        return Math.sqrt(Math.pow(loc1.getX()-loc2.getX(), 2) + Math.pow(loc1.getZ()-loc2.getZ(), 2));
//    }
}
