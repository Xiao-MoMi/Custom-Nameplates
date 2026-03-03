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

package net.momirealms.customnameplates.api.helper;

import java.util.UUID;

/**
 * Utility class for Bedrock player detection.
 * Uses reflection to avoid ClassNotFoundException when FloodgateAPI is not available.
 */
public class BedrockHelper {

    private static Boolean floodgateAvailable = null;
    private static Object floodgateInstance = null;

    /**
     * Check if FloodgateAPI is available on the server.
     *
     * @return true if FloodgateAPI is available
     */
    public static boolean isFloodgateAvailable() {
        if (floodgateAvailable != null) {
            return floodgateAvailable;
        }

        try {
            Class<?> floodgateApiClass = Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            floodgateInstance = floodgateApiClass.getMethod("getInstance").invoke(null);
            floodgateAvailable = true;
            return true;
        } catch (Exception e) {
            floodgateAvailable = false;
            return false;
        }
    }

    /**
     * Check if a player is a Bedrock player by UUID.
     *
     * @param uuid the player's UUID
     * @return true if the player is a Bedrock player, false otherwise
     */
    public static boolean isBedrockPlayer(UUID uuid) {
        if (!isFloodgateAvailable() || floodgateInstance == null) {
            return false;
        }

        try {
            Class<?> floodgateApiClass = Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            return (boolean) floodgateApiClass
                    .getMethod("isFloodgatePlayer", UUID.class)
                    .invoke(floodgateInstance, uuid);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Reset the cached state. Useful for reloads.
     */
    public static void reset() {
        floodgateAvailable = null;
        floodgateInstance = null;
    }
}
