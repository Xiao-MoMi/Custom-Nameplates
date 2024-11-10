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

package net.momirealms.customnameplates.backend.feature.background;

import dev.dejvokep.boostedyaml.YamlDocument;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.ConfiguredCharacter;
import net.momirealms.customnameplates.api.feature.background.Background;
import net.momirealms.customnameplates.api.feature.background.BackgroundManager;
import net.momirealms.customnameplates.api.util.ConfigUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackgroundManagerImpl implements BackgroundManager {

    private final CustomNameplates plugin;
    private final Map<String, Background> backgrounds = new HashMap<>();

    public BackgroundManagerImpl(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    @Override
    public void unload() {
        this.backgrounds.clear();
    }

    @Override
    public void load() {
        if (!ConfigManager.backgroundModule()) return;
        this.loadConfigs();
    }

    @Nullable
    @Override
    public Background backgroundById(String id) {
        return this.backgrounds.get(id);
    }

    @Override
    public Collection<Background> getBackgrounds() {
        return new ObjectArrayList<>(backgrounds.values());
    }

    private void loadConfigs() {
        File backgroundFolder = new File(plugin.getDataDirectory().toFile(), "contents" + File.separator + "backgrounds");
        if (!backgroundFolder.exists() && backgroundFolder.mkdirs()) {
            saveDefaultBackgrounds();
        }
        List<File> configFiles = ConfigUtils.getConfigsDeeply(backgroundFolder);
        for (File configFile : configFiles) {
            YamlDocument config = plugin.getConfigManager().loadData(configFile);
            String id = configFile.getName().substring(0, configFile.getName().lastIndexOf("."));
            int height = config.getInt("middle.height", 14);
            int ascent = config.getInt("middle.ascent", 8);
            Background background = Background.builder()
                    .id(id)
                    .left(ConfiguredCharacter.create(
                            ConfigUtils.getFileInTheSameFolder(configFile, config.getString("left.image") + ".png"),
                            config.getInt("left.ascent", 8),
                            config.getInt("left.height", 14)
                    ))
                    .right(ConfiguredCharacter.create(
                            ConfigUtils.getFileInTheSameFolder(configFile, config.getString("right.image") + ".png"),
                            config.getInt("right.ascent", 8),
                            config.getInt("right.height", 14)
                    ))
                    .width_1(ConfiguredCharacter.create(
                            ConfigUtils.getFileInTheSameFolder(configFile, config.getString("middle.1") + ".png"),
                            ascent, height
                    ))
                    .width_2(ConfiguredCharacter.create(
                            ConfigUtils.getFileInTheSameFolder(configFile, config.getString("middle.2") + ".png"),
                            ascent, height
                    ))
                    .width_4(ConfiguredCharacter.create(
                            ConfigUtils.getFileInTheSameFolder(configFile, config.getString("middle.4") + ".png"),
                            ascent, height
                    ))
                    .width_8(ConfiguredCharacter.create(
                            ConfigUtils.getFileInTheSameFolder(configFile, config.getString("middle.8") + ".png"),
                            ascent, height
                    ))
                    .width_16(ConfiguredCharacter.create(
                            ConfigUtils.getFileInTheSameFolder(configFile, config.getString("middle.16") + ".png"),
                            ascent, height
                    ))
                    .width_32(ConfiguredCharacter.create(
                            ConfigUtils.getFileInTheSameFolder(configFile, config.getString("middle.32") + ".png"),
                            ascent, height
                    ))
                    .width_64(ConfiguredCharacter.create(
                            ConfigUtils.getFileInTheSameFolder(configFile, config.getString("middle.64") + ".png"),
                            ascent, height
                    ))
                    .width_128(ConfiguredCharacter.create(
                            ConfigUtils.getFileInTheSameFolder(configFile, config.getString("middle.128") + ".png"),
                            ascent, height
                    ))
                    .build();
            this.backgrounds.put(id, background);
        }
    }

    private void saveDefaultBackgrounds() {
        String[] bg_list = new String[]{"b0", "b1", "b2", "b4", "b8", "b16","b32","b64","b128"};
        for (String bg : bg_list) {
            plugin.getConfigManager().saveResource("contents" + File.separator + "backgrounds" + File.separator + bg + ".png");
        }
        String[] config_list = new String[]{"bedrock_1", "bedrock_2"};
        for (String config : config_list) {
            plugin.getConfigManager().saveResource("contents" + File.separator + "backgrounds" + File.separator + config + ".yml");
        }
    }
}
