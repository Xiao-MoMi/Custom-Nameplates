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

import net.momirealms.customnameplates.api.CNPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Interface for sending packets to players.
 */
public interface PacketSender {

    /**
     * Sends a single packet to the specified player.
     *
     * @param player the player to send the packet to
     * @param packet the packet to be sent
     */
    void sendPacket(@NotNull CNPlayer player, Object packet);

    /**
     * Sends a list of packets to the specified player.
     *
     * @param player the player to send the packets to
     * @param packet the list of packets to be sent
     */
    void sendPacket(@NotNull CNPlayer player, List<Object> packet);
}
