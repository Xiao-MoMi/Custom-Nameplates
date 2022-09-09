package net.momirealms.customnameplates.nameplates.mode.tp;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.nameplates.mode.PacketsHandler;
import net.momirealms.customnameplates.nameplates.mode.listener.*;
import org.bukkit.entity.Player;

import java.util.List;

public class TpPacketsHandler extends PacketsHandler {

    private final TeleportingTag teleportingTag;

    private EntityDestroyListener entityDestroyListener;
    private EntityMoveListener entityMoveListener;
    private EntitySpawnListener entitySpawnListener;
    private EntityTeleportListener entityTeleportListener;
    private EntityLookListener entityLookListener;

    protected TpPacketsHandler(String name, TeleportingTag teleportingTag) {
        super(name, teleportingTag);
        this.teleportingTag = teleportingTag;
    }

    @Override
    public void load() {
        super.load();
        this.entityDestroyListener = new EntityDestroyListener(this);
        this.entityMoveListener = new EntityMoveListener(this);
        this.entitySpawnListener = new EntitySpawnListener(this);
        this.entityTeleportListener = new EntityTeleportListener(this);
        this.entityLookListener = new EntityLookListener(this);
        CustomNameplates.protocolManager.addPacketListener(entityDestroyListener);
        CustomNameplates.protocolManager.addPacketListener(entityMoveListener);
        CustomNameplates.protocolManager.addPacketListener(entitySpawnListener);
        CustomNameplates.protocolManager.addPacketListener(entityTeleportListener);
        CustomNameplates.protocolManager.addPacketListener(entityLookListener);
    }

    @Override
    public void unload() {
        super.unload();
        CustomNameplates.protocolManager.removePacketListener(entityDestroyListener);
        CustomNameplates.protocolManager.removePacketListener(entityMoveListener);
        CustomNameplates.protocolManager.removePacketListener(entitySpawnListener);
        CustomNameplates.protocolManager.removePacketListener(entityTeleportListener);
        CustomNameplates.protocolManager.removePacketListener(entityLookListener);
    }

    @Override
    public void onEntityMove(Player receiver, int entityId) {
        Player mover = getPlayerFromMap(entityId);
        if (mover != null) {
            teleportingTag.getArmorStandManager(mover).teleport(receiver);
        }
    }

    @Override
    public void onPlayerMove(Player mover) {
        teleportingTag.getArmorStandManager(mover).teleport();
    }

    @Override
    public void onEntitySpawn(Player receiver, int entityId) {
        Player spawnedPlayer = super.getPlayerFromMap(entityId);
        if (spawnedPlayer != null) {
            teleportingTag.getArmorStandManager(spawnedPlayer).spawn(receiver);
        }
    }

    @Override
    public void onEntityDestroy(Player receiver, List<Integer> entities) {
        for (int entity : entities) {
            onEntityDestroy(receiver, entity);
        }
    }

    @Override
    public void onEntityDestroy(Player receiver, int entity) {
        Player deSpawnedPlayer = super.getPlayerFromMap(entity);
        if (deSpawnedPlayer != null) {
            teleportingTag.getArmorStandManager(deSpawnedPlayer).destroy(receiver);
        }
    }
}
