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

package net.momirealms.customnameplates.object.nameplate.mode.team;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.manager.TeamManager;
import net.momirealms.customnameplates.object.nameplate.mode.AbstractNameplateTag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TeamTag extends AbstractNameplateTag {

    private final TeamManager teamManager;

    public TeamTag(CustomNameplates plugin) {
        super(plugin);
        this.teamManager = plugin.getTeamManager();
    }

    @Override
    public void arrangeRefreshTask() {
        refreshTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CustomNameplates.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                teamManager.sendUpdateToAll(player, false);
            }
        }, 1, 1);
    }
}