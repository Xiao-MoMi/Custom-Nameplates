package net.momirealms.customnameplates.api.manager;

import net.momirealms.customnameplates.api.mechanic.tag.unlimited.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface UnlimitedTagManager {

    @NotNull StaticTextEntity createNamedEntity(EntityTagEntity entity, StaticTextTagSetting setting);

    /**
     * Create a named entity (ArmorStand) for a player
     * To apply the changes, you should add it the named player instance
     *
     * @param player player
     * @param setting setting
     * @return named entity
     */
    @NotNull
    DynamicTextEntity createNamedEntity(EntityTagPlayer player, DynamicTextTagSetting setting);

    EntityTagEntity createOrGetTagForEntity(Entity entity);

    EntityTagPlayer createOrGetTagForPlayer(Player player);

    EntityTagPlayer createOrGetTagForPlayer(Player player, boolean manageTeam);
}
