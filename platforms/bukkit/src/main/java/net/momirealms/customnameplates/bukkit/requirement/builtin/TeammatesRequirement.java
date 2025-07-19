/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customnameplates.bukkit.requirement.builtin;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.backend.requirement.AbstractRequirement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class TeammatesRequirement extends AbstractRequirement {
    private final boolean is;

    public TeammatesRequirement(int refreshInterval, boolean is) {
        super(refreshInterval);
        this.is = is;
    }

    @Override
    public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
        Player player1 = (Player) p1.player();
        Player player2 = (Player) p2.player();
        Team team1 = Bukkit.getScoreboardManager().getMainScoreboard().getEntityTeam(player1);
        Team team2 = Bukkit.getScoreboardManager().getMainScoreboard().getEntityTeam(player2);
        if (this.is) {
            return team1 != null && team1.equals(team2);
        } else {
            return team1 == null || !team1.equals(team2);
        }
    }

    @Override
    public String type() {
        return "teammates";
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeammatesRequirement that = (TeammatesRequirement) o;
        return that.is == this.is;
    }

    @Override
    public int hashCode() {
        return this.is ? 17 : 3;
    }
}
