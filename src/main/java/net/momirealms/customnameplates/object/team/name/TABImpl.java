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

package net.momirealms.customnameplates.object.team.name;

import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.tablist.SortingManager;
import me.neznamy.tab.shared.TAB;
import net.momirealms.customnameplates.object.team.TeamNameInterface;
import org.bukkit.entity.Player;

public class TABImpl implements TeamNameInterface {

    private final SortingManager sortingManager;

    public TABImpl() {
        sortingManager = TAB.getInstance().getSortingManager();
    }

    @Override
    public String getTeamName(Player player) {
        TabPlayer tabPlayer = TAB.getInstance().getPlayer(player.getUniqueId());
        if (tabPlayer == null) return player.getName();
        return sortingManager.getOriginalTeamName(tabPlayer);
    }

    @Override
    public void onJoin(Player player) {

    }

    @Override
    public void onQuit(Player player) {

    }

    @Override
    public void unload() {

    }

    @Override
    public void load() {

    }
}