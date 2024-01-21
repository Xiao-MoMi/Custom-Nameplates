package net.momirealms.customnameplates.velocity;

import com.velocitypowered.api.proxy.Player;
import net.momirealms.customnameplates.common.team.TeamColor;
import net.momirealms.customnameplates.common.team.TeamTagVisibility;
import org.jetbrains.annotations.Nullable;

public interface VelocityTeamManager {

    @Nullable
    String getTeamName(Player player);

    void sendTeamUpdatePacket(Player receiver, String team, TeamColor color, TeamTagVisibility visibility, String prefix, String suffix);
}
