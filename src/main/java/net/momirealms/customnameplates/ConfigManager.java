package net.momirealms.customnameplates;

import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigManager {

    //根据文件名获取配置文件
    public static YamlConfiguration getConfig(String configName) {
        File file = new File(CustomNameplates.instance.getDataFolder(), configName);
        //文件不存在则生成默认配置
        if (!file.exists()) {
            CustomNameplates.instance.saveResource(configName, false);
        }
        return YamlConfiguration.loadConfiguration(file);
    }
    //主配置文件
    public static class MainConfig{

        public static String namespace;
        public static String fontName;
        public static String start_char;
        public static String folder_path;
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

        public static void ReloadConfig(){
            CustomNameplates.instance.saveDefaultConfig();
            CustomNameplates.instance.reloadConfig();
            FileConfiguration config = CustomNameplates.instance.getConfig();

            lang = config.getString("config.lang");
            namespace = config.getString("config.namespace");
            font = config.getString("config.font");
            fontName = namespace + ":" + font;
            start_char = config.getString("config.start-char");
            folder_path = config.getString("config.folder-path");
            default_nameplate = config.getString("config.default-nameplate");
            player_prefix = config.getString("config.prefix");
            player_suffix = config.getString("config.suffix");
            itemsAdder =config.getBoolean("config.integrations.ItemsAdder");
            placeholderAPI = config.getBoolean("config.integrations.PlaceholderAPI");
            show_after = config.getBoolean("config.show-after-load-resourcepack");
            key = Key.key(fontName);
            preview = config.getLong("config.preview-duration");
            thin_font = config.getBoolean("config.use-thin-font",false);
            hidePrefix = config.getBoolean("config.hide-prefix-when-equipped",false);
            hideSuffix = config.getBoolean("config.hide-suffix-when-equipped",false);
        }
    }
    //消息文件
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
        }
    }
    //数据库配置
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
            //使用SQLite
            if(storage_mode.equals("SQLite")){
                enable_pool = false;
                use_mysql = false;
                tableName = "nameplates";
            }
            //使用MYSQL
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
