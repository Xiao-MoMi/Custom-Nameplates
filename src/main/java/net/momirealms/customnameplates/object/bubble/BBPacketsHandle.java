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

package net.momirealms.customnameplates.object.bubble;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.listener.packet.*;
import net.momirealms.customnameplates.manager.ChatBubblesManager;
import net.momirealms.customnameplates.object.nameplate.mode.PacketsHandler;
import org.bukkit.entity.Player;

import java.util.List;

public class BBPacketsHandle extends PacketsHandler {

    private final ChatBubblesManager chatBubblesManager;
    private final EntityDestroyListener entityDestroyListener;
    private final EntityMoveListener entityMoveListener;
    private final EntitySpawnListener entitySpawnListener;
    private final EntityTeleportListener entityTeleportListener;
    private final EntityLookListener entityLookListener;

    public BBPacketsHandle(ChatBubblesManager chatBubblesManager) {
        super();
        this.chatBubblesManager = chatBubblesManager;
        this.entityDestroyListener = new EntityDestroyListener(this);
        this.entityMoveListener = new EntityMoveListener(this);
        this.entitySpawnListener = new EntitySpawnListener(this);
        this.entityTeleportListener = new EntityTeleportListener(this);
        this.entityLookListener = new EntityLookListener(this);
    }

    @Override
    public void load() {
        super.load();
        CustomNameplates.getProtocolManager().addPacketListener(entityDestroyListener);
        CustomNameplates.getProtocolManager().addPacketListener(entityMoveListener);
        CustomNameplates.getProtocolManager().addPacketListener(entitySpawnListener);
        CustomNameplates.getProtocolManager().addPacketListener(entityTeleportListener);
        CustomNameplates.getProtocolManager().addPacketListener(entityLookListener);
    }

    @Override
    public void unload() {
        super.unload();
        CustomNameplates.getProtocolManager().removePacketListener(entityDestroyListener);
        CustomNameplates.getProtocolManager().removePacketListener(entityMoveListener);
        CustomNameplates.getProtocolManager().removePacketListener(entitySpawnListener);
        CustomNameplates.getProtocolManager().removePacketListener(entityTeleportListener);
        CustomNameplates.getProtocolManager().removePacketListener(entityLookListener);
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
}
