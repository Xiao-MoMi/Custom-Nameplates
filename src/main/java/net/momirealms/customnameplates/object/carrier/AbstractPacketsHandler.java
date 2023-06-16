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

import net.momirealms.customnameplates.object.Function;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractPacketsHandler extends Function {

    protected ConcurrentHashMap<Integer, Player> entityIdMap = new ConcurrentHashMap<>();

    @Override
    public void load() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            entityIdMap.put(player.getEntityId(), player);
        }
    }

    @Override
    public void unload() {
        entityIdMap.clear();
    }

    @Override
    public void onJoin(Player player) {
        entityIdMap.put(player.getEntityId(), player);
    }

    @Override
    public void onQuit(Player player) {
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

    public Player getPlayerFromMap(int entityID) {
        return entityIdMap.get(entityID);
    }
}
