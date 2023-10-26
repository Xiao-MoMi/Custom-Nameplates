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

import com.comphenix.protocol.events.PacketEvent;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.listener.packet.*;
import org.bukkit.entity.Player;

import java.util.List;

public class NamedEntityPacketsHandler extends AbstractPacketsHandler {

    private final NamedEntityCarrier namedEntityCarrier;
    private final EntityDestroyListener entityDestroyListener;
    private final EntityMoveListener entityMoveListener;
    private final EntitySpawnListener entitySpawnListener;
    private final EntityTeleportListener entityTeleportListener;
    private final EntityLookListener entityLookListener;

    public NamedEntityPacketsHandler(NamedEntityCarrier namedEntityCarrier) {
        super();
        this.namedEntityCarrier = namedEntityCarrier;
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
    public void onEntityMove(Player receiver, int entityId, short x, short y, short z, boolean onGround) {
        Player mover = super.getPlayerFromMap(entityId);
        if (mover != null) {
            namedEntityCarrier.getNamedEntityManager(mover).move(receiver, x, y, z, onGround);
        }
    }

    @Override
    public void onEntityTeleport(Player receiver, int entityId) {
        Player teleporter = super.getPlayerFromMap(entityId);
        if (teleporter != null) {
            namedEntityCarrier.getNamedEntityManager(teleporter).teleport(receiver);
        }
    }

    @Override
    public void onEntitySpawn(Player receiver, int entityId, PacketEvent event) {
        Player spawnedPlayer = super.getPlayerFromMap(entityId);
        if (spawnedPlayer != null) {
            NamedEntityManager nem = namedEntityCarrier.getNamedEntityManager(spawnedPlayer);
            if (nem != null) {
                nem.spawn(receiver);
            }
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
            namedEntityCarrier.getNamedEntityManager(deSpawnedPlayer).destroy(receiver);
        }
    }
}
