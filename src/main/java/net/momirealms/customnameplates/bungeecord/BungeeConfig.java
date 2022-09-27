package net.momirealms.customnameplates.bungeecord;

public class BungeeConfig {

    private final Main plugin;

    public BungeeConfig(Main plugin) {
        this.plugin = plugin;
    }

    private boolean tab;

    public void load() {
        tab = plugin.getProxy().getPluginManager().getPlugin("TAB") != null;
        if (tab) {
            plugin.getLogger().info("TAB hooked");
        }
    }

    public boolean isTab() {
        return tab;
    }
}
