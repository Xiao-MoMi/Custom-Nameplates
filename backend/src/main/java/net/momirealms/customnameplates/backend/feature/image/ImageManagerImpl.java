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

package net.momirealms.customnameplates.backend.feature.image;

import dev.dejvokep.boostedyaml.YamlDocument;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.ConfiguredCharacter;
import net.momirealms.customnameplates.api.feature.image.Animation;
import net.momirealms.customnameplates.api.feature.image.Image;
import net.momirealms.customnameplates.api.feature.image.ImageManager;
import net.momirealms.customnameplates.api.util.ConfigUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ImageManagerImpl implements ImageManager {

    private final CustomNameplates plugin;
    private final HashMap<String, Image> images = new HashMap<>();

    public ImageManagerImpl(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    @Override
    public void unload() {
        this.images.clear();
    }

    @Override
    public void load() {
        if (!ConfigManager.imageModule()) return;
        this.loadConfigs();
    }

    @Override
    public @Nullable Image imageById(String id) {
        return images.get(id);
    }

    @Override
    public Collection<Image> images() {
        return new ObjectArrayList<>(images.values());
    }

    private void loadConfigs() {
        File imageFolder = new File(plugin.getDataDirectory().toFile(), "contents" + File.separator + "images");
        if (!imageFolder.exists() && imageFolder.mkdirs()) {
            saveDefaultImages();
        }
        List<File> configFiles = ConfigUtils.getConfigsDeeply(imageFolder);
        for (File configFile : configFiles) {
            YamlDocument config = plugin.getConfigManager().loadData(configFile);
            String id = configFile.getName().substring(0, configFile.getName().lastIndexOf("."));
            Image image = Image.builder()
                    .id(id)
                    .animation(config.contains("animation") ? new Animation(
                            config.getInt("animation.speed", 64),
                            config.getInt("animation.frames", 1)
                    ) : null)
                    .removeShadow(!config.getBoolean("shadow", true))
                    .character(ConfiguredCharacter.create(
                                    ConfigUtils.getFileInTheSameFolder(configFile, config.getString("image") + ".png"),
                                    config.getInt("ascent", 8),
                                    config.getInt("height", 10)
                    ))
                    .build();
            this.images.put(id, image);
        }
    }

    private void saveDefaultImages() {
        String[] png_list = new String[]{"bell", "bubble", "clock", "coin", "compass", "weather", "stamina_0", "stamina_1", "stamina_2"};
        String[] part_list = new String[]{".png", ".yml"};
        for (String name : png_list) {
            for (String part : part_list) {
                plugin.getConfigManager().saveResource("contents" + File.separator + "images" + File.separator + name + part);
            }
        }
    }
}
