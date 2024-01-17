package net.momirealms.customnameplates.paper.mechanic.nameplate.tag.unlimited;

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.NameplateManager;
import net.momirealms.customnameplates.api.manager.UnlimitedTagManager;
import net.momirealms.customnameplates.api.mechanic.misc.ViewerText;
import net.momirealms.customnameplates.api.mechanic.tag.unlimited.NamedEntity;
import net.momirealms.customnameplates.api.mechanic.tag.unlimited.UnlimitedObject;
import net.momirealms.customnameplates.api.mechanic.tag.unlimited.UnlimitedPlayer;
import net.momirealms.customnameplates.api.mechanic.tag.unlimited.UnlimitedTagSetting;
import net.momirealms.customnameplates.api.scheduler.CancellableTask;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.paper.mechanic.nameplate.tag.listener.MagicCosmeticsListener;
import net.momirealms.customnameplates.paper.util.LocationUtils;
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
    private final ConcurrentHashMap<UUID, UnlimitedObject> unlimitedEntryMap;
    private CancellableTask refreshTask;
    private MagicCosmeticsListener magicCosmeticsListener;

    public UnlimitedTagManagerImpl(NameplateManager nameplateManager) {
        this.manager = nameplateManager;
        this.unlimitedEntryMap = new ConcurrentHashMap<>();
        if (Bukkit.getPluginManager().getPlugin("MagicCosmetics") != null) {
            this.magicCosmeticsListener = new MagicCosmeticsListener(this);
        }
    }
    
    public void load() {
        this.refreshTask = CustomNameplatesPlugin.get().getScheduler().runTaskAsyncTimer(
                () -> {
                    try {
                        for (UnlimitedObject unlimitedObject : unlimitedEntryMap.values()) {
                            if (unlimitedObject instanceof UnlimitedPlayer unlimitedPlayer) {
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
        if (this.refreshTask != null && !this.refreshTask.isCancelled()) {
            this.refreshTask.cancel();
        }
        for (UnlimitedObject entry : unlimitedEntryMap.values()) {
            entry.destroy();
        }
        if (this.magicCosmeticsListener != null) {
            HandlerList.unregisterAll(magicCosmeticsListener);
        }
    }

    @NotNull
    @Override
    public NamedEntity createNamedEntity(UnlimitedPlayer player, UnlimitedTagSetting setting) {
        return new NamedEntityImpl(
                player,
                new ViewerText(player.getOwner(), setting.getRawText()),
                setting.getRefreshFrequency(),
                setting.getCheckFrequency(),
                setting.getVerticalOffset(),
                setting.getOwnerRequirements(),
                setting.getViewerRequirements()
        );
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public UnlimitedPlayer createTagForPlayer(Player player, List<UnlimitedTagSetting> settings) {
        if (this.unlimitedEntryMap.containsKey(player.getUniqueId())) {
            return null;
        }

        var unlimitedPlayer = new UnlimitedPlayer(this, player);
        this.unlimitedEntryMap.put(
                player.getUniqueId(),
                unlimitedPlayer
        );

        for (UnlimitedTagSetting setting : settings) {
            unlimitedPlayer.addTag(
                    createNamedEntity(unlimitedPlayer, setting)
            );
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (       online == player
                    || !online.canSee(player)
                    || LocationUtils.getDistance(online, player) > 48
                    || online.getWorld() != player.getWorld()
                    || online.isDead()
            ) continue;
            unlimitedPlayer.addNearbyPlayer(online);
        }
        return unlimitedPlayer;
    }

    @Override
    public UnlimitedObject removeUnlimitedObjectFromMap(UUID uuid) {
        return unlimitedEntryMap.remove(uuid);
    }

    @Nullable
    @Override
    public UnlimitedObject getUnlimitedObject(UUID uuid) {
        return unlimitedEntryMap.get(uuid);
    }

    public void handleEntitySpawnPacket(Player receiver, int entityId) {
        Player spawned = manager.getPlayerByEntityID(entityId);
        if (spawned == null) return;
        UnlimitedObject unlimitedObject = getUnlimitedObject(spawned.getUniqueId());
        if (unlimitedObject == null) return;
        unlimitedObject.addNearbyPlayer(receiver);
    }

    public void handlePlayerPose(Player player, Pose pose) {
        UnlimitedObject unlimitedObject = getUnlimitedObject(player.getUniqueId());
        if (unlimitedObject != null) {
            unlimitedObject.handlePose(player.getPose(), pose);
        }
    }

    public void handlePlayerQuit(Player quit) {
        UnlimitedObject unlimitedObject = getUnlimitedObject(quit.getUniqueId());
        if (unlimitedObject != null) {
            unlimitedObject.destroy();
        }
        for (UnlimitedObject entry : unlimitedEntryMap.values()) {
            entry.removeNearbyPlayer(quit);
        }
    }

    public void handleEntityMovePacket(Player receiver, int entityID, short x, short y, short z, boolean onGround) {
        Entity mover = manager.getEntityByEntityID(entityID);
        if (mover == null) return;
        UnlimitedObject unlimitedObject = getUnlimitedObject(mover.getUniqueId());
        if (unlimitedObject == null) return;
        unlimitedObject.move(receiver, x, y, z, onGround);
    }

    public void handleEntityTeleportPacket(Player receiver, int entityID, double x, double y, double z, boolean onGround) {
        Entity tp = manager.getEntityByEntityID(entityID);
        if (tp == null) return;
        UnlimitedObject unlimitedObject = getUnlimitedObject(tp.getUniqueId());
        if (unlimitedObject == null) return;
        unlimitedObject.teleport(receiver, x, y, z, onGround);
    }

    public void handleEntityDestroyPacket(Player receiver, List<Integer> list) {
        for (int id : list) {
            handleSingleEntityDestroy(receiver, id);
        }
    }

    private void handleSingleEntityDestroy(Player receiver, int entityID) {
        Entity deSpawned = manager.getEntityByEntityID(entityID);
        if (deSpawned == null) return;
        UnlimitedObject unlimitedObject = getUnlimitedObject(deSpawned.getUniqueId());
        if (unlimitedObject == null) return;
        unlimitedObject.removeNearbyPlayer(receiver);
    }

    public void handlePlayerSneak(Player sneaker, boolean sneaking, boolean flying) {
        UnlimitedObject unlimitedObject = getUnlimitedObject(sneaker.getUniqueId());
        if (unlimitedObject == null) return;
        unlimitedObject.sneak(sneaking, flying);
    }
}
