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

package net.momirealms.customnameplates.api.feature.bubble;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.common.plugin.feature.Reloadable;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

/**
 * The BubbleManager interface is responsible for managing the configuration and display of Bubbles,
 * including retrieving available Bubbles, managing Bubble configurations, and controlling display settings.
 */
public interface BubbleManager extends Reloadable {

    /**
     * Returns a collection of all available Bubble instances.
     *
     * @return a collection of Bubble instances
     */
    Collection<Bubble> bubbles();

    /**
     * Returns a collection of all Bubble configurations.
     *
     * @return a collection of BubbleConfig instances
     */
    Collection<BubbleConfig> bubbleConfigs();

    /**
     * Retrieves a Bubble by its unique ID.
     *
     * @param id the Bubble ID
     * @return the Bubble instance, or null if not found
     */
    @Nullable
    Bubble bubbleById(String id);

    /**
     * Retrieves a Bubble configuration by its unique ID.
     *
     * @param id the BubbleConfig ID
     * @return the BubbleConfig instance, or null if not found
     */
    @Nullable
    BubbleConfig bubbleConfigById(String id);

    /**
     * Checks if a player has access to a specific Bubble by its ID.
     *
     * @param player the player to check
     * @param id     the Bubble ID
     * @return true if the player has the Bubble, false otherwise
     */
    boolean hasBubble(CNPlayer player, String id);

    /**
     * Returns a collection of available Bubbles for the player.
     *
     * @param player the player to check
     * @return a collection of available BubbleConfig instances
     */
    Collection<BubbleConfig> availableBubbles(CNPlayer player);

    /**
     * Returns the set of channels that are blacklisted from displaying Bubbles.
     *
     * @return a set of blacklisted channel names
     */
    Set<String> blacklistChannels();

    /**
     * Returns the current mode for Bubble channels.
     *
     * @return the ChannelMode in use
     */
    ChannelMode channelMode();

    /**
     * Returns the default Bubble ID used when no specific Bubble is set.
     *
     * @return the default Bubble ID
     */
    String defaultBubbleId();

    /**
     * Returns the requirements to send a Bubble.
     *
     * @return an array of send Bubble requirements
     */
    Requirement[] sendBubbleRequirements();

    /**
     * Returns the requirements to view a Bubble.
     *
     * @return an array of view Bubble requirements
     */
    Requirement[] viewBubbleRequirements();

    /**
     * Returns the vertical offset for Bubble display.
     *
     * @return the vertical offset as a double
     */
    double verticalOffset();

    /**
     * Returns the duration (in ticks) for which the Bubble remains visible.
     *
     * @return the stay duration in ticks
     */
    int stayDuration();

    /**
     * Returns the duration (in ticks) for the Bubble to appear.
     *
     * @return the appear duration in ticks
     */
    int appearDuration();

    /**
     * Returns the duration (in ticks) for the Bubble to disappear.
     *
     * @return the disappear duration in ticks
     */
    int disappearDuration();

    /**
     * Returns the view range for Bubbles.
     *
     * @return the view range as a float
     */
    float viewRange();
}
