/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customnameplates.api;

import io.netty.channel.Channel;
import net.momirealms.customnameplates.api.feature.Feature;
import net.momirealms.customnameplates.api.feature.TimeStampData;
import net.momirealms.customnameplates.api.feature.tag.TeamView;
import net.momirealms.customnameplates.api.network.Tracker;
import net.momirealms.customnameplates.api.placeholder.Placeholder;
import net.momirealms.customnameplates.api.placeholder.PlayerPlaceholder;
import net.momirealms.customnameplates.api.placeholder.RelationalPlaceholder;
import net.momirealms.customnameplates.api.placeholder.SharedPlaceholder;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.api.util.Vector3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * CustomNameplates Player
 */
public interface CNPlayer {

    /**
     * Returns the player's name.
     *
     * @return the player's name
     */
    String name();

    /**
     * Returns the UUID of the player.
     *
     * @return the player's UUID
     */
    UUID uuid();

    /**
     * Returns the underlying player object (server-side representation).
     *
     * @return the player object
     */
    Object player();

    /**
     * Returns the channel of the player
     *
     * @return the channel
     */
    Channel channel();

    /**
     * Returns the entity ID of the player.
     *
     * @return the entity ID
     */
    int entityID();

    /**
     * Returns the player's current position as a {@link Vector3}.
     *
     * @return the player's position
     */
    Vector3 position();

    /**
     * Returns the name of the world the player is in.
     *
     * @return the world name
     */
    String world();

    /**
     * Checks if the player is currently online.
     *
     * @return true if the player is online, false otherwise
     */
    boolean isOnline();

    /**
     *
     * Checks if the player is on spectator mode
     *
     * @return true if the player is on spectator mode, false otherwise
     */
    boolean isSpectator();

    /**
     * Returns the scale of the player.
     *
     * @return the player's scale
     */
    double scale();

    /**
     * Checks if the player is crouching.
     *
     * @return true if the player is crouching, false otherwise
     */
    boolean isCrouching();

    /**
     * Checks if the player has been fully loaded into the system.
     *
     * @return true if the player is loaded, false otherwise
     */
    boolean isLoaded();

    /**
     * Checks if the player is currently previewing
     *
     * @return true if the player is previewing, false otherwise
     */
    boolean isTempPreviewing();

    /**
     * Checks if the player has turned their nameplate on
     *
     * @return true if the player has turned their nameplate on, false otherwise
     */
    boolean isToggleablePreviewing();

    /**
     * Checks if the player has the specified permission.
     *
     * @param permission the permission to check
     * @return true if the player has the permission, false otherwise
     */
    boolean hasPermission(String permission);

    /**
     * Returns the player's current time
     *
     * @return the player's time
     */
    long playerTime();

    /**
     * Checks if the player is flying.
     *
     * @return true if the player is flying, false otherwise
     */
    boolean isFlying();

    /**
     * Returns the remaining air level for the player.
     *
     * @return the player's remaining air
     */
    int remainingAir();

    /**
     * Retrieves the set of features that are actively using the specified placeholder.
     *
     * @param placeholder the placeholder to check
     * @return the set of active features using the placeholder
     */
    Set<Feature> activeFeatures(Placeholder placeholder);

    /**
     * Returns the placeholders that are actively used by the player's features.
     *
     * @return an array of active placeholders
     */
    Placeholder[] activePlaceholders();

    /**
     * Acquires the actionbar, preventing CustomNameplates from taking control of it.
     *
     * @param id The feature ID requesting the actionbar.
     */
    void acquireActionBar(String id);

    /**
     * Releases the actionbar, allowing CustomNameplates to take control again if no other features are active.
     *
     * @param id The feature ID releasing the actionbar.
     */
    void releaseActionBar(String id);

    /**
     * Checks if CustomNameplates should take control of the actionbar.
     *
     * @return True if CustomNameplates should take over the actionbar, false otherwise.
     */
    boolean shouldCNTakeOverActionBar();

    /**
     * Retrieves the list of placeholders that need to be refreshed based on their refresh intervals.
     *
     * @return a list of placeholders to refresh
     */
    List<Placeholder> activePlaceholdersToRefresh();

    /**
     * Forces an update for the specified placeholders and relational placeholders with another player.
     *
     * @param placeholders the placeholders to update
     * @param another      the players related to the placeholders
     */
    void forceUpdatePlaceholders(Set<Placeholder> placeholders, Collection<CNPlayer> another);

    /**
     * Retrieves the cached data for a given placeholder.
     *
     * @param placeholder the placeholder to retrieve data for
     * @return the cached data as a string
     */
    @NotNull
    String getCachedSharedValue(SharedPlaceholder placeholder);

    /**
     * Retrieves the cached data for a given placeholder.
     *
     * @param placeholder the placeholder to retrieve data for
     * @return the cached data as a string
     */
    @NotNull
    String getCachedPlayerValue(PlayerPlaceholder placeholder);

    /**
     * Retrieves the cached relational data between this player and another for a given placeholder.
     *
     * @param placeholder the relational placeholder
     * @param another     the other player
     * @return the relational data as a string
     */
    @NotNull
    String getCachedRelationalValue(RelationalPlaceholder placeholder, CNPlayer another);

    /**
     * Retrieves the cached {@link TimeStampData} for a given placeholder.
     *
     * @param placeholder the placeholder to retrieve data for
     * @return the cached TickStampData, or null if none exists
     */
    @Nullable
    TimeStampData<String> getRawPlayerValue(PlayerPlaceholder placeholder);

