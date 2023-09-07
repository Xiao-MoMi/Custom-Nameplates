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
import net.momirealms.customnameplates.object.background.BackGroundConfig;
import net.momirealms.customnameplates.object.font.OffsetFont;
import net.momirealms.customnameplates.object.placeholders.BackGroundText;
import net.momirealms.customnameplates.utils.AdventureUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class BackgroundManager extends Function {

    private final CustomNameplates plugin;
    private final HashMap<String, BackGroundConfig> backGroundConfigMap;

    public BackgroundManager(CustomNameplates plugin) {
        this.plugin = plugin;
        this.backGroundConfigMap = new HashMap<>();
    }

    @Override
    public void load() {
        if (!ConfigManager.enableBackground) return;
        loadConfig();
    }

    @Override
    public void unload() {
        this.backGroundConfigMap.clear();
    }

    private void loadConfig() {
        File bg_file = new File(plugin.getDataFolder(),"contents" + File.separator + "backgrounds");
        if (!bg_file.exists() && bg_file.mkdirs()) {
            saveDefaultBackgrounds();
        }
        File[] bg_config_files = bg_file.listFiles(file -> file.getName().endsWith(".yml"));
        if (bg_config_files == null) return;
        Arrays.sort(bg_config_files, Comparator.comparing(File::getName));
        for (File bg_config_file : bg_config_files) {
            String key = bg_config_file.getName().substring(0, bg_config_file.getName().length() - 4);
            YamlConfiguration config = YamlConfiguration.loadConfiguration(bg_config_file);
            char oStart = ConfigManager.start_char;
            char o1 = (char) (oStart + '\u0001'); char o2 = (char) (o1 + '\u0001');
            char o4 = (char) (o2 + '\u0001'); char o8 = (char) (o4 + '\u0001');
            char o16 = (char) (o8 + '\u0001'); char o32 = (char) (o16 + '\u0001');
            char o64 = (char) (o32 + '\u0001');  char o128 = (char) (o64 + '\u0001');
            char oEnd = (char) (o128 + '\u0001'); ConfigManager.start_char = (char) (oEnd + '\u0001');
            int height = config.getInt("middle.height", 14);
            int ascent = config.getInt("middle.ascent", 8);
            SimpleChar startChar = new SimpleChar(config.getInt("left.height"), config.getInt("left.ascent"),1 ,oStart, config.getString("left.image") + ".png");
            SimpleChar offset_1 = new SimpleChar(height, ascent,1, o1, config.getString("middle.1") + ".png");
            SimpleChar offset_2 = new SimpleChar(height, ascent,1, o2, config.getString("middle.2") + ".png");
            SimpleChar offset_4 = new SimpleChar(height, ascent,1, o4, config.getString("middle.4") + ".png");
            SimpleChar offset_8 = new SimpleChar(height, ascent,1, o8, config.getString("middle.8") + ".png");
            SimpleChar offset_16 = new SimpleChar(height, ascent,1, o16, config.getString("middle.16") + ".png");
            SimpleChar offset_32 = new SimpleChar(height, ascent,1, o32, config.getString("middle.32") + ".png");
            SimpleChar offset_64 = new SimpleChar(height, ascent,1, o64, config.getString("middle.64") + ".png");
            SimpleChar offset_128 = new SimpleChar(height, ascent,1, o128, config.getString("middle.128") + ".png");
            SimpleChar endChar = new SimpleChar(config.getInt("right.height"),config.getInt("right.ascent"),1, oEnd, config.getString("right.image") + ".png");
            backGroundConfigMap.put(key, new BackGroundConfig(startChar, offset_1,
                    offset_2, offset_4,
                    offset_8, offset_16,
                    offset_32, offset_64,
                    offset_128, endChar,
                    config.getInt("left-margin", 1), config.getInt("right-margin", 1)));
            plugin.getFontManager().loadCustomWidth(o1, 1);
            plugin.getFontManager().loadCustomWidth(o2, 2);
            plugin.getFontManager().loadCustomWidth(o4, 4);
            plugin.getFontManager().loadCustomWidth(o8, 8);
            plugin.getFontManager().loadCustomWidth(o16, 16);
            plugin.getFontManager().loadCustomWidth(o32, 32);
            plugin.getFontManager().loadCustomWidth(o64, 64);
            plugin.getFontManager().loadCustomWidth(o128, 128);
            plugin.getFontManager().loadCustomWidth(oStart, config.getInt("left.width", 1));
            plugin.getFontManager().loadCustomWidth(oEnd, config.getInt("right.width", 1));
        }
        AdventureUtils.consoleMessage("[CustomNameplates] Loaded <green>" + backGroundConfigMap.size() + " <gray>backgrounds");
    }

    public HashMap<String, BackGroundConfig> getBackGroundConfigMap() {
        return backGroundConfigMap;
    }

    public String getBackGroundImage(BackGroundText backGroundText, String text) {
        BackGroundConfig backGroundConfig = backGroundConfigMap.get(backGroundText.background());
        if (backGroundConfig == null) return backGroundText.background() + " NOT FOUND";
        return getBackGroundImage(backGroundConfig, text);
    }

    public String getBackGroundImage(BackGroundConfig backGroundConfig, String text) {
        int n = plugin.getFontManager().getTotalWidth(text);
        String offset = plugin.getFontManager().getShortestNegChars(n + backGroundConfig.right_margin() + 2);
        n = n + backGroundConfig.left_margin() + backGroundConfig.right_margin() + 2;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(backGroundConfig.left().getChars());
        while (n >= 128) {
            stringBuilder.append(OffsetFont.NEG_1.getCharacter());
            stringBuilder.append(backGroundConfig.offset_128().getChars());
            n -= 128;
        }
        if (n - 64 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.getCharacter());
            stringBuilder.append(backGroundConfig.offset_64().getChars());
            n -= 64;
        }
        if (n - 32 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.getCharacter());
            stringBuilder.append(backGroundConfig.offset_32().getChars());
            n -= 32;
        }
        if (n - 16 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.getCharacter());
            stringBuilder.append(backGroundConfig.offset_16().getChars());
            n -= 16;
        }
        if (n - 8 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.getCharacter());
            stringBuilder.append(backGroundConfig.offset_8().getChars());
            n -= 8;
        }
        if (n - 4 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.getCharacter());
            stringBuilder.append(backGroundConfig.offset_4().getChars());
            n -= 4;
        }
        if (n - 2 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.getCharacter());
            stringBuilder.append(backGroundConfig.offset_2().getChars());
            n -= 2;
        }
        if (n - 1 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.getCharacter());
            stringBuilder.append(backGroundConfig.offset_1().getChars());
        }
        stringBuilder.append(OffsetFont.NEG_1.getCharacter());
        stringBuilder.append(backGroundConfig.right().getChars()).append(offset);
        return stringBuilder.toString();
    }

    private void saveDefaultBackgrounds() {
        String[] bg_list = new String[]{"b0", "b1", "b2", "b4", "b8", "b16","b32","b64","b128"};
        for (String bg : bg_list) {
            plugin.saveResource("contents" + File.separator + "backgrounds" + File.separator + bg + ".png", false);
        }
        String[] config_list = new String[]{"bedrock_1", "bedrock_2", "bedrock_3"};
        for (String config : config_list) {
            plugin.saveResource("contents" + File.separator + "backgrounds" + File.separator + config + ".yml", false);
        }
    }
}
