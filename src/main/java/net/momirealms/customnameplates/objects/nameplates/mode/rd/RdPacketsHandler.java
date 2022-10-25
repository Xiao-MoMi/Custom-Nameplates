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

package net.momirealms.customnameplates.objects.nameplates.mode.rd;

import com.comphenix.protocol.events.PacketContainer;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.listener.packet.EntityDestroyListener;
import net.momirealms.customnameplates.listener.packet.EntityMountListener;
import net.momirealms.customnameplates.listener.packet.EntitySpawnListener;
import net.momirealms.customnameplates.manager.NameplateManager;
import net.momirealms.customnameplates.objects.nameplates.ArmorStandManager;
import net.momirealms.customnameplates.objects.nameplates.mode.PacketsHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class RdPacketsHandler extends PacketsHandler {

    private final RidingTag ridingTag;

    private final EntityDestroyListener entityDestroyListener;
    private final EntitySpawnListener entitySpawnListener;
    private final EntityMountListener entityMountListener;

    protected RdPacketsHandler(RidingTag ridingTag) {
        super(ridingTag);
        this.ridingTag = ridingTag;
        this.entityDestroyListener = new EntityDestroyListener(this);
        this.entitySpawnListener = new EntitySpawnListener(this);
        if (NameplateManager.tryHook) {
            this.entityMountListener = new EntityMountListener(this);
        } else entityMountListener = null;
    }

    @Override
    public void load() {
        super.load();
        CustomNameplates.protocolManager.addPacketListener(entityDestroyListener);
        CustomNameplates.protocolManager.addPacketListener(entitySpawnListener);
        if (entityMountListener != null) {
            CustomNameplates.protocolManager.addPacketListener(entityMountListener);
        }
    }

    @Override
    public void unload() {
        super.unload();
        CustomNameplates.protocolManager.removePacketListener(entityDestroyListener);
        CustomNameplates.protocolManager.removePacketListener(entitySpawnListener);
        if (entityMountListener != null) {
            CustomNameplates.protocolManager.removePacketListener(entityMountListener);
        }
    }

    @Override
    public void onEntitySpawn(Player receiver, int entityId) {
        Player spawnedPlayer = super.getPlayerFromMap(entityId);
        if (spawnedPlayer != null) {
            ArmorStandManager asm = ridingTag.getArmorStandManager(spawnedPlayer);
            asm.spawn(receiver);
            Bukkit.getScheduler().runTaskAsynchronously(CustomNameplates.plugin, ()-> asm.mount(receiver));
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
    public void onEntityMount(PacketContainer packet) {
        Player player = super.getPlayerFromMap(packet.getIntegers().read(0));
        if (player == null) return;
        int[] ids =  ridingTag.getArmorStandManager(player).getArmorStandIDs();
        if (ids != null) {
            int[] old = packet.getIntegerArrays().read(0);
            int[] idArray = new int[ids.length + old.length];
            int i = 0;
            while (i < ids.length) {
                idArray[i] = ids[i];
                i ++;
            }
            while (i < ids.length + old.length) {
                idArray[i] = old[i - ids.length];
                i ++;
            }
            packet.getModifier().write(1, idArray);
        }
    }
}
