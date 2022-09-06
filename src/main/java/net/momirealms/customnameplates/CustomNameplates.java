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
import net.momirealms.customnameplates.listener.*;
import net.momirealms.customnameplates.resource.ResourceManager;
import net.momirealms.customnameplates.scoreboard.ScoreBoardManager;
import net.momirealms.customnameplates.utils.AdventureUtil;
import net.momirealms.customnameplates.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class CustomNameplates extends JavaPlugin {

    public static CustomNameplates instance;
    public static BukkitAudiences adventure;
    public static ProtocolManager protocolManager;
    public static Placeholders placeholders;

    private ResourceManager resourceManager;
    private DataManager dataManager;
    private ScoreBoardManager scoreBoardManager;
    private Timer timer;
    private PlayerPacketsListener playerPackets;
    private MountPacketListener mountPackets;
    private EntityDestroyListener entityDestroy;

    @Override
    public void onLoad(){
        instance = this;
        LibraryLoader.load("commons-io","commons-io","2.11.0","https://repo.maven.apache.org/maven2/");
        LibraryLoader.load("com.zaxxer","HikariCP","5.0.1","https://repo.maven.apache.org/maven2/");
        LibraryLoader.load("dev.dejvokep","boosted-yaml","1.3","https://repo.maven.apache.org/maven2/");
    }

    @Override
    public void onEnable() {

        adventure = BukkitAudiences.create(this);
        protocolManager = ProtocolLibrary.getProtocolManager();

        AdventureUtil.consoleMessage("[CustomNameplates] Running on <white>" + Bukkit.getVersion());

        Objects.requireNonNull(Bukkit.getPluginCommand("customnameplates")).setExecutor(new Execute());
        Objects.requireNonNull(Bukkit.getPluginCommand("customnameplates")).setTabCompleter(new TabComplete());

        loadConfig();
        this.resourceManager = new ResourceManager();
        this.resourceManager.generateResourcePack();

        if (Objects.equals(ConfigManager.MainConfig.version, "2")){
            ConfigUtil.update();
        }

        AdventureUtil.consoleMessage("<gradient:#2E8B57:#48D1CC>[CustomNameplates]</gradient> <color:#baffd1>Plugin Enabled!");
    }

    @Override
    public void onDisable() {
        if (ConfigManager.nameplate){
            SqlHandler.saveAll();
            SqlHandler.close();
        }
        if (timer != null){
            timer.stopTimer(timer.getTaskID());
        }
        if (adventure != null) {
            adventure.close();
        }
        if (protocolManager != null){
            if (playerPackets != null) {
                protocolManager.removePacketListener(this.playerPackets);
            }
            if (mountPackets != null) {
                protocolManager.removePacketListener(this.mountPackets);
            }
            if (entityDestroy != null) {
                protocolManager.removePacketListener(this.entityDestroy);
            }
        }
        if (placeholders != null){
            placeholders.unregister();
        }
        if (resourceManager != null){
            resourceManager = null;
        }
        if (scoreBoardManager != null){
            scoreBoardManager = null;
        }
        if (dataManager != null){
            dataManager = null;
        }
    }

    private void loadConfig() {
        ConfigManager.loadModule();
        ConfigManager.MainConfig.reload();
        ConfigManager.Message.reload();
        ConfigManager.loadWidth();

        if (ConfigManager.bossbar){
            ConfigManager.loadBossBar();
            if (ConfigManager.useAdventure) Bukkit.getPluginManager().registerEvents(new QuitAndJoinA(),this);
            else Bukkit.getPluginManager().registerEvents(new QuitAndJoinP(),this);
        }
        if (ConfigManager.actionbar){
            ConfigManager.ActionbarConfig.load();
            timer = new Timer();
        }
        if (ConfigManager.background){
            ConfigManager.loadBGConfig();
        }
        if (ConfigManager.nameplate){
            ConfigManager.Nameplate.reload();
            ConfigManager.DatabaseConfig.LoadConfig();
            Bukkit.getPluginManager().registerEvents(new PlayerListener(),this);
            this.scoreBoardManager = new ScoreBoardManager();
            this.dataManager = new DataManager();
            if (!dataManager.create()) {
                AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to enable Data Manager! Disabling plugin...</red>");
                return;
            }
            if (!ConfigManager.Nameplate.mode_team) {
                playerPackets = new PlayerPacketsListener(this);
                protocolManager.addPacketListener(playerPackets);
                entityDestroy = new EntityDestroyListener(this);
                protocolManager.addPacketListener(entityDestroy);
                if (ConfigManager.Nameplate.tryHook) {
                    mountPackets = new MountPacketListener(this);
                    protocolManager.addPacketListener(mountPackets);
                }
            }
        }
        if (ConfigManager.MainConfig.placeholderAPI){
            placeholders = new Placeholders();
            placeholders.register();
            ConfigManager.loadPapi();
            Bukkit.getPluginManager().registerEvents(new PapiReload(), this);
        }
    }

    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    public DataManager getDataManager() {
        return this.dataManager;
    }

    public ScoreBoardManager getScoreBoardManager() {
        return this.scoreBoardManager;
    }
}
