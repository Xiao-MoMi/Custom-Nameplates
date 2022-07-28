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

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.momirealms.customnameplates.actionbar.Timer;
import net.momirealms.customnameplates.bossbar.adventure.QuitAndJoinA;
import net.momirealms.customnameplates.bossbar.protocollib.QuitAndJoinP;
import net.momirealms.customnameplates.commands.Execute;
import net.momirealms.customnameplates.commands.TabComplete;
import net.momirealms.customnameplates.data.DataManager;
import net.momirealms.customnameplates.data.SqlHandler;
import net.momirealms.customnameplates.helper.LibraryLoader;
import net.momirealms.customnameplates.hook.Placeholders;
import net.momirealms.customnameplates.listener.PlayerListener;
import net.momirealms.customnameplates.listener.PacketsListener;
import net.momirealms.customnameplates.resource.ResourceManager;
import net.momirealms.customnameplates.scoreboard.ScoreBoardManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class CustomNameplates extends JavaPlugin {

    public static JavaPlugin instance;
    public static BukkitAudiences adventure;
    public static ProtocolManager protocolManager;

    private ResourceManager resourceManager;
    private DataManager dataManager;
    private ScoreBoardManager scoreBoardManager;
    private Timer timer;

    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }
    public DataManager getDataManager() { return this.dataManager; }
    public ScoreBoardManager getScoreBoardManager() { return this.scoreBoardManager; }

    @Override
    public void onLoad(){
        instance = this;
        LibraryLoader.load("commons-io","commons-io","2.11.0","https://repo.maven.apache.org/maven2/");
        LibraryLoader.load("com.zaxxer","HikariCP","5.0.1","https://repo.maven.apache.org/maven2/");
    }

    @Override
    public void onEnable() {
        adventure = BukkitAudiences.create(this);
        protocolManager = ProtocolLibrary.getProtocolManager();
        AdventureManager.consoleMessage("<gradient:#2E8B57:#48D1CC>[CustomNameplates] </gradient><color:#baffd1>Running on " + Bukkit.getVersion());
        ConfigManager.loadModule();
        ConfigManager.MainConfig.ReloadConfig();
        ConfigManager.Message.ReloadConfig();
        if (ConfigManager.bossbar){
            ConfigManager.loadBossBar();
            if (ConfigManager.useAdventure){
                Bukkit.getPluginManager().registerEvents(new QuitAndJoinA(),this);
            }else {
                Bukkit.getPluginManager().registerEvents(new QuitAndJoinP(),this);
            }
        }
        if (ConfigManager.actionbar){
            ConfigManager.ActionbarConfig.LoadConfig();
            timer = new Timer();
        }
        if (ConfigManager.background){
            ConfigManager.loadBGConfig();
        }
        if (ConfigManager.nameplate){
            ConfigManager.DatabaseConfig.LoadConfig();
            Bukkit.getPluginManager().registerEvents(new PlayerListener(this),this);
            ProtocolLibrary.getProtocolManager().addPacketListener(new PacketsListener(this));
        }
        if (ConfigManager.MainConfig.placeholderAPI){
            new Placeholders().register();
            AdventureManager.consoleMessage("<gradient:#2E8B57:#48D1CC>[CustomNameplates]</gradient> <color:#baffd1>PlaceholderAPI Hooked!");
        }
        Objects.requireNonNull(Bukkit.getPluginCommand("customnameplates")).setExecutor(new Execute(this));
        Objects.requireNonNull(Bukkit.getPluginCommand("customnameplates")).setTabCompleter(new TabComplete(this));
        this.resourceManager = new ResourceManager(this);
        this.dataManager = new DataManager(this);
        this.scoreBoardManager = new ScoreBoardManager(this);
        resourceManager.generateResourcePack();
        if (!DataManager.create()) {
            AdventureManager.consoleMessage("<red>[CustomNameplates] Error! Failed to enable Data Manager! Disabling plugin...</red>");
            instance.getPluginLoader().disablePlugin(instance);
            return;
        }
        AdventureManager.consoleMessage("<gradient:#2E8B57:#48D1CC>[CustomNameplates]</gradient> <color:#baffd1>Plugin Enabled!");
    }

    @Override
    public void onDisable() {
        if (ConfigManager.nameplate){
            SqlHandler.saveAll();
            SqlHandler.close();
        }
        Execute.pCache.forEach(Entity::remove);
        if (timer != null){
            timer.stopTimer(timer.getTaskID());
        }
        if(adventure != null) {
            adventure.close();
            adventure = null;
        }
    }
}
