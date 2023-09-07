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
import net.momirealms.customnameplates.object.Function;
import net.momirealms.customnameplates.object.SimpleChar;
import net.momirealms.customnameplates.utils.AdventureUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class ImageManager extends Function {

    private final HashMap<String, SimpleChar> characterMap;
    private final CustomNameplates plugin;

    public ImageManager(CustomNameplates plugin) {
        this.plugin = plugin;
        this.characterMap = new HashMap<>();
    }

    @Override
    public void load() {
        if (!ConfigManager.enableImages) return;
        loadImages();
    }

    @Override
    public void unload() {
        characterMap.clear();
    }

    private void loadImages() {
        File img_file = new File(plugin.getDataFolder(), "contents" + File.separator + "images");
        if (!img_file.exists() && img_file.mkdirs()) {
            saveDefaultImages();
        }
        File[] image_config_files = img_file.listFiles(file -> file.getName().endsWith(".yml"));
        if (image_config_files == null) return;
        Arrays.sort(image_config_files, Comparator.comparing(File::getName));
        for (File image_config_file : image_config_files) {
            char img = ConfigManager.start_char;
            ConfigManager.start_char = (char) (img + '\u0001');
            String key = image_config_file.getName().substring(0, image_config_file.getName().length() - 4);
            YamlConfiguration config = YamlConfiguration.loadConfiguration(image_config_file);
            if (!config.contains("height")) config.set("height", 10);
            if (!config.contains("ascent")) config.set("ascent", 8);
            if (!config.contains("width")) config.set("width", 10);
            if (!config.contains("image")) config.set("image", key);
            try {
                config.save(image_config_file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            SimpleChar simpleChar = new SimpleChar(config.getInt("height"), config.getInt("ascent"), config.getInt("width"), img, config.getString("image") + ".png");
            characterMap.put(key, simpleChar);
        }
        AdventureUtils.consoleMessage("[CustomNameplates] Loaded <green>" + characterMap.size() + " <gray>images");
    }

    private void saveDefaultImages() {
        String[] png_list = new String[]{"bell", "bubble", "clock", "coin", "compass", "weather", "stamina_0", "stamina_1", "stamina_2"};
        String[] part_list = new String[]{".png", ".yml"};
        for (String name : png_list) {
            for (String part : part_list) {
                plugin.saveResource("contents" + File.separator + "images" + File.separator + name + part, false);
            }
        }
    }

    @Nullable
    public SimpleChar getImage(String key) {
        return characterMap.get(key);
    }

    public HashMap<String, SimpleChar> getCharacterMap() {
        return characterMap;
    }
}
