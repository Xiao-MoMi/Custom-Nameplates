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
import net.momirealms.customnameplates.object.actionbar.ActionBarConfig;
import net.momirealms.customnameplates.object.actionbar.ActionBarTask;
import net.momirealms.customnameplates.utils.AdventureUtils;
import net.momirealms.customnameplates.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ActionBarManager extends Function {

    private final LinkedHashMap<String, ActionBarConfig> actionBarConfigMap;
    private final ConcurrentHashMap<UUID, ActionBarTask> actionBarTaskMap;
    private final JoinQuitListener joinQuitListener;
    private final CustomNameplates plugin;

    public ActionBarManager(CustomNameplates plugin) {
        this.plugin = plugin;
        this.actionBarConfigMap = new LinkedHashMap<>();
        this.actionBarTaskMap = new ConcurrentHashMap<>();
        this.joinQuitListener = new JoinQuitListener(this);
    }

    @Override
    public void load() {
        if (!ConfigManager.enableActionBar) return;
        this.loadConfig();
        Bukkit.getPluginManager().registerEvents(joinQuitListener, plugin);
        for (Player player : Bukkit.getOnlinePlayers()) {
            onJoin(player);
        }
    }

    @Override
    public void unload() {
        for (ActionBarTask actionBarTask : actionBarTaskMap.values()) {
            actionBarTask.stop();
        }
        actionBarConfigMap.clear();
        HandlerList.unregisterAll(joinQuitListener);
    }

    @Override
    public void onJoin(Player player) {
        ActionBarTask actionBarTask = new ActionBarTask(player, actionBarConfigMap.values().toArray(new ActionBarConfig[0]));
        actionBarTaskMap.put(player.getUniqueId(), actionBarTask);
        actionBarTask.start();
    }

    @Override
    public void onQuit(Player player) {
        ActionBarTask actionBarTask = actionBarTaskMap.remove(player.getUniqueId());
        if (actionBarTask != null) actionBarTask.stop();
    }

    private void loadConfig() {
        YamlConfiguration config = ConfigUtils.getConfig("configs" + File.separator + "actionbar.yml");
        for (String key : config.getKeys(false)) {
            ConfigurationSection actionBarSection = config.getConfigurationSection(key);
            if (actionBarSection == null) continue;
            actionBarConfigMap.put(key, new ActionBarConfig(
                    actionBarSection.getInt("switch-interval", 15) * 20,
                    actionBarSection.getString("text") == null ? actionBarSection.getStringList("dynamic-text").toArray(new String[0]) : new String[]{actionBarSection.getString("text")},
                    ConfigUtils.getRequirements(actionBarSection.getConfigurationSection("conditions"))
            ));
        }
        AdventureUtils.consoleMessage("[CustomNameplates] Loaded <green>" + actionBarConfigMap.size() + " <gray>actionbars");
    }
}
