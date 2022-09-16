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

import net.kyori.adventure.key.Key;
import net.momirealms.customnameplates.bossbar.BossBarConfig;
import net.momirealms.customnameplates.bossbar.Overlay;
import net.momirealms.customnameplates.data.SqlHandler;
import net.momirealms.customnameplates.font.FontOffset;
import net.momirealms.customnameplates.font.FontWidthNormal;
import net.momirealms.customnameplates.font.FontWidthThin;
import net.momirealms.customnameplates.utils.AdventureUtil;
import net.momirealms.customnameplates.objects.BackGroundText;
import net.momirealms.customnameplates.objects.NameplateText;
import net.momirealms.customnameplates.utils.ConfigUtil;
import net.momirealms.customnameplates.utils.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

public class ConfigManager {

    public static TreeMap<String, BossBarConfig> bossBars = new TreeMap<>();
    public static HashMap<String, BackGroundText> papiBG = new HashMap<>();
    public static HashMap<String, NameplateText> papiNP = new HashMap<>();
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
    public static class Module{

        public static boolean nameplate;
        public static boolean background;
        public static boolean bossBar;
        public static boolean actionbar;
        public static boolean bubbles;

        public static void loadModule(){
            YamlConfiguration module = getConfig("MODULES.yml");
            nameplate = module.getBoolean("nameplate", true);
            background = module.getBoolean("background", true);
            bossBar = module.getBoolean("bossbar", true);
            actionbar = module.getBoolean("actionbar", true);
            bubbles = module.getBoolean("bubbles", false);
            try {
                Reflection.load();
                ConfigUtil.update();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 载入主配置
     */
    public static class Main {

        public static String namespace;
        public static String fontName;
        public static String start_char;
        public static String lang;
        public static String version;

        public static String np_folder_path;
        public static String bg_folder_path;
        public static String ss_folder_path;
        public static String bb_folder_path;
        public static String font;

        public static boolean itemsAdder;
        public static boolean placeholderAPI;

        public static boolean thin_font;
        public static boolean tab;
        public static boolean tab_bc;
        public static boolean oraxen;
        public static boolean extract;
        public static boolean trChat;
        public static List<Integer> offsets;
        public static char start;
        public static Key key;

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
            np_folder_path = config.getString("config.nameplate-folder-path","font\\nameplates\\");
            bb_folder_path = config.getString("config.bubble-folder-path","font\\bubbles\\");
            bg_folder_path = config.getString("config.background-folder-path","font\\backgrounds\\");
            ss_folder_path = config.getString("config.space-split-folder-path","font\\");
            key = Key.key(fontName);
            thin_font = config.getBoolean("config.use-thin-font",false);
            itemsAdder = config.getBoolean("config.integrations.ItemsAdder",false);
            version = config.getString("config-version");
            placeholderAPI = config.getBoolean("config.integrations.PlaceholderAPI",false);
            tab = config.getBoolean("config.integrations.TAB",false);
            tab_bc = config.getBoolean("config.integrations.TAB-BC",false);
            oraxen = config.getBoolean("config.integrations.Oraxen",false);
            trChat = config.getBoolean("config.integrations.TrChat",false);
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
            if ((tab && CustomNameplates.instance.getServer().getPluginManager().getPlugin("TAB") == null) || tab_bc){
                tab = false;
            }
            if (oraxen && CustomNameplates.instance.getServer().getPluginManager().getPlugin("Oraxen") == null){
                CustomNameplates.instance.getLogger().warning("Failed to initialize Oraxen!");
                oraxen = false;
            }
            if (trChat && CustomNameplates.instance.getServer().getPluginManager().getPlugin("TrChat") == null){
                CustomNameplates.instance.getLogger().warning("Failed to initialize TrChat!");
                trChat = false;
            }
        }
    }

    public static class Nameplate{

        public static String default_nameplate;
        public static String player_prefix;
        public static String player_suffix;
        public static long preview;
        public static boolean update;
        public static int refresh;
        public static String mode;
        public static boolean hidePrefix;
        public static boolean hideSuffix;
        public static boolean tryHook;
        public static boolean removeTag;
        public static boolean smallSize;
        public static boolean fakeTeam;
        public static HashMap<String, Double> textMap = new HashMap<>();

        public static void reload() {

            YamlConfiguration config = getConfig("nameplate.yml");
            default_nameplate = config.getString("nameplate.default-nameplate");
            preview = config.getLong("nameplate.preview-duration");
            mode = config.getString("nameplate.mode","team");
            update = config.getBoolean("nameplate.update.enable",true);
            fakeTeam = config.getBoolean("nameplate.create-fake-team",true);
            refresh = config.getInt("nameplate.update.ticks",20);
            player_prefix = config.getString("nameplate.prefix","");
            player_suffix = config.getString("nameplate.suffix","");
            hidePrefix = config.getBoolean("nameplate.team.hide-prefix-when-equipped",true);
            hideSuffix = config.getBoolean("nameplate.team.hide-suffix-when-equipped",true);
            if (mode.equalsIgnoreCase("team")) removeTag = false;
            else if (mode.equalsIgnoreCase("riding")) {
                tryHook = config.getBoolean("nameplate.riding.try-to-hook-cosmetics-plugin", false);
                List<String> texts = config.getStringList("nameplate.riding.text");
                textMap.clear();
                for (String text : texts) {
                    textMap.put(text, -0.1);
                }
                smallSize = config.getBoolean("nameplate.riding.small-height", true);
                removeTag = config.getBoolean("nameplate.riding.remove-nametag");
            }
            else if (mode.equalsIgnoreCase("teleporting")) {
                removeTag = config.getBoolean("nameplate.teleporting.remove-nametag");
                smallSize = config.getBoolean("nameplate.teleporting.small-height", true);
                textMap.clear();
                config.getConfigurationSection("nameplate.teleporting.text").getKeys(false).forEach(key -> {
                    textMap.put(config.getString("nameplate.teleporting.text." + key + ".content"), config.getDouble("nameplate.teleporting.text." + key + ".offset"));
                });
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
        public static String not_online;
        public static String no_console;
        public static String coolDown;
        public static String preview;
        public static String generate;
        public static String noNameplate;
        public static String generateDone;

        public static String np_equip;
        public static String np_unEquip;
        public static String np_force_equip;
        public static String np_force_unEquip;
        public static String np_not_exist;
        public static String np_notAvailable;
        public static String np_available;
        public static String np_haveNone;

        public static String bb_equip;
        public static String bb_unEquip;
        public static String bb_force_equip;
        public static String bb_force_unEquip;
        public static String bb_not_exist;
        public static String bb_notAvailable;
        public static String bb_available;
        public static String bb_haveNone;

        public static void reload(){

            YamlConfiguration messagesConfig = getConfig("messages" + File.separator + Main.lang +".yml");

            noPerm = messagesConfig.getString("messages.no-perm");
            prefix = messagesConfig.getString("messages.prefix");
            lackArgs = messagesConfig.getString("messages.lack-args");
            reload = messagesConfig.getString("messages.reload");
            coolDown = messagesConfig.getString("messages.cooldown");
            preview = messagesConfig.getString("messages.preview");
            generate = messagesConfig.getString("messages.generate");
            generateDone = messagesConfig.getString("messages.generate-done");
            noNameplate = messagesConfig.getString("messages.no-nameplate");
            not_online = messagesConfig.getString("messages.not-online");
            no_console = messagesConfig.getString("messages.no-console");

            np_equip = messagesConfig.getString("messages.equip-nameplates");
            np_unEquip = messagesConfig.getString("messages.unequip-nameplates");
            np_force_equip = messagesConfig.getString("messages.force-equip-nameplates");
            np_force_unEquip = messagesConfig.getString("messages.force-unequip-nameplates");
            np_not_exist = messagesConfig.getString("messages.not-exist-nameplates");
            np_notAvailable = messagesConfig.getString("messages.not-available-nameplates");
            np_available = messagesConfig.getString("messages.available-nameplates");
            np_haveNone = messagesConfig.getString("messages.have-no-nameplates");

            bb_equip = messagesConfig.getString("messages.equip-bubbles");
            bb_unEquip = messagesConfig.getString("messages.unequip-bubbles");
            bb_force_equip = messagesConfig.getString("messages.force-equip-bubbles");
            bb_force_unEquip = messagesConfig.getString("messages.force-unequip-bubbles");
            bb_not_exist = messagesConfig.getString("messages.not-exist-bubbles");
            bb_notAvailable = messagesConfig.getString("messages.not-available-bubbles");
            bb_available = messagesConfig.getString("messages.available-bubbles");
            bb_haveNone = messagesConfig.getString("messages.have-no-bubbles");
        }
    }

    /**
     * 载入BossBar配置
     */
    public static void loadBossBar() {
        bossBars.clear();
        YamlConfiguration config = getConfig("bossbar.yml");
        Objects.requireNonNull(config.getConfigurationSection("bossbar")).getKeys(false).forEach(key -> {
            String[] texts;
            String text = config.getString("bossbar." + key + ".text");
            if (text != null) {
                texts = new String[]{text};
            }
            else {
                List<String> strings = config.getStringList("bossbar." + key + ".dynamic-text");
                texts = strings.toArray(new String[0]);
            }
            BossBarConfig bossBarConfig = new BossBarConfig(
                    texts,
                    Overlay.valueOf(config.getString("bossbar."+key+".overlay","progress").toUpperCase()),
                    BarColor.valueOf(config.getString("bossbar."+key+".color","white").toUpperCase()),
                    config.getInt("bossbar." + key + ".refresh-rate") - 1
            );
            bossBarConfig.setInternal(config.getInt("bossbar." + key + ".switch-interval", 5) * 20);
            bossBars.put(key, bossBarConfig);
        });
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
                papiBG.put(key, new BackGroundText(papiInfo.getString("papi."+key+".text"), papiInfo.getString("papi." + key + ".background")));
            if (papiInfo.contains("papi." + key + ".nameplate"))
                papiNP.put(key, new NameplateText(papiInfo.getString("papi."+key+".text"), papiInfo.getString("papi." + key + ".nameplate")));
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
     * 加载聊天气泡模块功能
     */
    public static class Bubbles {
        public static String defaultBubble = "none";
        public static String defaultFormat;
        public static String prefix;
        public static String suffix;
        public static double lineSpace;
        public static double yOffset;
        public static int stayTime;
        public static int coolDown;
        public static int maxChar;
        public static void load() {
            YamlConfiguration config = getConfig("bubble.yml");
            defaultBubble = config.getString("bubble.default-bubbles", "none");
            prefix = config.getString("bubble.text-prefix", "");
            suffix = config.getString("bubble.text-suffix", "");
            lineSpace = config.getDouble("bubble.line-spacing");
            defaultFormat = config.getString("bubble.default-format", "<white><underlined>");
            yOffset = config.getDouble("bubble.bottom-line-Y-offset");
            stayTime = config.getInt("bubble.stay-time", 5);
            coolDown = config.getInt("bubble.cool-down", 1) * 1000;
            maxChar = config.getInt("bubble.max-char-length", 35);
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
        if (Main.thin_font)
            for (int i = 0; i < FontWidthThin.values().length; i++)
                fontWidth.put(FontWidthThin.values()[i].getCharacter(), FontWidthThin.values()[i].getLength());
        else
            for (int i = 0; i < FontWidthNormal.values().length; i++)
                fontWidth.put(FontWidthNormal.values()[i].getCharacter(), FontWidthNormal.values()[i].getLength());
        for (int i = 0; i < FontOffset.values().length; i++)
            fontWidth.put(FontOffset.values()[i].getCharacter(), FontOffset.values()[i].getSpace() - 1);
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

        public static void reload(){

            YamlConfiguration databaseConfig = getConfig("database.yml");
            String storage_mode = databaseConfig.getString("settings.storage-mode","SQLite");
            async = !databaseConfig.getBoolean("settings.disable-async", false);
            if(storage_mode.equalsIgnoreCase("SQLite")){
                enable_pool = false;
                use_mysql = false;
                tableName = "nameplates";
            }
            else if(storage_mode.equalsIgnoreCase("MYSQL")){
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
                    maximum_pool_size = databaseConfig.getInt("Pool-Settings.maximum-pool-height");
                    minimum_idle = databaseConfig.getInt("Pool-Settings.minimum-idle");
                    maximum_lifetime = databaseConfig.getInt("Pool-Settings.maximum-lifetime");
                    idle_timeout = databaseConfig.getInt("Pool-Settings.idle-timeout");
                }
            }
            else {
                AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! No such storage mode!</red>");
                Bukkit.getPluginManager().disablePlugin(CustomNameplates.instance);
            }

            if (databaseConfig.getBoolean("settings.migration", false)) {
                SqlHandler.updateTable();
                databaseConfig.set("settings.migration", false);
                AdventureUtil.consoleMessage("<green>[CustomNameplates] Migration is done!");
                try {
                    databaseConfig.save(new File(CustomNameplates.instance.getDataFolder(), "database.yml"));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}