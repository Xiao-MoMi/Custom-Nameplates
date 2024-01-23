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

package net.momirealms.customnameplates.paper.mechanic.background;

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.BackGroundManager;
import net.momirealms.customnameplates.api.mechanic.background.BackGround;
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

public class BackGroundManagerImpl implements BackGroundManager {

    private final HashMap<String, BackGround> backGroundMap;
    private final CustomNameplatesPlugin plugin;

    public BackGroundManagerImpl(CustomNameplatesPlugin plugin) {
        this.plugin = plugin;
        this.backGroundMap = new HashMap<>();
    }

    public void reload() {
        unload();
        load();
    }

    public void load() {
        if (!CNConfig.backgroundModule) return;
        this.loadConfigs();
    }

    public void unload() {
        this.backGroundMap.clear();
    }

    private void loadConfigs() {
        File bgFolder = new File(plugin.getDataFolder(),"contents" + File.separator + "backgrounds");
        if (!bgFolder.exists() && bgFolder.mkdirs()) {
            saveDefaultBackgrounds();
        }
        File[] bgConfigFiles = bgFolder.listFiles(file -> file.getName().endsWith(".yml"));
        if (bgConfigFiles == null) return;
        Arrays.sort(bgConfigFiles, Comparator.comparing(File::getName));
        for (File bgConfigFile : bgConfigFiles) {
            String key = bgConfigFile.getName().substring(0, bgConfigFile.getName().length() - 4);

            YamlConfiguration config = YamlConfiguration.loadConfiguration(bgConfigFile);
            int height = config.getInt("middle.height", 14);
            int ascent = config.getInt("middle.ascent", 8);
            if (config.contains("middle.descent")) ascent = height - config.getInt("middle.descent");

            var background = BackGround.builder()
                    .leftMargin(config.getInt("left-margin", 1))
                    .rightMargin(config.getInt("right-margin", 1))
                    .left(
                            ConfiguredChar.builder()
                                    .character(CharacterArranger.getAndIncrease())
                                    .png(config.getString("left.image"))
                                    .height(config.getInt("left.height"))
                                    .ascent(config.getInt("left.ascent"))
                                    .width(config.getInt("left.width"))
                                    .build()
                    )
                    .offset_1(
                            ConfiguredChar.builder()
                                    .character(CharacterArranger.getAndIncrease())
                                    .png(config.getString("middle.1"))
                                    .height(height)
                                    .ascent(ascent)
                                    .width(1)
                                    .build()
                    )
                    .offset_2(
                            ConfiguredChar.builder()
                                    .character(CharacterArranger.getAndIncrease())
                                    .png(config.getString("middle.2"))
                                    .height(height)
                                    .ascent(ascent)
                                    .width(2)
                                    .build()
                    )
                    .offset_4(
                            ConfiguredChar.builder()
                                    .character(CharacterArranger.getAndIncrease())
                                    .png(config.getString("middle.4"))
                                    .height(height)
                                    .ascent(ascent)
                                    .width(4)
                                    .build()
                    )
                    .offset_8(
                            ConfiguredChar.builder()
                                    .character(CharacterArranger.getAndIncrease())
                                    .png(config.getString("middle.8"))
                                    .height(height)
                                    .ascent(ascent)
                                    .width(8)
                                    .build()
                    )
                    .offset_16(
                            ConfiguredChar.builder()
                                    .character(CharacterArranger.getAndIncrease())
                                    .png(config.getString("middle.16"))
                                    .height(height)
                                    .ascent(ascent)
                                    .width(16)
                                    .build()
                    )
                    .offset_32(
                            ConfiguredChar.builder()
                                    .character(CharacterArranger.getAndIncrease())
                                    .png(config.getString("middle.32"))
                                    .height(height)
                                    .ascent(ascent)
                                    .width(32)
                                    .build()
                    )
                    .offset_64(
                            ConfiguredChar.builder()
                                    .character(CharacterArranger.getAndIncrease())
                                    .png(config.getString("middle.64"))
                                    .height(height)
                                    .ascent(ascent)
                                    .width(64)
                                    .build()
                    )
                    .offset_128(
                            ConfiguredChar.builder()
                                    .character(CharacterArranger.getAndIncrease())
                                    .png(config.getString("middle.128"))
                                    .height(height)
                                    .ascent(ascent)
                                    .width(128)
                                    .build()
                    )
                    .right(
                            ConfiguredChar.builder()
                                    .character(CharacterArranger.getAndIncrease())
                                    .png(config.getString("right.image"))
                                    .height(config.getInt("right.height"))
                                    .ascent(config.getInt("right.ascent"))
                                    .width(config.getInt("right.width"))
                                    .build()
                    )
                    .build();

            if (!registerBackGround(key, background)) {
                LogUtils.warn("Found duplicated background: " + key);
            }
        }
    }

    @Override
    public BackGround getBackGround(@NotNull String key) {
        return backGroundMap.get(key);
    }

    @Override
    public Collection<BackGround> getBackGrounds() {
        return backGroundMap.values();
    }

    @Override
    public boolean registerBackGround(@NotNull String key, @NotNull BackGround backGround) {
        if (backGroundMap.containsKey(key)) {
            return false;
        }
        backGroundMap.put(key, backGround);
        return true;
    }

    @Override
    public boolean unregisterBackGround(@NotNull String key) {
        return backGroundMap.remove(key) != null;
    }

    private void saveDefaultBackgrounds() {
        String[] bg_list = new String[]{"b0", "b1", "b2", "b4", "b8", "b16","b32","b64","b128"};
        for (String bg : bg_list) {
            plugin.saveResource("contents" + File.separator + "backgrounds" + File.separator + bg + ".png", false);
        }
        String[] config_list = new String[]{"bedrock_1", "bedrock_2"};
        for (String config : config_list) {
            plugin.saveResource("contents" + File.separator + "backgrounds" + File.separator + config + ".yml", false);
        }
    }
}
