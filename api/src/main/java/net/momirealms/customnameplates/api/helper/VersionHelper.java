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

import net.momirealms.customnameplates.common.plugin.NameplatesPlugin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * VersionHelper is a utility class that provides methods for managing and checking version-related information,
 * including plugin updates and server version details. It implements the VersionManager interface.
 */
public class VersionHelper {

    /**
     * A function to check for plugin updates asynchronously by comparing the plugin's current version with the latest version available.
     */
    public static final Function<NameplatesPlugin, CompletableFuture<Boolean>> UPDATE_CHECKER = (plugin) -> {
        CompletableFuture<Boolean> updateFuture = new CompletableFuture<>();
        plugin.getScheduler().async().execute(() -> {
            try {
                URL url = new URL("https://api.polymart.org/v1/getResourceInfoSimple/?resource_id=2723&key=version");
                URLConnection conn = url.openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(60000);
                InputStream inputStream = conn.getInputStream();
                String newest = new BufferedReader(new InputStreamReader(inputStream)).readLine();
                String current = plugin.getPluginVersion();
                inputStream.close();
                if (!compareVer(newest, current)) {
                    updateFuture.complete(false);
                    return;
                }
                updateFuture.complete(true);
            } catch (Exception exception) {
                plugin.getPluginLogger().warn("Error occurred when checking update.");
                updateFuture.completeExceptionally(exception);
            }
        });
        return updateFuture;
    };

    private static float version;
    private static boolean mojmap;
    private static boolean folia;
    private static boolean mohist;
    private static boolean paper;

    /**
     * Initializes version-specific settings based on the server version.
     * This method checks if the server is running Mojmap, Folia, Mohist, or Paper.
     *
     * @param serverVersion The server version string.
     */
    public static void init(String serverVersion) {
        String[] split = serverVersion.split("\\.");
        version = Float.parseFloat(split[1] + "." + (split.length == 3 ? split[2] : "0"));
        checkMojMap();
        checkFolia();
        checkMohist();
        checkPaper();
        boolean isModdedServer = mohist;
        paper = paper && !isModdedServer;
    }

    /**
     * Gets the current server version as a float.
     *
     * @return The server version as a float.
     */
    public static float version() {
        return version;
    }

    /**
     * Checks if the server is running Mojmap.
     */
    private static void checkMojMap() {
        try {
            Class.forName("net.minecraft.network.protocol.game.ClientboundBossEventPacket");
            mojmap = true;
        } catch (ClassNotFoundException ignored) {
        }
    }

    /**
     * Checks if the server is running Folia.
     */
    private static void checkFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException ignored) {
        }
    }

    /**
     * Checks if the server is running Mohist.
     */
    private static void checkMohist() {
        try {
            Class.forName("com.mohistmc.api.ServerAPI");
            mohist = true;
        } catch (ClassNotFoundException ignored) {
        }
    }

    /**
     * Checks if the server is running Paper or its forks.
     */
    private static void checkPaper() {
        try {
            Class.forName("com.destroystokyo.paper.Metrics");
            paper = true;
        } catch (ClassNotFoundException ignored) {
        }
    }

    /**
     * Checks if the server version is newer than 1.21.5
     *
     * @return True if the version is newer than 1.21.5, otherwise false.
     */
    public static boolean isVersionNewerThan1_21_5() {
        return version >= 21.49f;
    }

    /**
     * Checks if the server version is newer than 1.21.4
     *
     * @return True if the version is newer than 1.21.4, otherwise false.
     */
    public static boolean isVersionNewerThan1_21_4() {
        return version >= 21.39f;
    }

    /**
     * Checks if the server version is newer than 1.21.2.
     *
     * @return True if the version is newer than 1.21.2, otherwise false.
     */
    public static boolean isVersionNewerThan1_21_2() {
        return version >= 21.19f;
    }

    /**
     * Checks if the server version is newer than 1.20.5.
     *
     * @return True if the version is newer than 1.20.5, otherwise false.
     */
    public static boolean isVersionNewerThan1_20_5() {
        return version >= 20.49f;
    }

    /**
     * Checks if the server version is newer than 1.20.4.
     *
     * @return True if the version is newer than 1.20.4, otherwise false.
     */
    public static boolean isVersionNewerThan1_20_4() {
        return version >= 20.39f;
    }

    /**
     * Checks if the server version is newer than 1.19.4.
     *
     * @return True if the version is newer than 1.19.4, otherwise false.
     */
    public static boolean isVersionNewerThan1_19_4() {
        return version >= 19.39f;
    }

    /**
     * Checks if the server version is newer than 1.20.2.
     *
     * @return True if the version is newer than 1.20.2, otherwise false.
     */
    public static boolean isVersionNewerThan1_20_2() {
        return version >= 20.19f;
    }

    /**
     * Checks if the server version is newer than 1.20.
     *
     * @return True if the version is newer than 1.20, otherwise false.
     */
    public static boolean isVersionNewerThan1_20() {
        return version >= 20f;
    }

    /**
     * Checks if the server is running Folia.
     *
     * @return True if the server is running Folia, otherwise false.
     */
    public static boolean isFolia() {
        return folia;
    }

    /**
     * Checks if the server is running Mohist.
     *
     * @return True if the server is running Mohist, otherwise false.
     */
    public static boolean isMohist() {
        return mohist;
    }

    /**
     * Checks if the server is running Paper or its forks.
     *
     * @return True if the server is running Paper or its forks, otherwise false.
     */
    public static boolean isPaperOrItsForks() {
        return paper;
    }

    /**
     * Checks if the server is using Mojmap.
     *
     * @return True if the server is using Mojmap, otherwise false.
     */
    public static boolean isMojmap() {
        return mojmap;
    }

    /**
     * Compares two version strings to determine if the first version is newer than the second.
     *
     * @param newV The new version string.
     * @param currentV The current version string.
     * @return True if the new version is newer than the current version, otherwise false.
     */
    private static boolean compareVer(String newV, String currentV) {
        if (newV == null || currentV == null || newV.isEmpty() || currentV.isEmpty()) {
            return false;
        }
        String[] newVS = newV.split("\\.");
        String[] currentVS = currentV.split("\\.");
        int maxL = Math.min(newVS.length, currentVS.length);
        for (int i = 0; i < maxL; i++) {
            try {
                String[] newPart = newVS[i].split("-");
                String[] currentPart = currentVS[i].split("-");
                int newNum = Integer.parseInt(newPart[0]);
                int currentNum = Integer.parseInt(currentPart[0]);
                if (newNum > currentNum) {
                    return true;
                } else if (newNum < currentNum) {
                    return false;
                }
            } catch (NumberFormatException e) {
                // handle error
            }
        }
        return false;
    }
}
