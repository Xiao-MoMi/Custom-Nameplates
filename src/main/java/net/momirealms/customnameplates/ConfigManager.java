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

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.momirealms.customnameplates.objects.BackGround;
import net.momirealms.customnameplates.bossbar.adventure.BossBarConfigA;
import net.momirealms.customnameplates.bossbar.protocollib.BossBarConfigP;
import net.momirealms.customnameplates.bossbar.protocollib.Overlay;
import net.momirealms.customnameplates.font.FontWidthNormal;
import net.momirealms.customnameplates.font.FontWidthThin;
import net.momirealms.customnameplates.utils.AdventureUtil;
import net.momirealms.customnameplates.objects.BGInfo;
import net.momirealms.customnameplates.objects.NPInfo;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class ConfigManager {

    public static TreeMap<String, BackGround> backgrounds = new TreeMap<>();
    public static TreeMap<String, BossBarConfigA> bossbarsA = new TreeMap<>();
    public static TreeMap<String, BossBarConfigP> bossbarsP = new TreeMap<>();
    public static HashMap<String, BGInfo> papiBG = new HashMap<>();
    public static HashMap<String, NPInfo> papiNP = new HashMap<>();
    public static HashMap<Character, Integer> fontWidth = new HashMap<>();

    /**
     * 获取配置文件
     * @param configName 文件名
     * @return YAML配置
     */
    public static YamlConfiguration getConfig(String configName) {
        File file = new File(CustomNameplates.instance.getDataFolder(), configName);
        if (!file.exists()) {
            CustomNameplates.instance.saveResource(configName, false);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    /**
     * 载入模块
     */
    public static boolean nameplate;
    public static boolean background;
    public static boolean bossbar;
    public static boolean actionbar;
    public static boolean useAdventure;
    public static void loadModule(){
        YamlConfiguration module = getConfig("MODULES.yml");
        nameplate = module.getBoolean("nameplate", true);
        background = module.getBoolean("background", true);
        bossbar = module.getBoolean("bossbar", true);
        actionbar = module.getBoolean("actionbar", true);
    }

    /**
     * 载入主配置
     */
    public static class MainConfig{
        public static String namespace;
        public static String fontName;
        public static String start_char;
        public static char start;
        public static String folder_path;
        public static String bg_folder_path;
        public static String ss_folder_path;
        public static String font;
        public static List<Integer> offsets;
        public static Key key;
        public static boolean itemsAdder;
        public static boolean placeholderAPI;
        public static String lang;
        public static boolean thin_font;
        public static boolean tab;
        public static boolean oraxen;
        public static boolean extract;
        public static String version;

        public static void reload(){

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

            key = Key.key(fontName);
            thin_font = config.getBoolean("config.use-thin-font",false);
            itemsAdder = config.getBoolean("config.integrations.ItemsAdder",false);
            version = config.getString("config-version");
            placeholderAPI = config.getBoolean("config.integrations.PlaceholderAPI",false);
            tab = config.getBoolean("config.integrations.TAB",false);
            oraxen = config.getBoolean("config.integrations.Oraxen",false);
            offsets = config.getIntegerList("config.ascii-y-offset.offset");

            if(config.getBoolean("config.extract-shader",true)) {
                extract = true;
            }
            if (itemsAdder && CustomNameplates.instance.getServer().getPluginManager().getPlugin("ItemsAdder") == null){
                CustomNameplates.instance.getLogger().warning("Failed to initialize ItemsAdder!");
                itemsAdder = false;
            }
            if (placeholderAPI && CustomNameplates.instance.getServer().getPluginManager().getPlugin("PlaceholderAPI") == null){
                CustomNameplates.instance.getLogger().warning("Failed to initialize PlaceholderAPI!");
                placeholderAPI = false;
            }
            if (tab && CustomNameplates.instance.getServer().getPluginManager().getPlugin("TAB") == null){
                CustomNameplates.instance.getLogger().warning("Failed to initialize TAB!");
                tab = false;
            }
            if (oraxen && CustomNameplates.instance.getServer().getPluginManager().getPlugin("Oraxen") == null){
                CustomNameplates.instance.getLogger().warning("Failed to initialize Oraxen!");
                oraxen = false;
            }
        }
    }

    public static class Nameplate{

        public static String default_nameplate;
        public static String player_prefix;
        public static String player_suffix;
        public static boolean mode_team;
        public static long preview;
        public static boolean update;
        public static int refresh;
        public static boolean hidePrefix;
        public static boolean hideSuffix;
        public static boolean show_after;
        public static boolean tryHook;
        public static boolean removeTag;
        public static boolean smallSize;
        public static List<String> texts;
        public static void reload() {

            YamlConfiguration config = getConfig("nameplate.yml");
            default_nameplate = config.getString("nameplate.default-nameplate");
            preview = config.getLong("nameplate.preview-duration");
            show_after = config.getBoolean("nameplate.show-after-load-resourcepack");
            mode_team = config.getString("nameplate.mode","team").equalsIgnoreCase("team");
            update = config.getBoolean("nameplate.update.enable",false);
            refresh = config.getInt("nameplate.update.ticks",20);

            if (mode_team) {
                player_prefix = config.getString("nameplate.team.prefix","");
                player_suffix = config.getString("nameplate.team.suffix","");
                hidePrefix = config.getBoolean("nameplate.team.hide-prefix-when-equipped",false);
                hideSuffix = config.getBoolean("nameplate.team.hide-suffix-when-equipped",false);
            }
            else {
                player_prefix = "";
                player_suffix = "";
                removeTag = config.getBoolean("nameplate.entity.remove-nametag");
                tryHook = config.getBoolean("nameplate.entity.try-to-hook-cosmetics-plugin");
                texts = config.getStringList("nameplate.entity.text");
                smallSize = config.getBoolean("nameplate.entity.small-size", true);
            }
        }
    }

    /**
     * 载入消息文件
     */
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
        public static String noNameplate;

        public static void reload(){

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
            noNameplate = messagesConfig.getString("messages.no-nameplate","messages.no-nameplate is missing");
        }
    }

    /*
    载入背景配置
     */
    public static void loadBGConfig(){
        backgrounds.clear();
        YamlConfiguration bgConfig = getConfig("background.yml");
        bgConfig.getConfigurationSection("background").getKeys(false).forEach(key -> backgrounds.put(key, new BackGround(key, bgConfig.getString("background." + key + ".start"),bgConfig.getString("background." + key + ".offset_1"),
                bgConfig.getString("background." + key + ".offset_2"),bgConfig.getString("background." + key + ".offset_4"),bgConfig.getString("background." + key + ".offset_8"),
                bgConfig.getString("background." + key + ".offset_16"),bgConfig.getString("background." + key + ".offset_32"),bgConfig.getString("background." + key + ".offset_64"),
                bgConfig.getString("background." + key + ".offset_128"),bgConfig.getString("background." + key + ".end"),bgConfig.getInt("background." + key + ".y-offset",0),bgConfig.getInt("background." + key + ".x-offset"),bgConfig.getInt("background." + key + ".size",14)
        )));
    }

    /**
     * 载入bossbar配置
     */
    public static void loadBossBar() {
        YamlConfiguration config = getConfig("bossbar.yml");
        useAdventure =  config.getString("mode").equalsIgnoreCase("Adventure");
        if (useAdventure){
            config.getConfigurationSection("bossbar").getKeys(false).forEach(key -> {
                BossBarConfigA bossbarConfig = ConfigManager.bossbarsA.get(key);
                if (bossbarConfig != null) {
                    bossbarConfig.setColor(BossBar.Color.valueOf(config.getString("bossbar." + key + ".color").toUpperCase()));
                    bossbarConfig.setOverlay(BossBar.Overlay.valueOf(config.getString("bossbar." + key + ".overlay").toUpperCase()));
                    bossbarConfig.setRate(config.getInt("bossbar." + key + ".refresh-rate") - 1);
                    bossbarConfig.setText(config.getString("bossbar." + key + ".text"));
                }
                else {
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
                if (bossbarConfig != null) {
                    bossbarConfig.setColor(BarColor.valueOf(config.getString("bossbar."+key+".color").toUpperCase()));
                    bossbarConfig.setRate(config.getInt("bossbar." + key + ".refresh-rate") - 1);
                    bossbarConfig.setText(config.getString("bossbar." + key + ".text"));
                    bossbarConfig.setOverlay(Overlay.valueOf(config.getString("bossbar."+key+".overlay").toUpperCase()));
                }
                else {
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

    /**
     * 加载自定义的papi变量
     */
    public static void loadPapi() {
        papiBG.clear();
        papiNP.clear();
        YamlConfiguration papiInfo = getConfig("custom-papi.yml");
        papiInfo.getConfigurationSection("papi").getKeys(false).forEach(key -> {
            if (papiInfo.contains("papi." + key + ".background"))
                papiBG.put(key, new BGInfo(papiInfo.getString("papi."+key+".text"), papiInfo.getString("papi." + key + ".background")));
            if (papiInfo.contains("papi." + key + ".nameplate"))
                papiNP.put(key, new NPInfo(papiInfo.getString("papi."+key+".text"), papiInfo.getString("papi." + key + ".nameplate")));
        });
    }

    /**
     * 加载actionbar模块相关功能
     */
    public static class ActionbarConfig {
        public static int rate;
        public static String text;
        public static void load() {
            YamlConfiguration config = getConfig("actionbar.yml");
            rate = config.getInt("refresh-rate") - 1;
            text = config.getString("text");
        }
    }

    /**
     * 载入自定义宽度配置
     */
    public static void loadWidth() {
        fontWidth.clear();
        YamlConfiguration config = getConfig("char-width.yml");
        config.getConfigurationSection("").getKeys(false).forEach(key -> fontWidth.put(key.charAt(0), config.getInt(key)));
        AdventureUtil.consoleMessage("[CustomNameplates] Loaded <green>" + fontWidth.size() + " <gray>custom char-width");
        if (MainConfig.thin_font)
            for (int i = 0; i < FontWidthThin.values().length; i++)
                fontWidth.put(FontWidthThin.values()[i].getCharacter(), FontWidthThin.values()[i].getLength());
        else
            for (int i = 0; i < FontWidthNormal.values().length; i++)
                fontWidth.put(FontWidthNormal.values()[i].getCharacter(), FontWidthNormal.values()[i].getLength());
    }

    /**
     * 数据库设置
     */
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
            }
            else {
                AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! No such storage mode!</red>");
                Bukkit.getPluginManager().disablePlugin(CustomNameplates.instance);
            }
        }
    }
}