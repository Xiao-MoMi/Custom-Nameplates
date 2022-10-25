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

import net.momirealms.customnameplates.objects.Function;
import net.momirealms.customnameplates.objects.actionbar.ActionBarConfig;
import net.momirealms.customnameplates.objects.actionbar.ActionBarTask;
import net.momirealms.customnameplates.objects.requirements.*;
import net.momirealms.customnameplates.utils.AdventureUtil;
import net.momirealms.customnameplates.utils.ConfigUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ActionBarManager extends Function {

    private final HashSet<ActionBarTask> tasks;
    public HashMap<String, ActionBarConfig> actionBars;

    public ActionBarManager() {
        this.tasks = new HashSet<>();
        this.actionBars = new HashMap<>();
    }

    @Override
    public void load() {
        if (!ConfigUtil.isModuleEnabled("actionbar")) return;
        loadConfig();
        for (ActionBarConfig config : actionBars.values()) {
            tasks.add(new ActionBarTask(config));
        }
    }

    @Override
    public void unload() {
        for (ActionBarTask task : tasks) {
            task.stop();
        }
        tasks.clear();
    }

    private void loadConfig() {
        actionBars.clear();
        YamlConfiguration config = ConfigUtil.getConfig("actionbar.yml");
        if (config.contains("actionbar")) {
            for (String key : config.getConfigurationSection("actionbar").getKeys(false)) {
                String[] texts;
                String text = config.getString("actionbar." + key + ".text");
                if (text != null) {
                    texts = new String[]{text};
                }
                else {
                    List<String> strings = config.getStringList("actionbar." + key + ".dynamic-text");
                    texts = strings.toArray(new String[0]);
                }
                List<Requirement> requirements = new ArrayList<>();
                if (config.contains("actionbar." + key + ".conditions")){
                    config.getConfigurationSection("actionbar." + key + ".conditions").getKeys(false).forEach(requirement -> {
                        switch (requirement){
                            case "weather" -> requirements.add(new Weather(config.getStringList("actionbar." + key + ".conditions.weather")));
                            case "ypos" -> requirements.add(new YPos(config.getStringList("actionbar." + key + ".conditions.ypos")));
                            case "world" -> requirements.add(new World(config.getStringList("actionbar." + key + ".conditions.world")));
                            case "biome" -> requirements.add(new Biome(config.getStringList("actionbar." + key + ".conditions.biome")));
                            case "permission" -> requirements.add(new Permission(config.getString("actionbar." + key + ".conditions.permission")));
                            case "time" -> requirements.add(new Time(config.getStringList("actionbar." + key + ".conditions.time")));
                            case "papi-condition" -> requirements.add(new CustomPapi(config.getConfigurationSection("actionbar." + key + ".conditions.papi-condition").getValues(false)));
                        }
                    });
                }
                ActionBarConfig actionBarConfig = new ActionBarConfig(
                        config.getInt("actionbar." + key + ".refresh-rate", 5) - 1,
                        config.getInt("actionbar." + key + ".switch-interval", 15) * 20,
                        texts,
                        requirements
                );
                actionBars.put(key, actionBarConfig);
            }
            AdventureUtil.consoleMessage("[CustomNameplates] Loaded <green>" + actionBars.size() + " <gray>actionbars");
        }
    }
}
