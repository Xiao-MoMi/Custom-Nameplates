package net.momirealms.customnameplates.api.manager;

import net.momirealms.customnameplates.api.mechanic.tag.unlimited.NamedEntity;
import net.momirealms.customnameplates.api.mechanic.tag.unlimited.UnlimitedObject;
import net.momirealms.customnameplates.api.mechanic.tag.unlimited.UnlimitedPlayer;
import net.momirealms.customnameplates.api.mechanic.tag.unlimited.UnlimitedTagSetting;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface UnlimitedTagManager {

    /**
     * Create a named entity (ArmorStand) for a player
     * To apply the changes, you should add it the named player instance
     *
     * @param player player
     * @param setting setting
     * @return named entity
     */
    @NotNull
    NamedEntity createNamedEntity(UnlimitedPlayer player, UnlimitedTagSetting setting);

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
    UnlimitedPlayer createTagForPlayer(Player player, List<UnlimitedTagSetting> settings);

    /**
     * Remove UnlimitedObject from map by uuid
     *
     * @param uuid uuid
     * @return The removed unlimited object
     */
    @Nullable
    UnlimitedObject removeUnlimitedObjectFromMap(UUID uuid);

    /**
     * Get an UnlimitedObject from map by uuid
     *
     * @param uuid uuid
     * @return The unlimited object
     */
    @Nullable
    UnlimitedObject getUnlimitedObject(UUID uuid);
}
