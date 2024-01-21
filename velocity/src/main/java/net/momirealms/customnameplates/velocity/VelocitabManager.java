package net.momirealms.customnameplates.velocity;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
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
    public String getTeamName(Player player) {
        Optional<TabPlayer> playerOptional = velocitabAPI.getUser(player);
        return playerOptional.map(TabPlayer::getTeamName).orElse(null);
    }

    @Override
    public void sendTeamUpdatePacket(Player receiver, String team, TeamColor color, TeamTagVisibility visibility, String prefix, String suffix) {
        UpdateTeamsPacket packet = new UpdateTeamsPacket(velocitab)
                .teamName(team.length() > 16 ? team.substring(0, 16) : team)
                .mode(UpdateTeamsPacket.UpdateMode.UPDATE_INFO)
                .displayName(Component.empty())
                .friendlyFlags(List.of(UpdateTeamsPacket.FriendlyFlag.CAN_HURT_FRIENDLY))
                .nametagVisibility(UpdateTeamsPacket.NametagVisibility.valueOf(visibility.name()))
                .collisionRule(UpdateTeamsPacket.CollisionRule.ALWAYS)
                .color(UpdateTeamsPacket.TeamColor.valueOf(color.name()).ordinal())
                .prefix(GsonComponentSerializer.gson().deserialize(prefix))
                .suffix(GsonComponentSerializer.gson().deserialize(suffix));

        ConnectedPlayer connectedPlayer = (ConnectedPlayer) receiver;
        connectedPlayer.getConnection().write(packet);
    }
}
