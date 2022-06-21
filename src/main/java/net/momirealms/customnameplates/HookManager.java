package net.momirealms.customnameplates;

import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HookManager {

    private boolean placeholderAPI;
    private boolean itemsAdder;

    public boolean hasPlaceholderAPI() {
        return this.placeholderAPI;
    }
    public boolean hasItemsAdder() {
        return this.itemsAdder;
    }

    public HookManager(CustomNameplates plugin) {
        this.initializePlaceholderAPI();
        this.initializeItemsAdder();
    }

    //Papi Hook检测
    private void initializePlaceholderAPI() {
        if(!ConfigManager.MainConfig.placeholderAPI){
            this.placeholderAPI = false;
            return;
        }
        if(CustomNameplates.instance.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            this.placeholderAPI = true;
            AdventureManager.consoleMessage("<gradient:#DDE4FF:#8DA2EE>[CustomNameplates]</gradient> " + "<color:#F5F5F5>PlaceholderAPI Hooked!");
        }
    }
    //ItemsAdder Hook检测
    private void initializeItemsAdder() {
        if (!ConfigManager.MainConfig.itemsAdder) {
            this.itemsAdder = false;
        }
        if(CustomNameplates.instance.getServer().getPluginManager().getPlugin("ItemsAdder") != null){
            this.itemsAdder = true;
            AdventureManager.consoleMessage("<gradient:#DDE4FF:#8DA2EE>[CustomNameplates]</gradient> " + "<color:#F5F5F5>ItemsAdder Hooked!");
        }
    }
    /*
    解析prefix与suffix
     */
    public String parsePlaceholders(Player player, String papi) {
        String s = StringUtils.replace(StringUtils.replace(papi, "%player_name%", player.getName()), "%player_displayname%", player.getDisplayName());
        s = PlaceholderAPI.setPlaceholders(player, s);
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}