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
import net.momirealms.customnameplates.listener.SimpleListener;
import net.momirealms.customnameplates.objects.Function;
import net.momirealms.customnameplates.objects.bossbar.BossBarConfig;
import net.momirealms.customnameplates.objects.bossbar.Overlay;
import net.momirealms.customnameplates.objects.bossbar.TimerTaskP;
import net.momirealms.customnameplates.objects.requirements.*;
import net.momirealms.customnameplates.utils.AdventureUtil;
import net.momirealms.customnameplates.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.*;

public class BossBarManager extends Function {

    private final SimpleListener simpleListener;
    private final LinkedHashMap<String, BossBarConfig> bossBars;
    private final HashMap<Player, TimerTaskP> taskCache;

    public BossBarManager() {
        this.simpleListener = new SimpleListener(this);
        this.taskCache = new HashMap<>();
        this.bossBars = new LinkedHashMap<>();
    }

    @Override
    public void load() {
        if (!ConfigUtil.isModuleEnabled("bossbar")) return;
        loadConfig();
        for (Player player : Bukkit.getOnlinePlayers()) {
            taskCache.put(player, new TimerTaskP(player));
        }
        Bukkit.getPluginManager().registerEvents(simpleListener, CustomNameplates.plugin);
    }

    @Override
    public void unload() {
        for (TimerTaskP timerTask : taskCache.values()) {
            timerTask.stopTimer();
        }
        taskCache.clear();
        HandlerList.unregisterAll(simpleListener);
    }

    public void onJoin(Player player) {
        taskCache.put(player, new TimerTaskP(player));
    }

    public void onQuit(Player player) {
        TimerTaskP timerTask = taskCache.remove(player);
        if (timerTask != null){
            timerTask.stopTimer();
        }
    }

    private void loadConfig() {
        bossBars.clear();
        YamlConfiguration config = ConfigUtil.getConfig("bossbar.yml");
        config.getConfigurationSection("bossbar").getKeys(false).forEach(key -> {
            String[] texts;
            String text = config.getString("bossbar." + key + ".text");
            if (text != null) {
                texts = new String[]{text};
            }
            else {
                List<String> strings = config.getStringList("bossbar." + key + ".dynamic-text");
                texts = strings.toArray(new String[0]);
            }
            List<Requirement> requirements = new ArrayList<>();
            if (config.contains("bossbar." + key + ".conditions")){
                config.getConfigurationSection("bossbar." + key + ".conditions").getKeys(false).forEach(requirement -> {
                    switch (requirement){
                        case "weather" -> requirements.add(new Weather(config.getStringList("bossbar." + key + ".conditions.weather")));
                        case "ypos" -> requirements.add(new YPos(config.getStringList("bossbar." + key + ".conditions.ypos")));
                        case "world" -> requirements.add(new World(config.getStringList("bossbar." + key + ".conditions.world")));
                        case "biome" -> requirements.add(new Biome(config.getStringList("bossbar." + key + ".conditions.biome")));
                        case "permission" -> requirements.add(new Permission(config.getString("bossbar." + key + ".conditions.permission")));
                        case "time" -> requirements.add(new Time(config.getStringList("bossbar." + key + ".conditions.time")));
                        case "papi-condition" -> requirements.add(new CustomPapi(config.getConfigurationSection("bossbar." + key + ".conditions.papi-condition").getValues(false)));
                    }
                });
            }
            BossBarConfig bossBarConfig = new BossBarConfig(
                    texts,
                    Overlay.valueOf(config.getString("bossbar."+key+".overlay","progress").toUpperCase()),
                    BarColor.valueOf(config.getString("bossbar."+key+".color","white").toUpperCase()),
                    config.getInt("bossbar." + key + ".refresh-rate", 15) - 1,
                    config.getInt("bossbar." + key + ".switch-interval", 5) * 20,
                    requirements
            );
            bossBars.put(key, bossBarConfig);
        });
        AdventureUtil.consoleMessage("[CustomNameplates] Loaded <green>" + bossBars.size() + " <gray>bossbars");
    }

    public LinkedHashMap<String, BossBarConfig> getBossBars() {
        return bossBars;
    }
}
