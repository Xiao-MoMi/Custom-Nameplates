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

package net.momirealms.customnameplates.objects.nameplates.mode;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.listener.EntityTagListener;
import net.momirealms.customnameplates.manager.NameplateManager;
import net.momirealms.customnameplates.manager.TeamManager;
import net.momirealms.customnameplates.objects.nameplates.ArmorStandManager;
import net.momirealms.customnameplates.objects.nameplates.NameplateMode;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.concurrent.ConcurrentHashMap;

public abstract class EntityTag extends NameplateMode {

    protected final ConcurrentHashMap<Player, ArmorStandManager> armorStandManagerMap = new ConcurrentHashMap<>();

    protected EntityTagListener listener;

    protected EntityTag(TeamManager teamManager) {
        super(teamManager);
        this.listener = new EntityTagListener(this);
    }

    @Override
    public void load(){
        super.load();
        Bukkit.getPluginManager().registerEvents(listener, CustomNameplates.plugin);
    }

    @Override
    public void unload(){
        super.unload();
        for (ArmorStandManager asm : armorStandManagerMap.values()) {
            asm.destroy();
        }
        armorStandManagerMap.clear();
        HandlerList.unregisterAll(listener);
    }

    @Override
    public void onJoin(Player player) {
        super.onJoin(player);
        ArmorStandManager asm = new ArmorStandManager(player);
        addDefaultText(asm);
        armorStandManagerMap.put(player, asm);
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            spawnArmorStands(viewer, player, false);
            spawnArmorStands(player, viewer, false);
        }
    }

    @Override
    public void onQuit(Player player) {
        super.onQuit(player);
        ArmorStandManager asm = armorStandManagerMap.remove(player);
        if (asm != null) {
            asm.destroy();
        }
    }

    public void onSneak(Player player, boolean isSneaking) {
        //child
    }

    public void onRespawn(Player player) {
        //child
    }

    public void addDefaultText(ArmorStandManager asm) {
        asm.addDefault();
    }

    @Override
    public void arrangeRefreshTask() {
        if (NameplateManager.update) {
            refreshTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CustomNameplates.plugin, () -> {
                for (ArmorStandManager asm : armorStandManagerMap.values()) {
                    asm.refresh(false);
                }
            }, 20, NameplateManager.refresh);
        }
        else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(CustomNameplates.plugin, () -> {
                for (ArmorStandManager asm : armorStandManagerMap.values()) {
                    asm.refresh(false);
                }
            }, 20);
        }
    }

    public ArmorStandManager getArmorStandManager(Player player) {
        return armorStandManagerMap.get(player);
    }

    public void refresh(Player refreshed, boolean force) {
        getArmorStandManager(refreshed).refresh(force);
    }

    protected void spawnArmorStands(Player viewer, Player target, boolean ride) {
        if (target == viewer) return;
        if (target.getGameMode() == GameMode.SPECTATOR) return;
        if (viewer.getWorld() != target.getWorld()) return;
        if (getDistance(target, viewer) < 48 && viewer.canSee(target)) {
            ArmorStandManager asm = getArmorStandManager(target);
            asm.spawn(viewer);
            if (ride) {
                //just for a delay
                Bukkit.getScheduler().runTaskAsynchronously(CustomNameplates.plugin, () -> {
                    asm.mount(viewer);
                });
            }
        }
    }

    protected double getDistance(Player player1, Player player2) {
        Location loc1 = player1.getLocation();
        Location loc2 = player2.getLocation();
        return Math.sqrt(Math.pow(loc1.getX()-loc2.getX(), 2) + Math.pow(loc1.getZ()-loc2.getZ(), 2));
    }
}
