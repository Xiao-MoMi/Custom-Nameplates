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

package net.momirealms.customnameplates.api.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import net.momirealms.customnameplates.api.CNPlayer;
import org.jetbrains.annotations.ApiStatus;

/**
 * Interface for injecting and managing Netty pipeline handlers for players.
 */
public interface PipelineInjector {

    /**
     * Sets the user (player) associated with the given Netty channel.
     * This is an internal method for managing the mapping between a channel and a player.
     *
     * @param channel the Netty channel to associate with the player
     * @param user the player to associate with the channel
     */
    @ApiStatus.Internal
    void setUser(Channel channel, CNPlayer user);

    /**
     * Retrieves the player associated with the given Netty channel.
     * This is an internal method for fetching the player mapped to a specific channel.
     *
     * @param channel the Netty channel for which the player is being retrieved
     * @return the player associated with the given channel
     */
    @ApiStatus.Internal
    CNPlayer getUser(Channel channel);

    /**
     * Removes the player associated with the specified Netty channel.
     * This is an internal method for detaching a player from the given channel.
     *
     * @param channel the Netty channel from which the player will be removed
     * @return the player that was removed, or null if no player was associated
     */
    @ApiStatus.Internal
    CNPlayer removeUser(Channel channel);

    /**
     * Retrieves the player associated with the specified object.
     * This method is internal and can be used to get a player from various representations.
     *
     * @param player the object representing the player (could be a reference or other object type)
     * @return the player associated with the object
     */
    @ApiStatus.Internal
    CNPlayer getUser(Object player);

    /**
     * Retrieves the Netty channel associated with the specified player.
     *
     * @param player the player whose channel is being retrieved
     * @return the Netty channel associated with the player
     */
    @ApiStatus.Internal
    Channel getChannel(Object player);

    /**
     * Creates a custom ChannelDuplexHandler for the specified player, used to intercept and modify network traffic.
     *
     * @param player the player for whom the handler is being created
     * @return a ChannelDuplexHandler for the player
     */
    @ApiStatus.Internal
    ChannelDuplexHandler createHandler(CNPlayer player);
}