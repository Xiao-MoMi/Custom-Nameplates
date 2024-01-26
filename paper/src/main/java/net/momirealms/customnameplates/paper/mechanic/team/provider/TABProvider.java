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

package net.momirealms.customnameplates.paper.mechanic.team.provider;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.tablist.SortingManager;
import net.momirealms.customnameplates.api.util.LogUtils;
import org.bukkit.entity.Player;

public class TABProvider implements TeamProvider {

    private final TabAPI api;
    private final SortingManager sortingManager;

    public TABProvider() {
        this.api = TabAPI.getInstance();
        this.sortingManager = api.getSortingManager();
        if (sortingManager == null) {
            LogUtils.warn("Detected that team management is disabled in TAB. Using player name as team name.");
        }
    }

    @Override
    public String getTeam(Player player, Player ignore) {
        TabPlayer tabPlayer = api.getPlayer(player.getUniqueId());
        if (tabPlayer == null) {
            return null;
        }
        String forced = sortingManager.getForcedTeamName(tabPlayer);
        if (forced != null) {
            return forced;
        }
        return sortingManager.getOriginalTeamName(tabPlayer);
    }
}
