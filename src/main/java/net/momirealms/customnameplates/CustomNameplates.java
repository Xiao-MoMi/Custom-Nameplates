/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customnameplates;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.momirealms.customnameplates.commands.BubblesCommand;
import net.momirealms.customnameplates.commands.NameplateCommand;
import net.momirealms.customnameplates.helper.LibraryLoader;
import net.momirealms.customnameplates.helper.VersionHelper;
import net.momirealms.customnameplates.manager.*;
import net.momirealms.customnameplates.utils.AdventureUtil;
import net.momirealms.customnameplates.utils.ConfigUtil;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomNameplates extends JavaPlugin {

    public static CustomNameplates plugin;
    public static BukkitAudiences adventure;
    public static ProtocolManager protocolManager;

    private ResourceManager resourceManager;
    private BossBarManager bossBarManager;
    private ActionBarManager actionBarManager;
    private PlaceholderManager placeholderManager;
    private NameplateManager nameplateManager;
    private ChatBubblesManager chatBubblesManager;
    private DataManager dataManager;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private WidthManager widthManager;
    private VersionHelper versionHelper;

    @Override
    public void onLoad(){
        plugin = this;
        LibraryLoader.load("commons-io","commons-io","2.11.0","https://repo.maven.apache.org/maven2/");
        LibraryLoader.load("com.zaxxer","HikariCP","5.0.1","https://repo.maven.apache.org/maven2/");
        LibraryLoader.load("dev.dejvokep","boosted-yaml","1.3","https://repo.maven.apache.org/maven2/");
        LibraryLoader.load("org.mariadb.jdbc","mariadb-java-client","3.0.6","https://repo.maven.apache.org/maven2/");
    }

    @Override
    public void onEnable() {
        adventure = BukkitAudiences.create(this);
        protocolManager = ProtocolLibrary.getProtocolManager();

        //Don't delete this
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);

        AdventureUtil.consoleMessage("[CustomNameplates] Running on <white>" + Bukkit.getVersion());

        this.placeholderManager = new PlaceholderManager();
        this.actionBarManager = new ActionBarManager();
        this.bossBarManager = new BossBarManager();
        this.resourceManager = new ResourceManager();
        this.dataManager = new DataManager();
        this.nameplateManager = new NameplateManager();
        this.configManager = new ConfigManager();
        this.messageManager = new MessageManager();
        this.widthManager = new WidthManager();
        this.chatBubblesManager = new ChatBubblesManager();
        this.versionHelper = new VersionHelper();

        ConfigUtil.reloadConfigs();

        NameplateCommand nameplateCommand = new NameplateCommand();
        Bukkit.getPluginCommand("customnameplates").setExecutor(nameplateCommand);
        Bukkit.getPluginCommand("customnameplates").setTabCompleter(nameplateCommand);

        BubblesCommand bubblesCommand = new BubblesCommand();
        Bukkit.getPluginCommand("bubbles").setExecutor(bubblesCommand);
        Bukkit.getPluginCommand("bubbles").setTabCompleter(bubblesCommand);

        AdventureUtil.consoleMessage("[CustomNameplates] Plugin Enabled!");

        new Metrics(this, 16649);
    }

    @Override
    public void onDisable() {
        if (actionBarManager != null) {
            actionBarManager.unload();
        }
        if (nameplateManager != null) {
            nameplateManager.unload();
        }
        if (bossBarManager != null) {
            bossBarManager.unload();
        }
        if (chatBubblesManager != null) {
            chatBubblesManager.unload();
        }
        if (placeholderManager != null) {
            placeholderManager.unload();
        }
        if (widthManager != null) {
            widthManager.unload();
        }
        if (dataManager != null) {
            dataManager.disable();
        }
        if (adventure != null) {
            adventure.close();
        }
    }

    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    public DataManager getDataManager() {
        return this.dataManager;
    }

    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }

    public BossBarManager getBossBarManager() {
        return bossBarManager;
    }

    public ActionBarManager getActionBarManager() {
        return actionBarManager;
    }

    public NameplateManager getNameplateManager() {
        return nameplateManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public ChatBubblesManager getChatBubblesManager() {
        return chatBubblesManager;
    }

    public WidthManager getWidthManager() {
        return widthManager;
    }

    public VersionHelper getVersionHelper() {
        return versionHelper;
    }
}
