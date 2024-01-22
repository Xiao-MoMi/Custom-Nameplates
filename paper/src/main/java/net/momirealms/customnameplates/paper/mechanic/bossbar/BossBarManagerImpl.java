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

package net.momirealms.customnameplates.paper.mechanic.bossbar;

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.BossBarManager;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.common.Pair;
import net.momirealms.customnameplates.paper.mechanic.misc.DisplayController;
import net.momirealms.customnameplates.paper.setting.CNConfig;
import net.momirealms.customnameplates.paper.util.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class BossBarManagerImpl implements BossBarManager, Listener {

    private final CustomNameplatesPlugin plugin;
    private final ConcurrentHashMap<UUID, BossBarReceiver> receiverMap;
    private BossBarConfig[] configs;

    public void load() {
        if (!CNConfig.bossBarModule) return;
        this.loadConfigs();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        for (Player player : Bukkit.getOnlinePlayers()) {
            createBossBarFor(player);
        }
    }

    public void unload() {
        HandlerList.unregisterAll(this);
        for (BossBarReceiver receiver : receiverMap.values()) {
            receiver.cancelTask();
            receiver.destroy();
        }
        receiverMap.clear();
    }

    public void reload() {
        unload();
        load();
    }

    public BossBarManagerImpl(CustomNameplatesPlugin plugin) {
        this.plugin = plugin;
        this.receiverMap = new ConcurrentHashMap<>();
        this.configs = new BossBarConfig[0];
    }

    private void loadConfigs() {
        ArrayList<BossBarConfig> configs = new ArrayList<>();

        YamlConfiguration config = plugin.getConfig("configs" + File.separator + "bossbar.yml");
        for (Map.Entry<String, Object> barEntry : config.getValues(false).entrySet()) {
            if (!(barEntry.getValue() instanceof ConfigurationSection section))
                return;

            var barConfig = BossBarConfig.Builder.of()
                    .barColor(BarColor.getColor(section.getString("color", "YELLOW")))
                    .overlay(Overlay.getOverlay(section.getString("overlay", "PROGRESS")))
                    .checkFrequency(section.getInt("check-frequency", 10))
                    .requirement(plugin.getRequirementManager().getRequirements(section.getConfigurationSection("conditions")))
                    .displayOrder(ConfigUtils.getTimeLimitTexts(section.getConfigurationSection("text-display-order")))
                    .build();

            configs.add(barConfig);
        }

        this.configs = configs.toArray(new BossBarConfig[0]);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (CNConfig.sendDelay == 0) {
            createBossBarFor(player);
            return;
        }
        this.plugin.getScheduler().runTaskAsyncLater(() -> {
            createBossBarFor(player);
        }, CNConfig.sendDelay * 50L, TimeUnit.MILLISECONDS);
    }

    private void createBossBarFor(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        Pair<DisplayController, BossBar>[] pairs = new Pair[configs.length];
        for (int i = 0; i < configs.length; i++) {
            var config = configs[i];
            pairs[i] = Pair.of(
                    new DisplayController(player, config.getCheckFrequency(), config.getRequirements(), config.getTextDisplayOrder()),
                    new BossBar(player, config.getOverlay(), config.getBarColor())
            );
        }

        BossBarReceiver bossBarReceiver = new BossBarReceiver(plugin, player, pairs);
        bossBarReceiver.arrangeTask();
        this.putReceiverToMap(player.getUniqueId(), bossBarReceiver);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        BossBarReceiver receiver = receiverMap.remove(event.getPlayer().getUniqueId());
        if (receiver != null) {
            receiver.cancelTask();
        }
    }

    private void putReceiverToMap(UUID uuid, BossBarReceiver bossBarReceiver) {
        BossBarReceiver previous = this.receiverMap.put(uuid, bossBarReceiver);
        if (previous != null) {
            LogUtils.warn("Unexpected error: Duplicated bossbar created");
            previous.cancelTask();
        }
    }

    private BossBarReceiver getReceiver(UUID uuid) {
        return this.receiverMap.get(uuid);
    }
}
