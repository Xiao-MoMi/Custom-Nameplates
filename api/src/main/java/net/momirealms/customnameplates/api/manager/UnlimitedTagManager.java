package net.momirealms.customnameplates.api.manager;

import net.momirealms.customnameplates.api.mechanic.tag.unlimited.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

    EntityTagEntity createTagForEntity(Entity entity);

    /**
     * Create unlimited tags for a player
     * If failed, the return value would be null
     * This happens when there already exists an UnlimitedObject for a player
     *
     * @param player player
     * @param settings settings
     * @return unlimited tag
     */
    @Nullable
    EntityTagPlayer createTagForPlayer(Player player, List<DynamicTextTagSetting> settings);
}
