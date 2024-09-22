package net.momirealms.customnameplates.bukkit;

import net.momirealms.customnameplates.api.ConfigManager;

import java.io.File;

public class BukkitConfigManager extends ConfigManager {

    private final BukkitCustomNameplates plugin;

    public BukkitConfigManager(BukkitCustomNameplates plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public void saveResource(String filePath) {
        if (!new File(plugin.getDataDirectory().toFile(), filePath).exists()) {
            plugin.getBootstrap().saveResource(filePath, false);
        }
    }
}
