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

package net.momirealms.customnameplates.api.feature.bubble;

/**
 * Enum representing the different modes for Bubble channels.
 * The mode determines the visibility and access rules for Bubbles in various channels.
 */
public enum ChannelMode {
    /**
     * The ALL mode means that Bubbles can be displayed in all channels, regardless of the player's status or permissions.
     */
    ALL,
    /**
     * The JOINED mode means that Bubbles can only be displayed in channels where the player is currently a member.
     */
    JOINED,
    /**
     * The CAN_JOIN mode means that Bubbles can be displayed in channels that the player can join, even if they are not currently in them.
     */
    CAN_JOIN
}