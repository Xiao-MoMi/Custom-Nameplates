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

package net.momirealms.customnameplates.manager;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.listener.JoinQuitListener;
import net.momirealms.customnameplates.object.Function;
import net.momirealms.customnameplates.object.bossbar.BossBarConfig;
import net.momirealms.customnameplates.object.bossbar.BossBarTask;
import net.momirealms.customnameplates.object.bossbar.Overlay;
import net.momirealms.customnameplates.utils.AdventureUtils;
import net.momirealms.customnameplates.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BossBarManager extends Function {

    private final LinkedHashMap<String, BossBarConfig> bossBars;
    private final ConcurrentHashMap<UUID, BossBarTask> bossBarTaskMap;
    private final JoinQuitListener joinQuitListener;
    private final CustomNameplates plugin;

    public BossBarManager(CustomNameplates plugin) {
        this.plugin = plugin;
        this.joinQuitListener = new JoinQuitListener(this);
        this.bossBarTaskMap = new ConcurrentHashMap<>();
        this.bossBars = new LinkedHashMap<>();
    }

    @Override
    public void load() {
        if (!ConfigManager.enableBossBar) return;
        this.loadConfig();
        Bukkit.getPluginManager().registerEvents(joinQuitListener, plugin);
        for (Player player : Bukkit.getOnlinePlayers()) {
            onJoin(player);
        }
    }

    @Override
    public void unload() {
        for (BossBarTask bossBarTask : bossBarTaskMap.values()) {
            bossBarTask.stop();
        }
        bossBarTaskMap.clear();
        HandlerList.unregisterAll(joinQuitListener);
    }

    public void onJoin(Player player) {
        BossBarTask bossBarTask = new BossBarTask(player, bossBars.values().toArray(new BossBarConfig[0]));
        bossBarTaskMap.put(player.getUniqueId(), bossBarTask);
        bossBarTask.start();
    }

    public void onQuit(Player player) {
        BossBarTask bossBarTask = bossBarTaskMap.remove(player.getUniqueId());
        if (bossBarTask != null) bossBarTask.stop();
    }

    private void loadConfig() {
        bossBars.clear();
        YamlConfiguration config = ConfigUtils.getConfig("configs" + File.separator + "bossbar.yml");
        for (String key : config.getKeys(false)) {
            ConfigurationSection bossBarSection = config.getConfigurationSection(key);
            if (bossBarSection == null) continue;
            bossBars.put(key, new BossBarConfig(
                    bossBarSection.getString("text") == null ? bossBarSection.getStringList("dynamic-text").toArray(new String[0]) : new String[]{bossBarSection.getString("text")},
                    Overlay.valueOf(bossBarSection.getString("overlay","progress").toUpperCase(Locale.ENGLISH)),
                    BarColor.valueOf(bossBarSection.getString("color","white").toUpperCase(Locale.ENGLISH)),
                    bossBarSection.getInt("switch-interval", 5) * 20,
                    ConfigUtils.getRequirements(bossBarSection.getConfigurationSection("conditions"))
            ));
        }
        AdventureUtils.consoleMessage("[CustomNameplates] Loaded <green>" + bossBars.size() + " <gray>bossbars");
    }

    public LinkedHashMap<String, BossBarConfig> getBossBars() {
        return bossBars;
    }
}
