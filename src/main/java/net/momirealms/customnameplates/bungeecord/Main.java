package net.momirealms.customnameplates.bungeecord;

import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {

    public static Main bungeePlugin;

    private BungeeConfig bungeeConfig;

    @Override
    public void onEnable() {
        bungeePlugin = this;
        this.getProxy().registerChannel("customnameplates:cnp");
        this.getProxy().getPluginManager().registerListener(this, new BungeeEventListener(this));
        this.bungeeConfig = new BungeeConfig(this);
        this.bungeeConfig.load();
    }

    @Override
    public void onDisable() {

    }

    public BungeeConfig getBungeeConfig() {
        return bungeeConfig;
    }
}
