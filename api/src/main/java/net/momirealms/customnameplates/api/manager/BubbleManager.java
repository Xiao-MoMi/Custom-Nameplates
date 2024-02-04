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

import net.momirealms.customnameplates.api.mechanic.bubble.Bubble;
import net.momirealms.customnameplates.api.mechanic.bubble.provider.AbstractChatProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface BubbleManager {

    /**
     * Set a custom chat provider
     *
     * @param provider provider
     * @return success or not
     */
    boolean setCustomChatProvider(AbstractChatProvider provider);

    /**
     * Remove a custom chat provider
     *
     * @return success or not
     */
    boolean removeCustomChatProvider();

    /**
     * Register a bubble into map
     *
     * @param key key
     * @param bubble bubble
     * @return success or not
     */
    boolean registerBubble(String key, Bubble bubble);

    /**
     * Unregister a bubble from map
     *
     * @param key key
     * @return success or not
     */
    boolean unregisterBubble(String key);

    /**
     * Get a bubble from map
     *
     * @param bubble key
     * @return bubble
     */
    @Nullable Bubble getBubble(String bubble);

    /**
     * If a player has a certain bubble
     *
     * @param player player
     * @param bubble key
     * @return
     */
    boolean hasBubble(Player player, String bubble);

    /**
     * Get a list of the bubbles that a player has
     *
     * @param player player
     * @return bubbles' keys
     */
    List<String> getAvailableBubbles(Player player);

    /**
     * Get a list of the bubbles' display names that a player has
     *
     * @param player player
     * @return bubbles' display names
     */
    List<String> getAvailableBubblesDisplayNames(Player player);

    /**
     * Get blacklist chat channels
     *
     * @return channels
     */
    String[] getBlacklistChannels();

    /**
     * Get all the bubbles
     *
     * @return bubbles
     */
    Collection<Bubble> getBubbles();

    /**
     * Whether a bubble exists
     */
    boolean containsBubble(String key);

    /**
     * Equip a bubble for a player
     *
     * @param player player
     * @param bubble bubble
     * @return success or not
     */
    boolean equipBubble(Player player, String bubble);

    /**
     * Unequip a bubble for a player
     *
     * @param player player
     */
    void unEquipBubble(Player player);

    /**
     * Get the default bubble key
     *
     * @return bubble key
     */
    String getDefaultBubble();

    void onChat(Player player, String text, String channel);

    /**
     * Get all the bubbles' keys
     *
     * @return keys
     */
    Collection<String> getBubbleKeys();

    /**
     * Trigger chat
     *
     * @param player player
     * @param text text
     */
    void onChat(Player player, String text);
}