    /**
     * Retrieves the cached {@link TimeStampData} for a given placeholder.
     *
     * @param placeholder the placeholder to retrieve data for
     * @return the cached TickStampData, or null if none exists
     */
    @Nullable
    TimeStampData<String> getRawSharedValue(SharedPlaceholder placeholder);

    /**
     * Retrieves the cached relational {@link TimeStampData} for a given placeholder.
     *
     * @param placeholder the relational placeholder
     * @param another     the other player
     * @return the cached relational TickStampData, or null if none exists
     */
    @Nullable
    TimeStampData<String> getRawRelationalValue(RelationalPlaceholder placeholder, CNPlayer another);

    /**
     * Caches the specified {@link TimeStampData} for the given placeholder.
     *
     * @param placeholder the placeholder to cache
     * @param value       the value to cache
     */
    void setPlayerValue(PlayerPlaceholder placeholder, TimeStampData<String> value);

    /**
     * Caches the specified {@link TimeStampData} for the given placeholder.
     *
     * @param placeholder the placeholder to cache
     * @param value       the value to cache
     */
    void setSharedValue(SharedPlaceholder placeholder, TimeStampData<String> value);

    /**
     * Caches the specified relational {@link TimeStampData} for a given placeholder and player.
     *
     * @param placeholder the relational placeholder
     * @param another     the other player
     * @param value       the value to cache
     */
    void setRelationalValue(RelationalPlaceholder placeholder, CNPlayer another, TimeStampData<String> value);

//    /**
//     * Caches the specified value for the given placeholder.
//     *
//     * @param placeholder the placeholder to cache
//     * @param value       the value to cache
//     * @return true if the value was changed, false otherwise
//     */
//    boolean setPlayerValue(PlayerPlaceholder placeholder, String value);

//    /**
//     * Caches the specified relational value for a given placeholder and player.
//     *
//     * @param placeholder the relational placeholder
//     * @param another     the other player
//     * @param value       the value to cache
//     * @return true if the value was changed, false otherwise
//     */
//    boolean setRelationalValue(RelationalPlaceholder placeholder, CNPlayer another, String value);

    /**
     * Adds a feature to the player
     *
     * @param feature the feature to add
     */
    void addFeature(Feature feature);

    /**
     * Removes a feature from the player.
     *
     * @param feature the feature to remove
     */
    void removeFeature(Feature feature);

    /**
     * Checks if the player meets the specified requirements.
     *
     * @param requirements the requirements to check
     * @return true if all requirements are met, false otherwise
     */
    boolean isMet(Requirement[] requirements);

    /**
     * Checks if the player meets the specified relational requirements with another player.
     *
     * @param another      the other player
     * @param requirements the relational requirements to check
     * @return true if all requirements are met, false otherwise
     */
    boolean isMet(CNPlayer another, Requirement[] requirements);

    /**
     * Adds a player to be track this player, creating a tracker for relational placeholders.
     *
     * @param another the player
     * @return the tracker instance
     */
    Tracker addPlayerToTracker(CNPlayer another);

    /**
     * Removes a player from tracking this player.
     *
     * @param another the player to stop tracking
     */
    void removePlayerFromTracker(CNPlayer another);

    /**
     * Retrieves the set of nearby players tracking this player.
     *
     * @return the set of nearby players
     */
    Collection<CNPlayer> nearbyPlayers();

    /**
     * Adds passenger entities to the tracker for another player.
     *
     * @param another    the player whose passengers to track
     * @param passengers the IDs of the passenger entities
     */
    void trackPassengers(CNPlayer another, int... passengers);

    /**
     * Removes passenger entities from the tracker for another player.
     *
     * @param another    the player whose passengers to stop tracking
     * @param passengers the IDs of the passenger entities
     */
    void untrackPassengers(CNPlayer another, int... passengers);

    /**
     * Retrieves the IDs of tracked passenger entities for another player.
     *
     * @param another the viewer
     * @return the set of passenger entity IDs
     */
    Set<Integer> getTrackedPassengerIds(CNPlayer another);

    /**
     * Retrieves the IDs of all passenger entities currently associated with this player.
     *
     * @return the set of passenger entity IDs
     */
    Set<Integer> passengers();

    /**
     * Retrieves the tracker instance for another player.
     *
     * @param another the viewer
     * @return the tracker instance, or null if none exists
     */
    Tracker getTracker(CNPlayer another);

    /**
     * Gets the bubble currently in use (Includes temp bubble used in preview)
     *
     * @return bubble
     */
    String currentBubble();

    /**
     * Sets the bubble currently in use, which would not be saved
     *
     * @param bubble id
     * @return false if data not loaded
     */
    boolean setCurrentBubble(String bubble);

    /**
     * Sets the bubble data
     *
     * @param bubble id
     * @return false if data not loaded
     */
    boolean setBubbleData(String bubble);

    /**
     * Gets the bubble data
     *
     * @return bubble id
     */
    String bubbleData();

    /**
     * Gets the nameplate currently in use (Includes temp nameplate used in preview)
     *
     * @return nameplate
     */
    String currentNameplate();

    /**
     * Sets the nameplate currently in use, which would not be saved
     *
     * @param nameplate id
     * @return false if data not loaded
     */
    boolean setCurrentNameplate(String nameplate);

    /**
     * Gets the nameplate data
     *
     * @return the nameplate id
     */
    String nameplateData();

    /**
     * Sets the nameplate data
     *
     * @param nameplate id
     * @return false if data not loaded
     */
    boolean setNameplateData(String nameplate);

    /**
     * Save the player's current nameplate/bubble to database
     */
    void save();

    /**
     * Get the player's team view
     *
     * @return team view
     */
    TeamView teamView();

    /**
     * Check if the player is initialized
     *
     * @return initialized or not
     */
    boolean isInitialized();
}
