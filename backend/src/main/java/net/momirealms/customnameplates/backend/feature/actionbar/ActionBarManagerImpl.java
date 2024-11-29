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

package net.momirealms.customnameplates.backend.feature.actionbar;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.CarouselText;
import net.momirealms.customnameplates.api.feature.JoinQuitListener;
import net.momirealms.customnameplates.api.feature.actionbar.ActionBarConfig;
import net.momirealms.customnameplates.api.feature.actionbar.ActionBarManager;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.api.util.ConfigUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ActionBarManagerImpl implements ActionBarManager, JoinQuitListener {

    protected final CustomNameplates plugin;
    private final LinkedHashMap<String, ActionBarConfig> configs = new LinkedHashMap<>();
    private final ConcurrentHashMap<UUID, ActionBarSender> senders = new ConcurrentHashMap<>();
    private ActionBarConfig[] configArray = new ActionBarConfig[0];

    public ActionBarManagerImpl(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        // ignore disabled modules
        if (!ConfigManager.actionbarModule()) return;
        this.loadConfig();
        this.resetArray();
        for (CNPlayer online : plugin.getOnlinePlayers()) {
            onPlayerJoin(online);
        }
    }

    @Override
    public void unload() {
        for (ActionBarSender sender : senders.values()) {
            sender.destroy();
        }
        this.senders.clear();
        this.configs.clear();
        this.resetArray();
    }

    @Override
    public void refreshConditions() {
        for (ActionBarSender sender : senders.values()) {
            sender.onConditionTimerCheck();
        }
    }

    @Override
    public void checkHeartBeats() {
        for (ActionBarSender sender : senders.values()) {
            sender.onHeartBeatTimer();
        }
    }

    private void resetArray() {
        configArray = configs.values().toArray(new ActionBarConfig[0]);
    }

    @Override
    public ActionBarConfig configById(String name) {
        return configs.get(name);
    }

    @Override
    public ActionBarConfig[] actionBarConfigs() {
        return configArray;
    }

    public void handleActionBarPacket(CNPlayer player, String miniMessage) {
        ActionBarSender sender = senders.get(player.uuid());
        if (sender != null) {
            if (miniMessage.length() >= 1024) return;
            sender.externalActionBar(miniMessage);
        }
    }

    public void temporarilyHideCustomActionBar(CNPlayer player) {
        ActionBarSender sender = senders.get(player.uuid());
        if (sender != null) {
            sender.temporarilyHide();
        }
    }

    @Override
    public @Nullable String getExternalActionBar(CNPlayer player) {
        ActionBarSender sender = senders.get(player.uuid());
        if (sender != null) {
            return sender.externalActionBar();
        }
        return null;
    }

    @Override
    public void onPlayerJoin(CNPlayer player) {
        if (!ConfigManager.actionbarModule()) return;
        Runnable r = () -> {
            if (!player.isOnline()) return;
            ActionBarSender sender = new ActionBarSender(this, player);
            ActionBarSender previous = senders.put(player.uuid(), sender);
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
        ActionBarSender sender = senders.remove(player.uuid());
        if (sender != null) {
            sender.destroy();
        }
    }

    private void loadConfig() {
        plugin.getConfigManager().saveResource("configs" + File.separator + "actionbar.yml");
        YamlDocument document = plugin.getConfigManager().loadData(new File(plugin.getDataDirectory().toFile(), "configs" + File.separator + "actionbar.yml"));
        for (Map.Entry<String, Object> entry : document.getStringRouteMappedValues(false).entrySet()) {
            if (!(entry.getValue() instanceof Section section))
                return;
            this.configs.put(entry.getKey(),
                    ActionBarConfig.builder()
                            .id(entry.getKey())
                            .requirement(plugin.getRequirementManager().parseRequirements(section.getSection("conditions")))
                            .carouselText(
                                section.contains("text") ?
                                new CarouselText[]{new CarouselText(-1, new Requirement[0], section.getString("text"), false)} :
                                ConfigUtils.carouselTexts(section.getSection("text-display-order"))
                            )
                            .build()
            );
        }
    }
}
