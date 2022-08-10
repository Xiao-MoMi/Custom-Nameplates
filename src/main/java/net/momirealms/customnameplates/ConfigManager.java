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

package net.momirealms.customnameplates;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.momirealms.customnameplates.background.BackGround;
import net.momirealms.customnameplates.bossbar.adventure.BossBarConfigA;
import net.momirealms.customnameplates.bossbar.protocollib.BossBarConfigP;
import net.momirealms.customnameplates.bossbar.protocollib.Overlay;
import net.momirealms.customnameplates.helper.Log;
import net.momirealms.customnameplates.hook.Placeholders;
import net.momirealms.customnameplates.utils.BGInfo;
import net.momirealms.customnameplates.utils.NPInfo;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

public class ConfigManager {

    public static TreeMap<String, BackGround> backgrounds = new TreeMap<>();
    public static TreeMap<String, BossBarConfigA> bossbarsA = new TreeMap<>();
    public static TreeMap<String, BossBarConfigP> bossbarsP = new TreeMap<>();
    public static HashMap<String, BGInfo> papiBG = new HashMap<>();
    public static HashMap<String, NPInfo> papiNP = new HashMap<>();
    public static HashMap<Character, Integer> fontWidth = new HashMap<>();
    private static Placeholders placeholders;

    public static YamlConfiguration getConfig(String configName) {
        File file = new File(CustomNameplates.instance.getDataFolder(), configName);
        if (!file.exists()) {
            CustomNameplates.instance.saveResource(configName, false);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public static boolean nameplate;
    public static boolean background;
    public static boolean bossbar;
    public static boolean actionbar;
    public static boolean useAdventure;
    public static void loadModule(){
        YamlConfiguration module = getConfig("module.yml");
        nameplate = module.getBoolean("nameplate");
        background = module.getBoolean("background");
        bossbar = module.getBoolean("bossbar");
        actionbar = module.getBoolean("actionbar");
        YamlConfiguration bossbarmode = getConfig("bossbar.yml");
        useAdventure =  bossbarmode.getString("mode").equalsIgnoreCase("Adventure");
    }


    public static void loadWidth(){

        fontWidth.clear();
        YamlConfiguration config = getConfig("char-width.yml");
        config.getConfigurationSection("").getKeys(false).forEach(key -> {
            fontWidth.put(key.charAt(0), config.getInt(key));
        });
        AdventureManager.consoleMessage("<gradient:#2E8B57:#48D1CC>[CustomNameplates]</gradient> <color:#baffd1>Loaded <white>" + fontWidth.size() + " <color:#baffd1>custom char width");
    }

    public static class MainConfig{
        public static String namespace;
        public static String fontName;
        public static String start_char;
        public static char start;
        public static String folder_path;
        public static String bg_folder_path;
        public static String ss_folder_path;
        public static String font;
        public static String default_nameplate;
        public static String player_prefix;
        public static String player_suffix;
        public static Key key;
        public static boolean itemsAdder;
        public static boolean placeholderAPI;
        public static boolean show_after;
        public static String lang;
        public static Long preview;
        public static boolean thin_font;
        public static boolean hidePrefix;
        public static boolean hideSuffix;
        public static boolean anotherFont;
        public static boolean tab;
        public static boolean oraxen;
        public static int fontOffset;
        public static void ReloadConfig(){

            try {
                YamlDocument.create(new File(CustomNameplates.instance.getDataFolder(), "config.yml"), CustomNameplates.instance.getResource("config.yml"), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());
            }catch (IOException e){
                Log.warn(e.getMessage());
            }

            CustomNameplates.instance.saveDefaultConfig();
            CustomNameplates.instance.reloadConfig();
            FileConfiguration config = CustomNameplates.instance.getConfig();
            lang = config.getString("config.lang");
            namespace = config.getString("config.namespace");
            font = config.getString("config.font");
            fontName = namespace + ":" + font;
            start_char = config.getString("config.start-char");
            start = start_char.charAt(0);
            folder_path = config.getString("config.nameplate-folder-path","font\\nameplates\\");
            bg_folder_path = config.getString("config.background-folder-path","font\\backgrounds\\");
            ss_folder_path = config.getString("config.space-split-folder-path","font\\");
            default_nameplate = config.getString("config.default-nameplate");
            player_prefix = config.getString("config.prefix");
            player_suffix = config.getString("config.suffix");
            anotherFont = config.getBoolean("config.another-ascii-font.enable",true);
            if(config.getBoolean("config.extract-shader",true)){
                CustomNameplates.instance.saveResource("shaders.zip", true);
            }

            itemsAdder = config.getBoolean("config.integrations.ItemsAdder",false);
            fontOffset = config.getInt("config.another-ascii-font.y-offset",3);
            if (itemsAdder){
                if(CustomNameplates.instance.getServer().getPluginManager().getPlugin("ItemsAdder") == null){
                    CustomNameplates.instance.getLogger().warning("Failed to initialize ItemsAdder!");
                    itemsAdder = false;
                }
            }
            placeholderAPI = config.getBoolean("config.integrations.PlaceholderAPI",false);
            if (placeholderAPI){
                if(CustomNameplates.instance.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
                    loadPapi();
                    if (placeholders != null){
                        placeholders.unregister();
                    }
                    placeholders = new Placeholders();
                    placeholders.register();
                    AdventureManager.consoleMessage("<gradient:#2E8B57:#48D1CC>[CustomNameplates]</gradient> <color:#baffd1>PlaceholderAPI Hooked!");
                }else {
                    CustomNameplates.instance.getLogger().warning("Failed to initialize PlaceholderAPI!");
                    placeholderAPI = false;
                }
            }
            tab = config.getBoolean("config.integrations.TAB",false);
            if (tab){
                if(CustomNameplates.instance.getServer().getPluginManager().getPlugin("TAB") == null){
                    tab = false;
                }
            }
            oraxen = config.getBoolean("config.integrations.Oraxen",false);
            if (oraxen){
                if(CustomNameplates.instance.getServer().getPluginManager().getPlugin("Oraxen") == null){
                    oraxen = false;
                }
            }

            show_after = config.getBoolean("config.show-after-load-resourcepack");
            key = Key.key(fontName);
            preview = config.getLong("config.preview-duration");
            thin_font = config.getBoolean("config.use-thin-font",false);
            hidePrefix = config.getBoolean("config.hide-prefix-when-equipped",false);
            hideSuffix = config.getBoolean("config.hide-suffix-when-equipped",false);
        }
    }

    public static class Message{
        public static String noPerm;
        public static String prefix;
        public static String lackArgs;
        public static String reload;
        public static String equip;
        public static String unequip;
        public static String force_equip;
        public static String force_unequip;
        public static String not_exist;
        public static String not_online;
        public static String no_console;
        public static String notAvailable;
        public static String available;
        public static String cooldown;
        public static String preview;
        public static String generate;
        public static void ReloadConfig(){
            YamlConfiguration messagesConfig = getConfig("messages/messages_" + MainConfig.lang +".yml");
            noPerm = messagesConfig.getString("messages.no-perm");
            prefix = messagesConfig.getString("messages.prefix");
            lackArgs = messagesConfig.getString("messages.lack-args");
            reload = messagesConfig.getString("messages.reload");
            equip = messagesConfig.getString("messages.equip");
            unequip = messagesConfig.getString("messages.unequip");
            force_equip = messagesConfig.getString("messages.force-equip");
            force_unequip = messagesConfig.getString("messages.force-unequip");
            not_exist = messagesConfig.getString("messages.not-exist");
            not_online = messagesConfig.getString("messages.not-online");
            no_console = messagesConfig.getString("messages.no-console");
            notAvailable = messagesConfig.getString("messages.not-available");
            available = messagesConfig.getString("messages.available");
            cooldown = messagesConfig.getString("messages.cooldown");
            preview = messagesConfig.getString("messages.preview");
            generate = messagesConfig.getString("messages.generate");
        }
    }

    public static void loadBGConfig(){
        backgrounds.clear();
        YamlConfiguration bgConfig = getConfig("background.yml");
        bgConfig.getConfigurationSection("background").getKeys(false).forEach(key -> {
            backgrounds.put(key, new BackGround(key, bgConfig.getString("background." + key + ".start"),bgConfig.getString("background." + key + ".offset_1"),
                    bgConfig.getString("background." + key + ".offset_2"),bgConfig.getString("background." + key + ".offset_4"),bgConfig.getString("background." + key + ".offset_8"),
                    bgConfig.getString("background." + key + ".offset_16"),bgConfig.getString("background." + key + ".offset_32"),bgConfig.getString("background." + key + ".offset_64"),
                    bgConfig.getString("background." + key + ".offset_128"),bgConfig.getString("background." + key + ".end"),bgConfig.getInt("background." + key + ".y-offset"),bgConfig.getInt("background." + key + ".x-offset")
            ));
        });
    }

    public static void loadBossBar(){
        YamlConfiguration config = getConfig("bossbar.yml");
        if (useAdventure){
            config.getConfigurationSection("bossbar").getKeys(false).forEach(key -> {
                BossBarConfigA bossbarConfig = ConfigManager.bossbarsA.get(key);
                if (bossbarConfig != null){
                    bossbarConfig.setColor(BossBar.Color.valueOf(config.getString("bossbar." + key + ".color").toUpperCase()));
                    bossbarConfig.setOverlay(BossBar.Overlay.valueOf(config.getString("bossbar." + key + ".overlay").toUpperCase()));
                    bossbarConfig.setRate(config.getInt("bossbar." + key + ".refresh-rate") - 1);
                    bossbarConfig.setText(config.getString("bossbar." + key + ".text"));
                }else {
                    bossbarsA.put(key, new BossBarConfigA(
                            config.getString("bossbar." + key + ".text"),
                            BossBar.Overlay.valueOf(config.getString("bossbar." + key + ".overlay").toUpperCase()),
                            BossBar.Color.valueOf(config.getString("bossbar." + key + ".color").toUpperCase()),
                            config.getInt("bossbar." + key + ".refresh-rate") - 1
                    ));
                }
            });
        }else {
            config.getConfigurationSection("bossbar").getKeys(false).forEach(key -> {
                BossBarConfigP bossbarConfig = ConfigManager.bossbarsP.get(key);
                if (bossbarConfig != null){
                    bossbarConfig.setColor(BarColor.valueOf(config.getString("bossbar."+key+".color").toUpperCase()));
                    bossbarConfig.setRate(config.getInt("bossbar." + key + ".refresh-rate") - 1);
                    bossbarConfig.setText(config.getString("bossbar." + key + ".text"));
                    bossbarConfig.setOverlay(Overlay.valueOf(config.getString("bossbar."+key+".overlay").toUpperCase()));
                }else {
                    bossbarsP.put(key, new BossBarConfigP(
                            config.getString("bossbar." + key + ".text"),
                            Overlay.valueOf(config.getString("bossbar."+key+".overlay").toUpperCase()),
                            BarColor.valueOf(config.getString("bossbar."+key+".color").toUpperCase()),
                            config.getInt("bossbar." + key + ".refresh-rate") - 1
                    ));
                }
            });
        }
    }

    public static void loadPapi(){
        papiBG.clear();
        papiNP.clear();
        YamlConfiguration papiInfo = getConfig("custom-papi.yml");
        papiInfo.getConfigurationSection("papi").getKeys(false).forEach(key -> {
            if (papiInfo.contains("papi." + key + ".background")){
                papiBG.put(key, new BGInfo(papiInfo.getString("papi."+key+".text"), papiInfo.getString("papi." + key + ".background")));
            }
            if (papiInfo.contains("papi." + key + ".nameplate")){
                papiNP.put(key, new NPInfo(papiInfo.getString("papi."+key+".text"), papiInfo.getString("papi." + key + ".nameplate")));
            }
        });
    }

    public static class ActionbarConfig{
        public static int rate;
        public static String text;
        public static void LoadConfig(){
            YamlConfiguration actionbarConfig = getConfig("actionbar.yml");
            rate = actionbarConfig.getInt("refresh-rate") - 1;
            text = actionbarConfig.getString("text");
        }
    }

    public static class DatabaseConfig{
        public static String user;
        public static String password;
        public static String url;
        public static String ENCODING;
        public static String tableName;
        public static boolean enable_pool;
        public static boolean use_mysql;
        public static boolean async;
        public static int maximum_pool_size;
        public static int minimum_idle;
        public static int maximum_lifetime;
        public static int idle_timeout;
        public static void LoadConfig(){
            YamlConfiguration databaseConfig = getConfig("database.yml");
            String storage_mode = databaseConfig.getString("settings.storage-mode");
            async = !databaseConfig.getBoolean("settings.disable-async", true);
            if(storage_mode.equals("SQLite")){
                enable_pool = false;
                use_mysql = false;
                tableName = "nameplates";
            }
            else if(storage_mode.equals("MYSQL")){
                use_mysql = true;
                ENCODING = databaseConfig.getString("MySQL.property.encoding");
                tableName = databaseConfig.getString("MySQL.table-name");
                user = databaseConfig.getString("MySQL.user");
                password = databaseConfig.getString("MySQL.password");
                url = "jdbc:mysql://" + databaseConfig.getString("MySQL.host")
                        + ":" + databaseConfig.getString("MySQL.port") + "/"
                        + databaseConfig.getString("MySQL.database") + "?characterEncoding="
                        + ENCODING + "&useSSL="
                        + databaseConfig.getString("MySQL.property.use-ssl");
                if (databaseConfig.getString("MySQL.property.timezone") != null &&
                        !databaseConfig.getString("MySQL.property.timezone").equals("")) {
                    url = url + "&serverTimezone=" + databaseConfig.getString("MySQL.property.timezone");
                }
                if (databaseConfig.getBoolean("MySQL.property.allowPublicKeyRetrieval")) {
                    url = url + "&allowPublicKeyRetrieval=true";
                }
                enable_pool = databaseConfig.getBoolean("settings.use-pool");
                if(enable_pool){
                    maximum_pool_size = databaseConfig.getInt("Pool-Settings.maximum-pool-size");
                    minimum_idle = databaseConfig.getInt("Pool-Settings.minimum-idle");
                    maximum_lifetime = databaseConfig.getInt("Pool-Settings.maximum-lifetime");
                    idle_timeout = databaseConfig.getInt("Pool-Settings.idle-timeout");
                }
            }else {
                AdventureManager.consoleMessage("<red>[CustomNameplates] Error! No such storage mode!</red>");
                Bukkit.getPluginManager().disablePlugin(CustomNameplates.instance);
            }
        }
    }
}