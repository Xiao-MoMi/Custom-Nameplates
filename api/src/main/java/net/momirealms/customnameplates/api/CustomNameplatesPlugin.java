package net.momirealms.customnameplates.api;

import net.momirealms.customnameplates.api.manager.*;
import net.momirealms.customnameplates.api.scheduler.Scheduler;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class CustomNameplatesPlugin extends JavaPlugin {

    protected static CustomNameplatesPlugin instance;
    protected Scheduler scheduler;
    protected StorageManager storageManager;
    protected VersionManager versionManager;
    protected AdventureManager adventureManager;
    protected RequirementManager requirementManager;
    protected BossBarManager bossBarManager;
    protected ImageManager imageManager;
    protected PlaceholderManager placeholderManager;
    protected ResourcePackManager resourcePackManager;
    protected BackGroundManager backGroundManager;
    protected TeamManager teamManager;
    protected NameplateManager nameplateManager;
    protected ActionBarManager actionBarManager;
    protected WidthManager widthManager;

    protected CustomNameplatesPlugin() {
        instance = this;
    }

    /* Get plugin instance */
    public static CustomNameplatesPlugin getInstance() {
        return instance;
    }

    /* Get plugin instance */
    public static CustomNameplatesPlugin get() {
        return instance;
    }

    /* reload the plugin */
    public abstract void reload(boolean generatePack);

    /* Get the scheduler */
    public Scheduler getScheduler() {
        return scheduler;
    }

    /* Get the storage manager */
    public StorageManager getStorageManager() {
        return storageManager;
    }

    /* Get the requirement manager */
    public RequirementManager getRequirementManager() {
        return requirementManager;
    }

    /* Get the image manager */
    public ImageManager getImageManager() {
        return imageManager;
    }

    /* Get the background manager */
    public BackGroundManager getBackGroundManager() {
        return backGroundManager;
    }

    /* Get the resource pack manager */
    public ResourcePackManager getResourcePackManager() {
        return resourcePackManager;
    }

    /* Get the adventure manager */
    public AdventureManager getAdventure() {
        return adventureManager;
    }

    /* Get the version manager */
    public VersionManager getVersionManager() {
        return versionManager;
    }

    /* Get the bossbar manager */
    public BossBarManager getBossBarManager() {
        return bossBarManager;
    }

    /* Get the placeholder manager */
    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }

    /* Get the team manager */
    public TeamManager getTeamManager() {
        return teamManager;
    }

    /* Get the actionbar manager */
    public ActionBarManager getActionBarManager() {
        return actionBarManager;
    }

    /* Get the nameplate manager */
    public NameplateManager getNameplateManager() {
        return nameplateManager;
    }

    /* Get the width manager */
    public WidthManager getWidthManager() {
        return widthManager;
    }

    /* debug get config by file name */
    public abstract YamlConfiguration getConfig(String file);

    /* debug message */
    public abstract void debug(String s);
}
