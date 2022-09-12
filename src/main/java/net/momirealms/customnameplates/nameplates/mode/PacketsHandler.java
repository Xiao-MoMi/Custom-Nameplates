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

package net.momirealms.customnameplates.nameplates.mode;

import com.comphenix.protocol.events.PacketContainer;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.Function;
import net.momirealms.customnameplates.nameplates.listener.BukkitListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PacketsHandler extends Function {

    protected Map<Integer, Player> entityIdMap = new ConcurrentHashMap<>();
    private BukkitListener bukkitListener;
    private final EntityTag entityTag;

    protected PacketsHandler(String name, EntityTag entityTag) {
        super(name);
        this.entityTag = entityTag;
    }

    @Override
    public void load() {
        this.bukkitListener = new BukkitListener(this);
        Bukkit.getPluginManager().registerEvents(bukkitListener, CustomNameplates.instance);
        for (Player player : Bukkit.getOnlinePlayers()) {
            entityIdMap.put(player.getEntityId(), player);
        }
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(bukkitListener);
        entityIdMap.clear();
    }

    public void onJoin(Player player) {
        entityIdMap.put(player.getEntityId(), player);
        entityTag.onJoin(player);
    }

    public void onQuit(Player player) {
        entityTag.onQuit(player);
        entityIdMap.remove(player.getEntityId());
    }

    public void onEntityMove(Player receiver, int entityId) {
    }

    public void onEntitySpawn(Player receiver, int entityId) {
    }

    public void onEntityDestroy(Player receiver, List<Integer> entities) {
    }

    public void onEntityDestroy(Player receiver, int entity) {
    }

    public void onEntityMount(PacketContainer packet) {
    }

    public Player getPlayerFromMap(int entityID) {
        return entityIdMap.get(entityID);
    }
}
