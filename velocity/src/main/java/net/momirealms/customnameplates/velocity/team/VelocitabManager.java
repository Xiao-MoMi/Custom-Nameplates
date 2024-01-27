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

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import net.kyori.adventure.text.Component;
import net.momirealms.customnameplates.common.team.TeamColor;
import net.momirealms.customnameplates.common.team.TeamTagVisibility;
import net.william278.velocitab.Velocitab;
import net.william278.velocitab.api.VelocitabAPI;
import net.william278.velocitab.packet.UpdateTeamsPacket;
import net.william278.velocitab.player.TabPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class VelocitabManager implements VelocityTeamManager {

    private final Velocitab velocitab;
    private final VelocitabAPI velocitabAPI;

    public VelocitabManager(PluginContainer pluginContainer) {
        velocitab = (Velocitab) pluginContainer.getInstance().get();
        velocitabAPI = VelocitabAPI.getInstance();
    }

    @Override
    @Nullable
    public String getTeam(Player player, Player viewer) {
        Optional<TabPlayer> playerOptional = velocitabAPI.getUser(player);
        return playerOptional.map(TabPlayer::getTeamName).orElse(null);
    }

    @Override
    public void sendTeamUpdatePacket(Player receiver, String team, TeamColor color, TeamTagVisibility visibility, Component prefix, Component suffix) {
        UpdateTeamsPacket packet = new UpdateTeamsPacket(velocitab)
                .teamName(team.length() > 16 ? team.substring(0, 16) : team)
                .mode(UpdateTeamsPacket.UpdateMode.UPDATE_INFO)
                .displayName(Component.empty())
                .friendlyFlags(List.of(UpdateTeamsPacket.FriendlyFlag.CAN_HURT_FRIENDLY))
                .nametagVisibility(UpdateTeamsPacket.NametagVisibility.valueOf(visibility.name()))
                .collisionRule(UpdateTeamsPacket.CollisionRule.ALWAYS)
                .color(UpdateTeamsPacket.TeamColor.valueOf(color.name()).ordinal())
                .prefix(prefix)
                .suffix(suffix);

        ConnectedPlayer connectedPlayer = (ConnectedPlayer) receiver;
        connectedPlayer.getConnection().write(packet);
    }
}
