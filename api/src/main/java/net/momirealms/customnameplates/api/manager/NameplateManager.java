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

package net.momirealms.customnameplates.api.manager;

import net.momirealms.customnameplates.api.mechanic.nameplate.CachedNameplate;
import net.momirealms.customnameplates.api.mechanic.nameplate.Nameplate;
import net.momirealms.customnameplates.api.mechanic.nameplate.TagMode;
import net.momirealms.customnameplates.api.mechanic.tag.NameplatePlayer;
import net.momirealms.customnameplates.common.team.TeamColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface NameplateManager {

    /**
     * Get the default nameplate
     */
    @NotNull
    String getDefaultNameplate();

    /**
     * Put an entity's ID to map
     * This map is used for quickly getting the entity instance
     * Removal from the map is necessary when the entity is invalid
     * Otherwise it would cause memory leak
     *
     * @param entityID entityID
     * @param entity entity
     */
    boolean putEntityIDToMap(int entityID, @NotNull Entity entity);

    /**
     * Remove the entity from map
     *
     * @param entityID entityID
     * @return the removed entity
     */
    @Nullable
    Entity removeEntityIDFromMap(int entityID);

    /**
     * Nameplates are cached in memory so they would not be frequently updated
     * The update rate is decided by "refresh-frequency" in nameplate.yml
     *
     * @param uuid player uuid
     * @param nameplate cached nameplate
     */
    boolean putCachedNameplateToMap(@NotNull UUID uuid, @NotNull CachedNameplate nameplate);

    /**
     * Remove CachedNameplate from map
     *
     * @param uuid player uuid
     * @return removed CachedNameplate
     */
    @Nullable
    CachedNameplate removeCachedNameplateFromMap(@NotNull UUID uuid);

    /**
     * Get player by entityID from the cache
     *
     * @param id entityID
     * @return player
     */
    @Nullable
    Player getPlayerByEntityID(int id);

    /**
     * Get entity by entityID from the cache
     *
     * @param id entityID
     * @return entity
     */
    @Nullable
    Entity getEntityByEntityID(int id);

    /**
     * Update a player's cached nameplate
     * The nameplate is affected by "prefix" and "suffix" option
     *
     * @param player player
     * @return if the nameplate is updated
     */
    boolean updateCachedNameplate(@NotNull Player player);

    /**
     * Update a player's cached nameplate
     * The nameplate is affected by "prefix" and "suffix" option
     *
     * @param player player
     * @return if the nameplate is updated
     */
    boolean updateCachedNameplate(@NotNull Player player, @Nullable Nameplate nameplate);

    /**
     * This should not be null when player's data is loaded (async process)
     *
     * @param player player
     * @return cached nameplate
     */
    @Nullable
    CachedNameplate getCacheNameplate(@NotNull Player player);

    /**
     * Create a name tag for a player, the tag type is decided by the mode in nameplate.yml
     * If mode is DISABLE, this method would return null
     * The tag would be put into map automatically and you can get it by getNameplatePlayer(uuid)
     *
     * @param player player
     */
    @Nullable
    NameplatePlayer createNameTag(@NotNull Player player);

    /**
     * Put a nameplater player to map
     *
     * @param player player
     */
    void putNameplatePlayerToMap(@NotNull NameplatePlayer player);

    /**
     * Get a nameplate player from map
     *
     * @param uuid uuid
     * @return nameplate player
     */
    @Nullable
    NameplatePlayer getNameplatePlayer(@NotNull UUID uuid);

    /**
     * Remove nameplate player from map
     *
     * @param uuid uuid
     * @return removed nameplate player
     */
    @Nullable
    NameplatePlayer removeNameplatePlayerFromMap(@NotNull UUID uuid);

    /**
     * Get the nameplate's prefix with text tags
     *
     * @param player player
     * @return prefix with text tags
     */
    @NotNull
    String getNameplatePrefix(@NotNull Player player);

    /**
     * Get the nameplate's suffix with text tags
     *
     * @param player player
     * @return suffix with text tags
     */
    @NotNull
    String getNameplateSuffix(@NotNull Player player);

    /**
     * Get the full nameplate tag
     *
     * @param player player
     * @return nameplate tag
     */
    @NotNull
    String getFullNameTag(@NotNull Player player);

    /**
     * Register a custom nameplate into map
     *
     * @param key key
     * @param nameplate nameplate
     * @return success or not
     */
    boolean registerNameplate(@NotNull String key, @NotNull Nameplate nameplate);

    /**
     * Unregister a nameplate from map
     *
     * @param key key
     * @return success or not
     */
    boolean unregisterNameplate(@NotNull String key);

    /**
     * Equip a nameplate for a player
     *
     * @param player player
     * @param nameplateKey key
     * @param temp whether save to storage
     * @return success or not
     */
    boolean equipNameplate(@NotNull Player player, @NotNull String nameplateKey, boolean temp);

    /**
     * Remove a nameplate for a player
     *
     * @param player player
     * @param temp whether save to storage
     */
    void unEquipNameplate(Player player, boolean temp);

    /**
     * Is team managed on proxy side
     */
    boolean isProxyMode();

    /**
     * Get preview duration
     */
    int getPreviewDuration();

    /**
     * Get the tag mode
     */
    @NotNull
    TagMode getTagMode();

    /**
     * Get a nameplate by key
     *
     * @param key key
     * @return nameplate
     */
    @NotNull
    Nameplate getNameplate(@NotNull String key);

    /**
     * Get all the nameplates
     *
     * @return nameplates
     */
    @NotNull
    Collection<Nameplate> getNameplates();

    /**
     * Get all the nameplates' keys
     *
     * @return keys
     */
    @NotNull
    Collection<String> getNameplateKeys();

    /**
     * Whether a nameplate key exists
     */
    boolean containsNameplate(@NotNull String key);

    /**
     * Get all the nameplates that the player has
     *
     * @param player player
     * @return nameplates' keys
     */
    @NotNull
    List<String> getAvailableNameplates(@NotNull Player player);

    /**
     * If player has permission for a certain nameplate
     */
    boolean hasNameplate(@NotNull Player player, @NotNull String nameplate);

    /**
     * Get all the nameplates' display names that the player has
     *
     * @param player player
     * @return nameplates' display names
     */
    @NotNull
    List<String> getAvailableNameplateDisplayNames(@NotNull Player player);

    /**
     * Get the nameplate's team color
     *
     * @param player player
     * @return team color
     */
    @NotNull
    TeamColor getTeamColor(@NotNull Player player);

    /**
     * Get team tag manager
     */
    @NotNull
    TeamTagManager getTeamTagManager();

    /**
     * Get unlimited tag manager
     */
    @NotNull
    UnlimitedTagManager getUnlimitedTagManager();
}
