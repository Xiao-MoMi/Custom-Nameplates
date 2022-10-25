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

import net.momirealms.customnameplates.manager.TeamManager;
import net.momirealms.customnameplates.objects.nameplates.ArmorStandManager;
import net.momirealms.customnameplates.objects.nameplates.mode.EntityTag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RidingTag extends EntityTag {

    private final RdPacketsHandler handler;

    public RidingTag(TeamManager teamManager) {
        super(teamManager);
        this.handler = new RdPacketsHandler(this);
    }

    @Override
    public void load() {
        super.load();
        this.handler.load();
    }

    @Override
    public void unload() {
        super.unload();
        this.handler.unload();
    }

    @Override
    public void loadToAllPlayers() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            ArmorStandManager asm = new ArmorStandManager(all);
            asm.addDefault();
            armorStandManagerMap.put(all, asm);
            teamManager.sendUpdateToOne(all);
            teamManager.sendUpdateToAll(all, true);
            for (Player player : Bukkit.getOnlinePlayers())
                spawnArmorStands(player, all, true);
        }
    }

    @Override
    public void onSneak(Player player, boolean isSneaking) {
        getArmorStandManager(player).setSneak(isSneaking, false);
    }
}
