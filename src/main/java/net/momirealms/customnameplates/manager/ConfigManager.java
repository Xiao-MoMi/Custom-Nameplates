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

import net.momirealms.customnameplates.object.Function;
import net.momirealms.customnameplates.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigManager extends Function {

    public static String namespace;
    public static String font;
    public static String lang;
    public static String nameplates_folder_path;
    public static String backgrounds_folder_path;
    public static String space_split_folder_path;
    public static String bubbles_folder_path;
    public static String images_folder_path;
    public static boolean itemsAdderHook;
    public static boolean thin_font;
    public static boolean tab_hook;
    public static boolean tab_BC_hook;
    public static boolean oraxenHook;
    public static boolean trChat_Hook;
    public static boolean ventureChat_Hook;
    public static boolean extractShader;
    public static boolean extractBars;
    public static char start_char;
    public static boolean checkUpdate;
    public static boolean enableBStats;
    public static boolean enableNameplates;
    public static boolean enableBossBar;
    public static boolean enableActionBar;
    public static boolean enableBubbles;
    public static boolean enableBackground;
    public static boolean enableImages;
    public static int default_width;
    public static boolean enable1_20_Unicode;

    @Override
    public void load(){
        ConfigUtils.update("config.yml");
        YamlConfiguration config = ConfigUtils.getConfig("config.yml");
        lang = config.getString("lang");
        enableBStats = config.getBoolean("config.metrics", true);
        checkUpdate = config.getBoolean("update-checker", true);
        loadModules(config);
        loadIntegrations(config);
        loadResourcePack(config);
        loadOtherSettings(config);
    }

    private void loadIntegrations(ConfigurationSection config) {
        ConfigurationSection section = config.getConfigurationSection("integrations");
        if (section != null) {
            tab_hook = section.getBoolean("TAB",false) && Bukkit.getPluginManager().isPluginEnabled("TAB");
            tab_BC_hook = section.getBoolean("TAB-BC",false);
            trChat_Hook = section.getBoolean("TrChat",false) && Bukkit.getPluginManager().isPluginEnabled("TrChat");
            itemsAdderHook = section.getBoolean("ItemsAdder",false) && Bukkit.getPluginManager().isPluginEnabled("ItemsAdder");
            oraxenHook = section.getBoolean("Oraxen",false) && Bukkit.getPluginManager().isPluginEnabled("Oraxen");
            ventureChat_Hook = section.getBoolean("VentureChat",false) && Bukkit.getPluginManager().isPluginEnabled("VentureChat");
        }
    }

    private void loadResourcePack(ConfigurationSection config) {
        ConfigurationSection section = config.getConfigurationSection("resource-pack");
        if (section != null) {
            namespace = section.getString("namespace", "nameplates");
            font = section.getString("font", "default");
            start_char = section.getString("config.left-char", "뀁").charAt(0);
            nameplates_folder_path = section.getString("image-path.nameplates","font\\nameplates\\");
            bubbles_folder_path = section.getString("image-path.bubbles","font\\bubbles\\");
            backgrounds_folder_path = section.getString("image-path.backgrounds","font\\backgrounds\\");
            space_split_folder_path = section.getString("image-path.space-split","font\\base\\");
            images_folder_path = section.getString("image-path.images","font\\images\\");
            extractShader = section.getBoolean("extract-shader",true);
            extractBars = section.getBoolean("extract-bar-image",true);
            enable1_20_Unicode = section.getBoolean("support-1_20-unicodes",false);
        }
    }

    private void loadModules(ConfigurationSection config) {
        ConfigurationSection section = config.getConfigurationSection("modules");
        if (section != null) {
            enableNameplates = section.getBoolean("nameplates");
            enableBossBar = section.getBoolean("bossbars");
            enableActionBar = section.getBoolean("actionbars");
            enableBubbles = section.getBoolean("bubbles");
            enableBackground = section.getBoolean("backgrounds");
            enableImages = section.getBoolean("images");
        }
    }

    private void loadOtherSettings(ConfigurationSection config) {
        ConfigurationSection section = config.getConfigurationSection("other-settings");
        if (section != null) {
            default_width = section.getInt("default-character-width", 8);

        }
        if (enableNameplates) {
            YamlConfiguration np_config = ConfigUtils.getConfig("configs" + File.separator + "nameplate.yml");
            thin_font = np_config.getBoolean("thin-font", false);
        }
        else {
            thin_font = false;
        }
    }

    public static String getMiniMessageFontTag() {
        return "<font:" + namespace + ":" + font + ">";
    }

    public static String getFontTagCloser() {
        return "</font>";
    }

    public static String surroundWithFont(String text) {
        return getMiniMessageFontTag() + text + getFontTagCloser();
    }
}
