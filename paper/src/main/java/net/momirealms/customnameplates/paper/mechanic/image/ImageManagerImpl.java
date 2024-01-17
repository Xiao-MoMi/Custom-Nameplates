package net.momirealms.customnameplates.paper.mechanic.image;

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.ImageManager;
import net.momirealms.customnameplates.api.mechanic.character.CharacterArranger;
import net.momirealms.customnameplates.api.mechanic.character.ConfiguredChar;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.paper.setting.CNConfig;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;

public class ImageManagerImpl implements ImageManager {

    private final HashMap<String, ConfiguredChar> imageMap;
    private final CustomNameplatesPlugin plugin;

    public ImageManagerImpl(CustomNameplatesPlugin plugin) {
        this.plugin = plugin;
        this.imageMap = new HashMap<>();
    }

    public void load() {
        if (!CNConfig.imageModule) return;
        loadConfigs();
    }

    public void unload() {
        this.imageMap.clear();
    }

    public void reload() {
        unload();
        loadConfigs();
    }

    @Override
    public ConfiguredChar getImage(@NotNull String key) {
        return imageMap.get(key);
    }

    @Override
    public Collection<ConfiguredChar> getImages() {
        return imageMap.values();
    }

    private void loadConfigs() {
        File imgFolder = new File(plugin.getDataFolder(), "contents" + File.separator + "images");
        if (!imgFolder.exists() && imgFolder.mkdirs()) {
            saveDefaultImages();
        }
        File[] configFiles = imgFolder.listFiles(file -> file.getName().endsWith(".yml"));
        if (configFiles == null) return;
        Arrays.sort(configFiles, Comparator.comparing(File::getName));
        for (File configFile : configFiles) {

            String key = configFile.getName().substring(0, configFile.getName().length() - 4);
            YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

            if (!registerImage(key,
                    ConfiguredChar.builder()
                        .character(CharacterArranger.getAndIncrease())
                        .png(config.getString("image", key))
                        .height(config.getInt("height", 8))
                        .width(config.getInt("width", 8))
                        .ascent(config.getInt("ascent", 8))
                        .build()
            )) {
                LogUtils.warn("Found duplicated image: " + key);
            }
        }
    }

    @Override
    public boolean registerImage(@NotNull String key, @NotNull ConfiguredChar configuredChar) {
        if (imageMap.containsKey(key)) return false;
        imageMap.put(key, configuredChar);
        return true;
    }

    @Override
    public boolean unregisterImage(@NotNull String key) {
        return this.imageMap.remove(key) != null;
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
}
