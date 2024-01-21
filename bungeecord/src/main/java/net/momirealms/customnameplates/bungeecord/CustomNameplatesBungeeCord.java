package net.momirealms.customnameplates.bungeecord;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

public class CustomNameplatesBungeeCord extends Plugin implements Listener {

    private static final String CHANNEL = "customnameplates:cnp";
    private static CustomNameplatesBungeeCord instance;

    @Override
    public void onEnable() {
        instance = this;
        this.getProxy().registerChannel(CHANNEL);
        this.getProxy().getPluginManager().registerListener(this, this);
        this.getLogger().warning("It's not necessary to install the plugin on BungeeCord now!");
    }

    @Override
    public void onDisable() {
        this.getProxy().unregisterChannel(CHANNEL);
        this.getProxy().getPluginManager().unregisterListener(this);
    }

    public static CustomNameplatesBungeeCord getInstance() {
        return instance;
    }

    public static CustomNameplatesBungeeCord get() {
        return instance;
    }
}
