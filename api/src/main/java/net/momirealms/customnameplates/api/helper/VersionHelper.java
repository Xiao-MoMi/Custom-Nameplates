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
 * This class implements the VersionManager interface and is responsible for managing version-related information.
 */
public class VersionHelper {

    // Method to asynchronously check for plugin updates
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

    public static float version() {
        return version;
    }

    private static void checkMojMap() {
        // Check if the server is Mojmap
        try {
            Class.forName("net.minecraft.network.protocol.game.ClientboundBossEventPacket");
            mojmap = true;
        } catch (ClassNotFoundException ignored) {
        }
    }

    private static void checkFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException ignored) {
        }
    }

    private static void checkMohist() {
        try {
            Class.forName("com.mohistmc.api.ServerAPI");
            mohist = true;
        } catch (ClassNotFoundException ignored) {
        }
    }

    private static void checkPaper() {
        try {
            Class.forName("com.destroystokyo.paper.Metrics");
            paper = true;
        } catch (ClassNotFoundException ignored) {
        }
    }

    public static boolean isVersionNewerThan1_21_2() {
        return version >= 21.19;
    }

    public static boolean isVersionNewerThan1_20_5() {
        return version >= 20.49;
    }

    public static boolean isVersionNewerThan1_20_4() {
        return version >= 20.39;
    }

    public static boolean isVersionNewerThan1_19_4() {
        return version >= 19.39;
    }

    public static boolean isVersionNewerThan1_20_2() {
        return version >= 20.19;
    }

    public static boolean isVersionNewerThan1_20() {
        return version >= 20;
    }

    public static boolean isFolia() {
        return folia;
    }

    public static boolean isMohist() {
        return mohist;
    }

    public static boolean isPaperOrItsForks() {
        return paper;
    }

    public static boolean isMojmap() {
        return mojmap;
    }

    // Method to compare two version strings
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
                } else if (newPart.length > 1 && currentPart.length > 1) {
                    String[] newHotfix = newPart[1].split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
                    String[] currentHotfix = currentPart[1].split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
                    if (newHotfix.length == 2 && currentHotfix.length == 1) return true;
                    else if (newHotfix.length > 1 && currentHotfix.length > 1) {
                        int newHotfixNum = Integer.parseInt(newHotfix[1]);
                        int currentHotfixNum = Integer.parseInt(currentHotfix[1]);
                        if (newHotfixNum > currentHotfixNum) {
                            return true;
                        } else if (newHotfixNum < currentHotfixNum) {
                            return false;
                        } else {
                            return newHotfix[0].compareTo(currentHotfix[0]) > 0;
                        }
                    }
                } else if (newPart.length > 1) {
                    return true;
                } else if (currentPart.length > 1) {
                    return false;
                }
            }
            catch (NumberFormatException ignored) {
                return false;
            }
        }
        return newVS.length > currentVS.length;
    }
}