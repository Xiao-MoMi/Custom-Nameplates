package net.momirealms.customnameplates.bukkit;

import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.common.plugin.NameplatesPlugin;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitBootstrap extends JavaPlugin {

    private CustomNameplates nameplates;
    private NameplatesPlugin plugin;

    @Override
    public void onLoad() {
        this.plugin = new BukkitCustomNameplates(this);
        this.plugin.load();
    }

    @Override
    public void onEnable() {
        this.plugin.enable();
    }

    @Override
    public void onDisable() {
        this.plugin.disable();
    }
}
