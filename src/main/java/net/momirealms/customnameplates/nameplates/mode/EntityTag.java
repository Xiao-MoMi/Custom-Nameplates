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

import net.momirealms.customnameplates.nameplates.ArmorStandManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentHashMap;

public abstract class EntityTag extends NameplateManager {

    protected final ConcurrentHashMap<Player, ArmorStandManager> armorStandManagerMap = new ConcurrentHashMap<>();

    protected EntityTag(String name) {
        super(name);
    }

    @Override
    public void load(){
    }

    @Override
    public void unload(){
        for (Player all : Bukkit.getOnlinePlayers()) {
            getArmorStandManager(all).destroy();
        }
        armorStandManagerMap.clear();
    }

    @Override
    public void onJoin(Player player) {
        super.onJoin(player);
    }

    @Override
    public void onQuit(Player player) {
        super.onQuit(player);
    }

    public void onSneak(Player player, boolean isSneaking) {
    }

    public void onRespawn(Player player) {
    }

    public ArmorStandManager getArmorStandManager(Player player) {
        return armorStandManagerMap.get(player);
    }

    public void refresh(Player refreshed, boolean force) {
        getArmorStandManager(refreshed).refresh(force);
    }
}
