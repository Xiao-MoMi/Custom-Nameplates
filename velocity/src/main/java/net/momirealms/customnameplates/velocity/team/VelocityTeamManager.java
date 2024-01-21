package net.momirealms.customnameplates.velocity.team;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.momirealms.customnameplates.common.team.TeamColor;
import net.momirealms.customnameplates.common.team.TeamTagVisibility;
import org.jetbrains.annotations.Nullable;

public interface VelocityTeamManager {

    @Nullable
    String getTeamName(Player player);

    void sendTeamUpdatePacket(Player receiver, String team, TeamColor color, TeamTagVisibility visibility, Component prefix, Component suffix);
}
