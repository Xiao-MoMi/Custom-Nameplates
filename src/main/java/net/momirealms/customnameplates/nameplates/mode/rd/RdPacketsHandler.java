package net.momirealms.customnameplates.nameplates.mode.rd;

import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.nameplates.mode.ArmorStandManager;
import net.momirealms.customnameplates.nameplates.mode.PacketsHandler;
import net.momirealms.customnameplates.nameplates.mode.listener.EntityDestroyListener;
import net.momirealms.customnameplates.nameplates.mode.listener.EntityMountListener;
import net.momirealms.customnameplates.nameplates.mode.listener.EntitySpawnListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class RdPacketsHandler extends PacketsHandler {

    private final RidingTag ridingTag;

    private EntityDestroyListener entityDestroyListener;
    private EntitySpawnListener entitySpawnListener;
    private EntityMountListener entityMountListener;

    protected RdPacketsHandler(String name, RidingTag ridingTag) {
        super(name, ridingTag);
        this.ridingTag = ridingTag;
    }

    @Override
    public void load() {
        super.load();
        this.entityDestroyListener = new EntityDestroyListener(this);
        this.entitySpawnListener = new EntitySpawnListener(this);
        CustomNameplates.protocolManager.addPacketListener(entityDestroyListener);
        CustomNameplates.protocolManager.addPacketListener(entitySpawnListener);
        if (ConfigManager.Nameplate.tryHook) {
            this.entityMountListener = new EntityMountListener(this);
            CustomNameplates.protocolManager.addPacketListener(entityMountListener);
        }
    }

    @Override
    public void unload() {
        super.unload();
        CustomNameplates.protocolManager.removePacketListener(entityDestroyListener);
        CustomNameplates.protocolManager.removePacketListener(entitySpawnListener);
        if (entityDestroyListener != null) {
            CustomNameplates.protocolManager.removePacketListener(entityMountListener);
        }
    }

    @Override
    public void onEntitySpawn(Player receiver, int entityId) {
        Player spawnedPlayer = super.getPlayerFromMap(entityId);
        if (spawnedPlayer != null) {
            ArmorStandManager asm = ridingTag.getArmorStandManager(spawnedPlayer);
            asm.spawn(receiver);
            Bukkit.getScheduler().runTaskLater(CustomNameplates.instance, ()-> {
                asm.mount(receiver);
            },1);
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
            ridingTag.getArmorStandManager(deSpawnedPlayer).destroy(receiver);
        }
    }

    @Override
    public void onEntityMount(int vehicle, int[] passengers) {
    }
}
