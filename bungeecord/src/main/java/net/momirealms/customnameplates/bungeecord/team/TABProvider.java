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

package net.momirealms.customnameplates.bungeecord.team;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.tablist.SortingManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TABProvider implements TeamProvider {

    private final TabAPI api;
    private final SortingManager sortingManager;

    public TABProvider() {
        this.api = TabAPI.getInstance();
        this.sortingManager = api.getSortingManager();
    }

    @Override
    public String getTeam(ProxiedPlayer player, ProxiedPlayer ignore) {
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
