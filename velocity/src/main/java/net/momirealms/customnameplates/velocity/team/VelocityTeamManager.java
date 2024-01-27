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

package net.momirealms.customnameplates.velocity.team;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.momirealms.customnameplates.common.team.TeamColor;
import net.momirealms.customnameplates.common.team.TeamTagVisibility;
import org.jetbrains.annotations.Nullable;

public interface VelocityTeamManager {

    @Nullable
    String getTeam(Player player, Player viewer);

    void sendTeamUpdatePacket(Player receiver, String team, TeamColor color, TeamTagVisibility visibility, Component prefix, Component suffix);
}
