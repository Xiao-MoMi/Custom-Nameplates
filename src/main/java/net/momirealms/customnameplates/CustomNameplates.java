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
import net.momirealms.customnameplates.actionbar.ActionBarManager;
import net.momirealms.customnameplates.bossbar.BossBarManager;
import net.momirealms.customnameplates.commands.Execute;
import net.momirealms.customnameplates.commands.TabComplete;
import net.momirealms.customnameplates.data.DataManager;
import net.momirealms.customnameplates.data.SqlHandler;
import net.momirealms.customnameplates.helper.LibraryLoader;
import net.momirealms.customnameplates.hook.PlaceholderManager;
import net.momirealms.customnameplates.nameplates.TeamPacketManager;
import net.momirealms.customnameplates.nameplates.mode.NameplateManager;
import net.momirealms.customnameplates.nameplates.mode.rd.RidingTag;
import net.momirealms.customnameplates.nameplates.mode.tm.TeamTag;
import net.momirealms.customnameplates.nameplates.mode.tmpackets.TeamPacketA;
import net.momirealms.customnameplates.nameplates.mode.tmpackets.TeamPacketB;
import net.momirealms.customnameplates.nameplates.mode.tmpackets.TeamPacketC;
import net.momirealms.customnameplates.nameplates.mode.tp.TeleportingTag;
import net.momirealms.customnameplates.resource.ResourceManager;
import net.momirealms.customnameplates.nameplates.TeamManager;
import net.momirealms.customnameplates.utils.AdventureUtil;
import net.momirealms.customnameplates.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class CustomNameplates extends JavaPlugin {

    public static CustomNameplates instance;
    public static BukkitAudiences adventure;
    public static ProtocolManager protocolManager;

    private ResourceManager resourceManager;
    private DataManager dataManager;
    private TeamManager teamManager;
    private TeamPacketManager teamPacketManager;
    private BossBarManager bossBarManager;
    private ActionBarManager actionBarManager;
    private PlaceholderManager placeholderManager;
    private NameplateManager nameplateManager;

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

        if (!Objects.equals(ConfigManager.Main.version, "3")){
            ConfigUtil.update();
        }

        AdventureUtil.consoleMessage("<gradient:#2E8B57:#48D1CC>[CustomNameplates]</gradient> <color:#baffd1>Plugin Enabled!");
    }

    @Override
    public void onDisable() {
        if (ConfigManager.Module.nameplate){
            SqlHandler.saveAll();
            SqlHandler.close();
        }
        if (actionBarManager != null){
            actionBarManager.unload();
        }
        if (adventure != null) {
            adventure.close();
        }
        if (resourceManager != null){
            resourceManager = null;
        }
        if (teamManager != null){
            teamManager = null;
        }
        if (dataManager != null){
            dataManager = null;
        }
    }

    public void loadConfig() {

        ConfigManager.Module.loadModule();
        ConfigManager.Main.reload();
        ConfigManager.Message.reload();
        ConfigManager.loadWidth();

        if (ConfigManager.Module.bossBar){
            ConfigManager.loadBossBar();
            if (this.bossBarManager != null) {
                this.bossBarManager.unload();
                this.bossBarManager.load();
            }
            else {
                this.bossBarManager = new BossBarManager("BossBar");
                this.bossBarManager.load();
            }
        }
        else if (this.bossBarManager != null) {
            this.bossBarManager.unload();
            this.bossBarManager = null;
        }

        if (ConfigManager.Module.actionbar){
            ConfigManager.ActionbarConfig.load();
            if (actionBarManager != null) {
                this.actionBarManager.unload();
                this.actionBarManager.load();
            }
            else {
                this.actionBarManager = new ActionBarManager("ActionBar");
                this.actionBarManager.load();
            }
        }
        else if (this.actionBarManager != null) {
            this.actionBarManager.unload();
            this.actionBarManager = null;
        }

        if (ConfigManager.Module.background){
            ConfigManager.loadBGConfig();
        }

        if (ConfigManager.Module.nameplate){
            ConfigManager.Nameplate.reload();
            ConfigManager.DatabaseConfig.reload();
            if (this.dataManager == null) {
                this.dataManager = new DataManager();
                if (!dataManager.create()) {
                    AdventureUtil.consoleMessage("<red>[CustomNameplates] Error! Failed to enable Data Manager!</red>");
                    return;
                }
            }
            if (this.teamManager == null) {
                this.teamManager = new TeamManager();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    CustomNameplates.instance.getDataManager().loadData(player);
                }
            }
            if (this.nameplateManager != null) {
                this.nameplateManager.unload();
                this.nameplateManager = null;
            }
            if (ConfigManager.Nameplate.mode.equalsIgnoreCase("team")) {
                this.teamPacketManager = new TeamPacketA();
                this.nameplateManager = new TeamTag("TEAM");
                this.nameplateManager.load();
            }
            else {
                this.teamPacketManager = new TeamPacketB();
                if (ConfigManager.Nameplate.mode.equalsIgnoreCase("riding")) {
                    this.nameplateManager = new RidingTag("RIDING");
                    this.nameplateManager.load();
                }else if (ConfigManager.Nameplate.mode.equalsIgnoreCase("teleporting")){
                    this.nameplateManager = new TeleportingTag("TELEPORTING");
                    this.nameplateManager.load();
                }else {
                    AdventureUtil.consoleMessage("<red>[CustomNameplates] Unknown nameplate mode!");
                }
            }
        }
        else {
            if (this.nameplateManager != null) {
                TeamPacketC.clearTeamInfo();
                this.nameplateManager.unload();
                this.nameplateManager = null;
            }
        }

        if (ConfigManager.Main.placeholderAPI){
            ConfigManager.loadPapi();
            if (this.placeholderManager != null) {
                this.placeholderManager.unload();
                this.placeholderManager.load();
            }
            else {
                this.placeholderManager = new PlaceholderManager("PAPI");
                this.placeholderManager.load();
            }
        }
        else if (this.placeholderManager != null) {
            this.placeholderManager.unload();
            this.placeholderManager = null;
        }
    }

    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    public DataManager getDataManager() {
        return this.dataManager;
    }

    public TeamManager getTeamManager() {
        return this.teamManager;
    }

    public TeamPacketManager getTeamPacketManager() {
        return teamPacketManager;
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
}
