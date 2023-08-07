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
import net.momirealms.customnameplates.listener.EntityTagListener;
import net.momirealms.customnameplates.listener.compatibility.MagicCosmeticsListener;
import net.momirealms.customnameplates.object.ConditionalText;
import net.momirealms.customnameplates.object.DisplayMode;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class NamedEntityCarrier extends AbstractTextCarrier {

    protected final ConcurrentHashMap<Player, NamedEntityManager> namedEntityManagerMap = new ConcurrentHashMap<>();
    protected EntityTagListener entityTagListener;
    protected AbstractPacketsHandler handler;
    protected MagicCosmeticsListener magicCosmeticsListener;
    private final VehicleChecker vehicleChecker;
    private final HashMap<ConditionalText, Double> persistentText;

    public NamedEntityCarrier(CustomNameplates plugin, DisplayMode displayMode, @NotNull HashMap<ConditionalText, Double> persistentText) {
        super(plugin, displayMode);
        this.vehicleChecker = new VehicleChecker(this);
        this.entityTagListener = new EntityTagListener(this);
        this.persistentText = persistentText;
        this.handler = new NamedEntityPacketsHandler(this);
        if (Bukkit.getPluginManager().getPlugin("MagicCosmetics") != null) {
            this.magicCosmeticsListener = new MagicCosmeticsListener(this);
        }
    }

    @Override
    public void load(){
        super.load();
        handler.load();
        this.vehicleChecker.load();
        Bukkit.getPluginManager().registerEvents(entityTagListener, CustomNameplates.getInstance());
        if (magicCosmeticsListener != null) Bukkit.getPluginManager().registerEvents(magicCosmeticsListener, CustomNameplates.getInstance());
    }

    @Override
    public void unload(){
        super.unload();
        handler.unload();
        for (NamedEntityManager asm : namedEntityManagerMap.values()) {
            asm.destroy();
        }
        namedEntityManagerMap.clear();
        HandlerList.unregisterAll(entityTagListener);
        this.vehicleChecker.unload();
        if (magicCosmeticsListener != null) HandlerList.unregisterAll(magicCosmeticsListener);
    }

    @Override
    public void onJoin(Player player) {
        handler.onJoin(player);
        createNamedEntityManager(player);
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            spawnNamedEntity(viewer, player);
            spawnNamedEntity(player, viewer);
        }
    }

    @Override
    public void onQuit(Player player) {
        handler.onQuit(player);
        NamedEntityManager asm = namedEntityManagerMap.remove(player);
        if (asm != null) {
            asm.destroy();
        }
    }

    @Override
    public void loadToAllPlayers() {
        super.loadToAllPlayers();
        for (Player all : Bukkit.getOnlinePlayers()) {
            createNamedEntityManager(all);
            for (Player player : Bukkit.getOnlinePlayers())
                spawnNamedEntity(player, all);
        }
    }

    public NamedEntityManager createNamedEntityManager(Player player) {
        NamedEntityManager asm = new NamedEntityManager(this, player);
        namedEntityManagerMap.put(player, asm);
        return asm;
    }

    public void onSneak(Player player, boolean isSneaking) {
        NamedEntityManager nem = getNamedEntityManager(player);
        if (nem != null) {
            nem.setSneak(isSneaking, true);
        }
    }

    public void onRespawn(Player player) {
        getNamedEntityManager(player).teleport();
    }

    @Override
    public void arrangeRefreshTask() {
        refreshTask = plugin.getScheduler().runTaskAsyncTimer(() -> {
            for (NamedEntityManager asm : namedEntityManagerMap.values()) {
                asm.refresh(false);
            }
        }, 1, 1);
    }

    public NamedEntityManager getNamedEntityManager(Player player) {
        return namedEntityManagerMap.get(player);
    }

    protected void spawnNamedEntity(Player viewer, Player target) {
        if (target == viewer || target.getGameMode() == GameMode.SPECTATOR || viewer.getWorld() != target.getWorld()) return;
        if (getDistance(target, viewer) < 48 && viewer.canSee(target)) {
            NamedEntityManager asm = getNamedEntityManager(target);
            asm.spawn(viewer);
        }
    }

    protected double getDistance(Player player1, Player player2) {
        Location loc1 = player1.getLocation();
        Location loc2 = player2.getLocation();
        return Math.sqrt(Math.pow(loc1.getX()-loc2.getX(), 2) + Math.pow(loc1.getZ()-loc2.getZ(), 2));
    }

    public HashMap<ConditionalText, Double> getPersistentText() {
        return persistentText;
    }
}
