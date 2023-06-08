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

package net.momirealms.customnameplates.object.nameplate.mode;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.listener.EntityTagListener;
import net.momirealms.customnameplates.listener.compatibility.MagicCosmeticsListener;
import net.momirealms.customnameplates.object.armorstand.ArmorStandManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.concurrent.ConcurrentHashMap;

public abstract class EntityTag extends AbstractNameplateTag {

    protected final ConcurrentHashMap<Player, ArmorStandManager> armorStandManagerMap = new ConcurrentHashMap<>();

    protected EntityTagListener entityTagListener;
    protected PacketsHandler handler;
    protected MagicCosmeticsListener magicCosmeticsListener;

    protected EntityTag(CustomNameplates plugin) {
        super(plugin);
        this.entityTagListener = new EntityTagListener(this);
        if (Bukkit.getPluginManager().getPlugin("MagicCosmetics") != null) {
            this.magicCosmeticsListener = new MagicCosmeticsListener(this);
        }
    }

    @Override
    public void load(){
        super.load();
        handler.load();
        Bukkit.getPluginManager().registerEvents(entityTagListener, CustomNameplates.getInstance());
        if (magicCosmeticsListener != null) Bukkit.getPluginManager().registerEvents(magicCosmeticsListener, CustomNameplates.getInstance());
    }

    @Override
    public void unload(){
        super.unload();
        handler.unload();
        for (ArmorStandManager asm : armorStandManagerMap.values()) {
            asm.destroy();
        }
        armorStandManagerMap.clear();
        HandlerList.unregisterAll(entityTagListener);
        if (magicCosmeticsListener != null) HandlerList.unregisterAll(magicCosmeticsListener);
    }

    @Override
    public void onJoin(Player player) {
        handler.onJoin(player);
        init(player);
    }

    @Override
    public void onQuit(Player player) {
        handler.onQuit(player);
        ArmorStandManager asm = armorStandManagerMap.remove(player);
        if (asm != null) {
            asm.destroy();
        }
    }

    public void init(Player player) {

    }

    public ArmorStandManager createArmorStandManager(Player player) {
        ArmorStandManager asm = new ArmorStandManager(player);
        armorStandManagerMap.put(player, asm);
        return asm;
    }

    public void onSneak(Player player, boolean isSneaking) {
        //child
    }

    public void onRespawn(Player player) {
        //child
    }

    @Override
    public void arrangeRefreshTask() {

    }

    public ArmorStandManager getArmorStandManager(Player player) {
        return armorStandManagerMap.get(player);
    }

    protected void spawnArmorStands(Player viewer, Player target) {
        if (target == viewer || target.getGameMode() == GameMode.SPECTATOR || viewer.getWorld() != target.getWorld()) return;
        if (getDistance(target, viewer) < 48 && viewer.canSee(target)) {
            ArmorStandManager asm = getArmorStandManager(target);
            asm.spawn(viewer);
        }
    }

    protected double getDistance(Player player1, Player player2) {
        Location loc1 = player1.getLocation();
        Location loc2 = player2.getLocation();
        return Math.sqrt(Math.pow(loc1.getX()-loc2.getX(), 2) + Math.pow(loc1.getZ()-loc2.getZ(), 2));
    }
}
