package net.momirealms.customnameplates.manager;

import net.kyori.adventure.key.Key;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.objects.Function;
import net.momirealms.customnameplates.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public class ConfigManager extends Function {

    public static String namespace;
    public static String fontName;
    public static String start_char;
    public static String lang;
    public static String np_folder_path;
    public static String bg_folder_path;
    public static String ss_folder_path;
    public static String bb_folder_path;
    public static String font;
    public static boolean itemsAdderHook;
    public static boolean thin_font;
    public static boolean tab_hook;
    public static boolean tab_BC_hook;
    public static boolean oraxenHook;
    public static boolean extract;
    public static boolean trChat_Hook;
    public static boolean ventureChat_Hook;
    public static List<Integer> offsets;
    public static char start;
    public static Key key;

    public ConfigManager() {
        load();
    }

    public void load(){
        ConfigUtil.update("config.yml");
        YamlConfiguration config = ConfigUtil.getConfig("config.yml");
        lang = config.getString("config.lang");
        namespace = config.getString("config.namespace");
        font = config.getString("config.font");
        fontName = namespace + ":" + font;
        start_char = config.getString("config.start-char");
        assert start_char != null;
        start = start_char.charAt(0);
        np_folder_path = config.getString("config.nameplate-folder-path","font\\nameplates\\");
        bb_folder_path = config.getString("config.bubble-folder-path","font\\bubbles\\");
        bg_folder_path = config.getString("config.background-folder-path","font\\backgrounds\\");
        ss_folder_path = config.getString("config.space-split-folder-path","font\\");
        key = Key.key(fontName);
        thin_font = config.getBoolean("config.use-thin-font",false);
        itemsAdderHook = config.getBoolean("config.integrations.ItemsAdder",false);
        if (itemsAdderHook && Bukkit.getPluginManager().getPlugin("ItemsAdder") == null) itemsAdderHook = false;
        tab_hook = config.getBoolean("config.integrations.TAB",false);
        tab_BC_hook = config.getBoolean("config.integrations.TAB-BC",false);
        oraxenHook = config.getBoolean("config.integrations.Oraxen",false);
        if (oraxenHook && Bukkit.getPluginManager().getPlugin("Oraxen") == null) oraxenHook = false;
        trChat_Hook = config.getBoolean("config.integrations.TrChat",false);
        ventureChat_Hook = config.getBoolean("config.integrations.VentureChat",false);
        offsets = config.getIntegerList("config.ascii-y-offset.offset");
        extract = config.getBoolean("config.extract-shader",true);

        if (tab_hook && Bukkit.getPluginManager().getPlugin("TAB") == null) {
            tab_hook = false;
        }

        if (trChat_Hook && CustomNameplates.plugin.getServer().getPluginManager().getPlugin("TrChat") == null){
            CustomNameplates.plugin.getLogger().warning("Failed to initialize TrChat!");
            trChat_Hook = false;
        }
    }
}
