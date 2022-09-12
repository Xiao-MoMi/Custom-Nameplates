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

package net.momirealms.customnameplates.nameplates.mode.bubbles;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.nameplates.listener.*;
import net.momirealms.customnameplates.nameplates.mode.PacketsHandler;
import org.bukkit.entity.Player;

import java.util.List;

public class BBPacketsHandle extends PacketsHandler {

    private final ChatBubblesManager chatBubblesManager;

    private EntityDestroyListener entityDestroyListener;
    private EntityMoveListener entityMoveListener;
    private EntitySpawnListener entitySpawnListener;
    private EntityTeleportListener entityTeleportListener;
    private EntityLookListener entityLookListener;

    protected BBPacketsHandle(String name, ChatBubblesManager chatBubblesManager) {
        super(name, chatBubblesManager);
        this.chatBubblesManager = chatBubblesManager;
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
            chatBubblesManager.getArmorStandManager(mover).teleport(receiver);
        }
    }

    @Override
    public void onEntitySpawn(Player receiver, int entityId) {
        Player spawnedPlayer = getPlayerFromMap(entityId);
        if (spawnedPlayer != null) {
            chatBubblesManager.getArmorStandManager(spawnedPlayer).spawn(receiver);
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
        Player deSpawnedPlayer = getPlayerFromMap(entity);
        if (deSpawnedPlayer != null) {
            chatBubblesManager.getArmorStandManager(deSpawnedPlayer).destroy(receiver);
        }
    }

    @Override
    public Player getPlayerFromMap(int entityID) {
        return entityIdMap.get(entityID);
    }
}
