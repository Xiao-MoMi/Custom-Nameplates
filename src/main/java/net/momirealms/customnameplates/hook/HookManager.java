package net.momirealms.customnameplates.hook;

import net.momirealms.customnameplates.AdventureManager;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;

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

    private void initializePlaceholderAPI() {
        if(!ConfigManager.MainConfig.placeholderAPI){
            this.placeholderAPI = false;
            return;
        }
        if(CustomNameplates.instance.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            AdventureManager.consoleMessage("<gradient:#2E8B57:#48D1CC>[CustomNameplates]</gradient> <color:#baffd1>PlaceholderAPI Hooked!");
            this.placeholderAPI = true;
        }
    }

    private void initializeItemsAdder() {
        if (!ConfigManager.MainConfig.itemsAdder) {
            this.itemsAdder = false;
        }
        if(CustomNameplates.instance.getServer().getPluginManager().getPlugin("ItemsAdder") != null){
            this.itemsAdder = true;
        }
    }
}