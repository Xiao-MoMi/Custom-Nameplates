package net.momirealms.customnameplates.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import net.momirealms.customnameplates.AdventureManager;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
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
    //其实是一段问题代码，但基本人人都装了PAPI，更改意义不大
    private void initializePlaceholderAPI() {
        if(!ConfigManager.MainConfig.placeholderAPI){
            this.placeholderAPI = false;
            return;
        }
        if(CustomNameplates.instance.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            AdventureManager.consoleMessage("<gradient:#2E8B57:#48D1CC>[CustomNameplates]</gradient> <color:#baffd1>已启用 PlaceholderAPI 变量解析!");
            this.placeholderAPI = true;
        }
    }
    //ItemsAdder Hook检测
    private void initializeItemsAdder() {
        if (!ConfigManager.MainConfig.itemsAdder) {
            this.itemsAdder = false;
        }
        if(CustomNameplates.instance.getServer().getPluginManager().getPlugin("ItemsAdder") != null){
            this.itemsAdder = true;
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