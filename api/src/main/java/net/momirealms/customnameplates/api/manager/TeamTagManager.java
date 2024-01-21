package net.momirealms.customnameplates.api.manager;

import net.momirealms.customnameplates.api.mechanic.tag.team.TeamTagPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface TeamTagManager {

    /**
     * Create team tag for a player
     * If failed, the return value would be null
     * This happens when there already exists a team tag for a player
     *
     * @return team tag
     */
    @Nullable
    TeamTagPlayer createTagForPlayer(Player player, String prefix, String suffix);

    /**
     * Remove a team tag from map by uuid
     *
     * @param uuid uuid
     * @return team tag
     */
    @Nullable
    TeamTagPlayer removeTeamPlayerFromMap(UUID uuid);
}
