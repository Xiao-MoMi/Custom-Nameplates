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
import net.momirealms.customnameplates.api.manager.NameplateManager;
import net.momirealms.customnameplates.api.manager.UnlimitedTagManager;
import net.momirealms.customnameplates.api.mechanic.misc.ViewerText;
import net.momirealms.customnameplates.api.mechanic.tag.unlimited.*;
import net.momirealms.customnameplates.api.scheduler.CancellableTask;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.paper.mechanic.nameplate.tag.listener.MagicCosmeticsListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class UnlimitedTagManagerImpl implements UnlimitedTagManager {

    private final NameplateManager manager;
    private final ConcurrentHashMap<UUID, UnlimitedEntity> unlimitedEntityMap;
    private CancellableTask refreshTask;
    private MagicCosmeticsListener magicCosmeticsListener;
    private final VehicleChecker vehicleChecker;

    public UnlimitedTagManagerImpl(NameplateManager nameplateManager) {
        this.manager = nameplateManager;
        this.unlimitedEntityMap = new ConcurrentHashMap<>();
        this.vehicleChecker = new VehicleChecker(this);
        if (Bukkit.getPluginManager().getPlugin("MagicCosmetics") != null) {
            this.magicCosmeticsListener = new MagicCosmeticsListener(this);
        }
    }
    
    public void load() {
        this.vehicleChecker.load();
        this.refreshTask = CustomNameplatesPlugin.get().getScheduler().runTaskAsyncTimer(
                () -> {
                    try {
                        for (UnlimitedEntity unlimitedEntity : unlimitedEntityMap.values()) {
                            if (unlimitedEntity instanceof UnlimitedPlayer unlimitedPlayer) {
                                unlimitedPlayer.timer();
                            }
                        }
                    } catch (Exception e) {
                        LogUtils.severe(
                                "Error occurred when updating unlimited tags. " +
                                "This might not be a bug in CustomNameplates. Please report " +
                                "to the Plugin on the top of the following " +
                                "stack trace."
                        );
                        e.printStackTrace();
                    }
                },
                50L,
                50L,
                TimeUnit.MILLISECONDS
        );
        if (this.magicCosmeticsListener != null) {
            Bukkit.getPluginManager().registerEvents(magicCosmeticsListener, CustomNameplatesPlugin.get());
        }
    }
    
    public void unload() {
        this.vehicleChecker.unload();
        if (this.refreshTask != null && !this.refreshTask.isCancelled()) {
            this.refreshTask.cancel();
        }
        for (UnlimitedEntity entry : unlimitedEntityMap.values()) {
            entry.destroy();
        }
        if (this.magicCosmeticsListener != null) {
            HandlerList.unregisterAll(magicCosmeticsListener);
        }
    }

    @NotNull
    @Override
    public StaticTextEntity createNamedEntity(EntityTagEntity entity, StaticTextTagSetting setting) {
        return new StaticTextEntityImpl(
                (UnlimitedEntity) entity,
                setting.getVerticalOffset(),
                setting.getComeRule(),
                setting.getLeaveRule(),
                setting.getDefaultText(),
                setting.getPlugin()
        );
    }

    @NotNull
    @Override
    public DynamicTextEntity createNamedEntity(EntityTagPlayer player, DynamicTextTagSetting setting) {
        return new DynamicTextEntityImpl(
                (UnlimitedPlayer) player,
                new ViewerText(player.getPlayer(), setting.getRawText()),
                setting.getRefreshFrequency(),
                setting.getCheckFrequency(),
                setting.getVerticalOffset(),
                setting.getOwnerRequirements(),
                setting.getViewerRequirements()
        );
    }

    @Override
    public UnlimitedEntity createOrGetTagForEntity(Entity entity) {
        final UUID uuid = entity.getUniqueId();
        if (this.unlimitedEntityMap.containsKey(uuid)) {
            return this.unlimitedEntityMap.get(uuid);
        }

        var unlimitedEntity = new UnlimitedEntity(this, entity);
        this.unlimitedEntityMap.put(
                uuid,
                unlimitedEntity
        );

        unlimitedEntity.addNearByPlayerToMap(48);
        return unlimitedEntity;
    }

    @Override
    public UnlimitedPlayer createOrGetTagForPlayer(Player player) {
        if (!player.isOnline())
            return null;
        final UUID uuid = player.getUniqueId();
        if (this.unlimitedEntityMap.containsKey(uuid)) {
            return (UnlimitedPlayer) this.unlimitedEntityMap.get(uuid);
        }

        var unlimitedPlayer = new UnlimitedPlayer(this, player);
        this.unlimitedEntityMap.put(
                uuid,
                unlimitedPlayer
        );

        unlimitedPlayer.addNearByPlayerToMap(48);
        return unlimitedPlayer;
    }

    public UnlimitedEntity removeUnlimitedEntityFromMap(UUID uuid) {
        return unlimitedEntityMap.remove(uuid);
    }

    @Nullable
    public UnlimitedEntity getUnlimitedObject(UUID uuid) {
        return unlimitedEntityMap.get(uuid);
    }

    public void handleEntitySpawnPacket(Player receiver, int entityId) {
        Player spawned = manager.getPlayerByEntityID(entityId);
        if (spawned == null) return;
        UnlimitedEntity unlimitedEntity = getUnlimitedObject(spawned.getUniqueId());
        if (unlimitedEntity == null) return;
        unlimitedEntity.addNearbyPlayerNaturally(receiver);
    }

    public void handlePlayerPose(Player player, Pose pose) {
        UnlimitedEntity unlimitedEntity = getUnlimitedObject(player.getUniqueId());
        if (unlimitedEntity != null) {
            unlimitedEntity.handlePose(player.getPose(), pose);
        }
    }

    public void handlePlayerQuit(Player quit) {
        UnlimitedEntity unlimitedEntity = getUnlimitedObject(quit.getUniqueId());
        if (unlimitedEntity != null) {
            unlimitedEntity.destroy();
        }
        for (UnlimitedEntity entry : unlimitedEntityMap.values()) {
            entry.removeNearbyPlayerNaturally(quit);
        }
    }

    public void handleEntityMovePacket(Player receiver, int entityID, short x, short y, short z, boolean onGround) {
        Entity mover = manager.getEntityByEntityID(entityID);
        if (mover == null) return;
        UnlimitedEntity unlimitedEntity = getUnlimitedObject(mover.getUniqueId());
        if (unlimitedEntity == null) return;
        unlimitedEntity.move(receiver, x, y, z, onGround);
    }

    public void handleEntityTeleportPacket(Player receiver, int entityID, double x, double y, double z, boolean onGround) {
        Entity tp = manager.getEntityByEntityID(entityID);
        if (tp == null) return;
        UnlimitedEntity unlimitedEntity = getUnlimitedObject(tp.getUniqueId());
        if (unlimitedEntity == null) return;
        unlimitedEntity.teleport(receiver, x, y, z, onGround);
    }

    public void handleEntityDestroyPacket(Player receiver, List<Integer> list) {
        for (int id : list) {
            handleSingleEntityDestroy(receiver, id);
        }
    }

    private void handleSingleEntityDestroy(Player receiver, int entityID) {
        Entity deSpawned = manager.getEntityByEntityID(entityID);
        if (deSpawned == null) return;
        UnlimitedEntity unlimitedEntity = getUnlimitedObject(deSpawned.getUniqueId());
        if (unlimitedEntity == null) return;
        unlimitedEntity.removeNearbyPlayerNaturally(receiver);
    }

    public void handlePlayerSneak(Player sneaker, boolean sneaking, boolean flying) {
        UnlimitedEntity unlimitedEntity = getUnlimitedObject(sneaker.getUniqueId());
        if (!(unlimitedEntity instanceof UnlimitedPlayer unlimitedPlayer)) return;
        unlimitedPlayer.sneak(sneaking, flying);
    }
}
