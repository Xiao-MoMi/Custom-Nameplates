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

package net.momirealms.customnameplates.backend.feature.bossbar;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.CarouselText;
import net.momirealms.customnameplates.api.feature.JoinQuitListener;
import net.momirealms.customnameplates.api.feature.bossbar.BossBar;
import net.momirealms.customnameplates.api.feature.bossbar.BossBarConfig;
import net.momirealms.customnameplates.api.feature.bossbar.BossBarManager;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.api.util.ConfigUtils;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class BossBarManagerImpl implements BossBarManager, JoinQuitListener {

    private final CustomNameplates plugin;
    private final LinkedHashMap<String, BossBarConfig> configs = new LinkedHashMap<>();
    private final ConcurrentHashMap<UUID, BossBarDisplayController> senders = new ConcurrentHashMap<>();
    private BossBarConfig[] configArray = new BossBarConfig[0];

    public BossBarManagerImpl(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        // ignore disabled modules
        if (!ConfigManager.bossbarModule()) return;
        this.loadConfig();
        this.resetArray();
        for (CNPlayer online : plugin.getOnlinePlayers()) {
            onPlayerJoin(online);
        }
    }

    @Override
    public void unload() {
        for (BossBarDisplayController sender : senders.values()) {
            sender.destroy();
        }
        this.senders.clear();
        this.configs.clear();
        this.resetArray();
    }

    @Override
    public void onTick() {
        for (BossBarDisplayController sender : senders.values()) {
            sender.onTick();
        }
    }

    private void resetArray() {
        configArray = configs.values().toArray(new BossBarConfig[0]);
    }

    @Override
    public void onPlayerJoin(CNPlayer player) {
        if (!ConfigManager.bossbarModule()) return;
        Runnable r = () -> {
            if (!player.isOnline()) return;
            BossBarDisplayController sender = new BossBarDisplayController(this, player);
            BossBarDisplayController previous = senders.put(player.uuid(), sender);
            if (previous != null) {
                previous.destroy();
            }
        };
        if (ConfigManager.delaySend() < 0) {
            r.run();
        } else {
            plugin.getScheduler().asyncLater(r, ConfigManager.delaySend() * 50L, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void onPlayerQuit(CNPlayer player) {
        BossBarDisplayController sender = senders.remove(player.uuid());
        if (sender != null) {
            sender.destroy();
        }
    }

    @Override
    public BossBarConfig configById(String name) {
        return configs.get(name);
    }

    @Override
    public BossBarConfig[] bossBarConfigs() {
        return configArray;
    }

    private void loadConfig() {
        plugin.getConfigManager().saveResource("configs" + File.separator + "bossbar.yml");
        YamlDocument document = plugin.getConfigManager().loadData(new File(plugin.getDataDirectory().toFile(), "configs" + File.separator + "bossbar.yml"));
        for (Map.Entry<String, Object> entry : document.getStringRouteMappedValues(false).entrySet()) {
            if (!(entry.getValue() instanceof Section section))
                return;
            this.configs.put(entry.getKey(),
                    BossBarConfig.builder()
                            .id(entry.getKey())
                            .overlay(BossBar.Overlay.valueOf(section.getString("overlay", "PROGRESS").toUpperCase(Locale.ENGLISH)))
                            .color(BossBar.Color.valueOf(section.getString("color", "YELLOW").toUpperCase(Locale.ENGLISH)))
                            .requirement(plugin.getRequirementManager().parseRequirements(section.getSection("conditions")))
                            .carouselText(
                                section.contains("text") ?
                                new CarouselText[]{new CarouselText(-1, new Requirement[0], section.getString("text"), false)} :
                                ConfigUtils.carouselTexts(section.getSection("text-display-order"))
                            )
                            .progress(section.getFloat("progress", 0f))
                            .build()
            );
        }
    }
}
