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

package net.momirealms.customnameplates.paper.mechanic.misc;

import me.clip.placeholderapi.PlaceholderAPI;
import net.momirealms.customnameplates.api.manager.VersionManager;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.paper.CustomNameplatesPluginImpl;
import net.momirealms.customnameplates.paper.adventure.AdventureManagerImpl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CompletableFuture;

/**
 * This class implements the VersionManager interface and is responsible for managing version-related information.
 */
public class VersionManagerImpl implements VersionManager, Listener {

    private final boolean isNewerThan1_19;
    private final boolean isNewerThan1_19_R2;
    private final boolean isNewerThan1_19_R3;
    private final boolean isNewerThan1_20;
    private final boolean isNewerThan1_20_R2;
    private final String serverVersion;
    private final CustomNameplatesPluginImpl plugin;
    private boolean isFolia;
    private final String pluginVersion;
    private boolean isLatest = true;

    @SuppressWarnings("deprecation")
    public VersionManagerImpl(CustomNameplatesPluginImpl plugin) {
        this.plugin = plugin;
        // Get the server version
        serverVersion = plugin.getServer().getClass().getPackage().getName().split("\\.")[3];
        String[] split = serverVersion.split("_");
        int main_ver = Integer.parseInt(split[1]);
        // Determine if the server version is newer than 1_19_R2 and 1_20_R1
        if (main_ver >= 20) {
            isNewerThan1_20_R2 = Integer.parseInt(split[2].substring(1)) >= 2;
            isNewerThan1_19_R2 = isNewerThan1_19_R3 = true;
            isNewerThan1_20 = true;
            isNewerThan1_19 = true;
        } else if (main_ver == 19) {
            isNewerThan1_19_R2 = Integer.parseInt(split[2].substring(1)) >= 2;
            isNewerThan1_19_R3 = Integer.parseInt(split[2].substring(1)) >= 3;
            isNewerThan1_20 = isNewerThan1_20_R2 = false;
            isNewerThan1_19 = true;
        } else {
            isNewerThan1_19 = isNewerThan1_19_R2= isNewerThan1_19_R3 = false;
            isNewerThan1_20 = isNewerThan1_20_R2 = false;
        }
        // Check if the server is Folia
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.AsyncScheduler");
            this.isFolia = true;
        } catch (ClassNotFoundException ignored) {
        }
        // Get the plugin version
        this.pluginVersion = plugin.getDescription().getVersion();
    }

    @Override
    public boolean isVersionNewerThan1_19_R2() {
        return isNewerThan1_19_R2;
    }

    @Override
    public boolean isVersionNewerThan1_20() {
        return isNewerThan1_20;
    }

    @Override
    public boolean isVersionNewerThan1_20_R2() {
        return isNewerThan1_20_R2;
    }

    @Override
    public String getPluginVersion() {
        return pluginVersion;
    }

    @Override
    public boolean isLatest() {
        return isLatest;
    }

    @Override
    public boolean isVersionNewerThan1_19() {
        return isNewerThan1_19;
    }

    @Override
    public boolean isVersionNewerThan1_19_R3() {
        return isNewerThan1_19_R3;
    }

    @Override
    public int getPackFormat() {
        switch (serverVersion) {
            case "v1_20_R3" -> {
                return 22;
            }
            case "v1_20_R2" -> {
                return 18;
            }
            case "v1_20_R1" -> {
                return 15;
            }
            case "v1_19_R3" -> {
                return 13;
            }
            case "v1_19_R2" -> {
                return 12;
            }
            case "v1_19_R1" -> {
                return 9;
            }
            case "v1_18_R1", "v1_18_R2" -> {
                return 8;
            }
            default -> {
                return 7;
            }
        }
    }

    @Override
    public boolean isFolia() {
        return isFolia;
    }

    @Override
    public String getServerVersion() {
        return serverVersion;
    }

    // Method to asynchronously check for plugin updates
    public CompletableFuture<Boolean> checkUpdate() {
        CompletableFuture<Boolean> updateFuture = new CompletableFuture<>();
        plugin.getScheduler().runTaskAsync(() -> {
            try {
                URL url = new URL("https://api.polymart.org/v1/getResourceInfoSimple/?resource_id=2543&key=version");
                URLConnection conn = url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                InputStream inputStream = conn.getInputStream();
                String newest = new BufferedReader(new InputStreamReader(inputStream)).readLine();
                String current = getPluginVersion();
                inputStream.close();
                if (!compareVer(newest, current)) {
                    updateFuture.complete(false);
                    return;
                }
                isLatest = false;
                updateFuture.complete(true);
            } catch (Exception exception) {
                LogUtils.warn("Error occurred when checking update.");
                updateFuture.completeExceptionally(exception);
            }
        });
        return updateFuture;
    }

    // Method to compare two version strings
    // return true when update is available
    private boolean compareVer(String newV, String currentV) {
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

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (player.isOp()) {
            if (!PlaceholderAPI.isRegistered("player")) {
                AdventureManagerImpl.getInstance().sendMessageWithPrefix(player, "You haven't installed Player Expansion yet. Click <b><gold><click:run_command:/papi ecloud download Player>HERE</click></gold><b> to download.");
            }
        }
    }
}