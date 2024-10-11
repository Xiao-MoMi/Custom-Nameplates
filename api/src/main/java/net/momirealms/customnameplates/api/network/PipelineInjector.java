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

    @ApiStatus.Internal
    void setUser(Channel channel, CNPlayer user);

    @ApiStatus.Internal
    CNPlayer getUser(Channel channel);

    @ApiStatus.Internal
    CNPlayer removeUser(Channel channel);

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