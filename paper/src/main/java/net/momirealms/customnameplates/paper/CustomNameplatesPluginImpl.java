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

package net.momirealms.customnameplates.paper;

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.event.CustomNameplatesReloadEvent;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.paper.adventure.AdventureManagerImpl;
import net.momirealms.customnameplates.paper.command.CommandManager;
import net.momirealms.customnameplates.paper.libraries.classpath.ReflectionClassPathAppender;
import net.momirealms.customnameplates.paper.libraries.dependencies.Dependency;
import net.momirealms.customnameplates.paper.libraries.dependencies.DependencyManager;
import net.momirealms.customnameplates.paper.libraries.dependencies.DependencyManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.actionbar.ActionBarManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.background.BackGroundManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.bossbar.BossBarManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.bubble.BubbleManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.font.WidthManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.image.ImageManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.misc.CoolDownManager;
import net.momirealms.customnameplates.paper.mechanic.misc.PacketManager;
import net.momirealms.customnameplates.paper.mechanic.misc.VersionManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.nameplate.NameplateManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.pack.ResourcePackManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.placeholder.PlaceholderManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.requirement.RequirementManagerImpl;
import net.momirealms.customnameplates.paper.mechanic.team.TeamManagerImpl;
import net.momirealms.customnameplates.paper.scheduler.SchedulerImpl;
import net.momirealms.customnameplates.paper.setting.CNConfig;
import net.momirealms.customnameplates.paper.setting.CNLocale;
import net.momirealms.customnameplates.paper.storage.StorageManagerImpl;
import net.momirealms.customnameplates.paper.util.Migration;
import net.momirealms.customnameplates.paper.util.ReflectionUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CustomNameplatesPluginImpl extends CustomNameplatesPlugin implements Listener {

    private CoolDownManager coolDownManager;
    private PacketManager packetManager;
    private DependencyManager dependencyManager;

    @Override
    public void onLoad() {
        this.versionManager = new VersionManagerImpl(this);
        this.dependencyManager = new DependencyManagerImpl(this, new ReflectionClassPathAppender(this.getClassLoader()));
        this.dependencyManager.loadDependencies(new ArrayList<>(
                List.of(
                        Dependency.GSON,
                        Dependency.SLF4J_API,
                        Dependency.SLF4J_SIMPLE,
                        versionManager.isMojmap() ? Dependency.COMMAND_API_MOJMAP : Dependency.COMMAND_API,
                        Dependency.BOOSTED_YAML,
                        Dependency.MYSQL_DRIVER,
                        Dependency.MARIADB_DRIVER,
                        Dependency.MONGODB_DRIVER_SYNC,
                        Dependency.MONGODB_DRIVER_CORE,
                        Dependency.MONGODB_DRIVER_BSON,
                        Dependency.JEDIS,
                        Dependency.COMMONS_POOL_2,
                        Dependency.H2_DRIVER,
                        Dependency.SQLITE_DRIVER,
                        Dependency.BSTATS_BASE,
                        Dependency.HIKARI,
                        Dependency.BSTATS_BUKKIT
                )
        ));
        ReflectionUtils.load();
    }

    @Override
    public void onEnable() {
        if (Migration.check()) {
            LogUtils.warn("Please read /CustomNameplates/README.txt to finish the migration.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        this.adventureManager = new AdventureManagerImpl(this);
        this.scheduler = new SchedulerImpl(this);
        this.storageManager = new StorageManagerImpl(this);
        this.requirementManager = new RequirementManagerImpl(this);
        this.bossBarManager = new BossBarManagerImpl(this);
        this.imageManager = new ImageManagerImpl(this);
        this.placeholderManager = new PlaceholderManagerImpl(this);
        this.backGroundManager = new BackGroundManagerImpl(this);
        this.resourcePackManager = new ResourcePackManagerImpl(this);
        this.nameplateManager = new NameplateManagerImpl(this);
        this.teamManager = new TeamManagerImpl(this);
        this.widthManager = new WidthManagerImpl(this);
        this.bubbleManager = new BubbleManagerImpl(this);
        this.actionBarManager = new ActionBarManagerImpl(this);
        this.coolDownManager = new CoolDownManager(this);
        this.packetManager = new PacketManager(this);
        this.reload();
        new CommandManager(this).load();
        this.versionManager.checkUpdate().thenAccept(outDated -> {
            if (!outDated) this.getAdventure().sendConsoleMessage("[CustomNameplates] You are using the latest version.");
            else this.getAdventure().sendConsoleMessage("[CustomNameplates] Update is available: <u>https://polymart.org/resource/2543<!u>");
        });
        this.getServer().getPluginManager().registerEvents((VersionManagerImpl) versionManager, this);
        if (CNConfig.generatePackOnStart)
            this.resourcePackManager.generateResourcePack();
        if (CNConfig.metrics)
            new Metrics(this, 16649);
    }

    @Override
    public void onDisable() {
        if (scheduler != null) ((SchedulerImpl) this.scheduler).shutdown();
        if (actionBarManager != null) ((ActionBarManagerImpl) actionBarManager).unload();
        if (nameplateManager != null) ((NameplateManagerImpl) this.nameplateManager).disable();
        if (teamManager != null) ((TeamManagerImpl) this.teamManager).disable();
        if (bossBarManager != null)  ((BossBarManagerImpl) this.bossBarManager).unload();
        if (imageManager != null) ((ImageManagerImpl) this.imageManager).unload();
        if (backGroundManager != null) ((BackGroundManagerImpl) this.backGroundManager).unload();
        if (placeholderManager != null) ((PlaceholderManagerImpl) this.placeholderManager).unload();
        if (bubbleManager != null) ((BubbleManagerImpl) this.bubbleManager).unload();
        if (requirementManager != null) ((RequirementManagerImpl) this.requirementManager).unload();
        if (resourcePackManager != null) ((ResourcePackManagerImpl) this.resourcePackManager).unload();
        if (widthManager != null) ((WidthManagerImpl) this.widthManager).unload();
        if (storageManager != null) ((StorageManagerImpl) this.storageManager).disable();
        if (adventureManager != null) ((AdventureManagerImpl) this.adventureManager).close();
        if (versionManager != null) HandlerList.unregisterAll((VersionManagerImpl) versionManager);
    }

    @Override
    public void reload() {
        CNConfig.load();
        CNLocale.load();
        ((SchedulerImpl) this.scheduler).reload();
        ((AdventureManagerImpl) this.adventureManager).reload();
        ((NameplateManagerImpl) this.nameplateManager).reload();
        ((BubbleManagerImpl) this.bubbleManager).reload();
        ((BackGroundManagerImpl) this.backGroundManager).reload();
        ((TeamManagerImpl) this.teamManager).reload();
        ((StorageManagerImpl) this.storageManager).reload();
        ((RequirementManagerImpl) this.requirementManager).reload();
        ((BossBarManagerImpl) this.bossBarManager).reload();
        ((ActionBarManagerImpl) actionBarManager).reload();
        ((ImageManagerImpl) this.imageManager).reload();
        ((PlaceholderManagerImpl) this.placeholderManager).reload();
        ((WidthManagerImpl) this.widthManager).reload();
        ((ResourcePackManagerImpl) this.resourcePackManager).reload();
        CustomNameplatesReloadEvent event = new CustomNameplatesReloadEvent(this);
        this.getServer().getPluginManager().callEvent(event);
    }

    @Override
    public YamlConfiguration getConfig(String file) {
        File config = new File(this.getDataFolder(), file);
        if (!config.exists()) this.saveResource(file, false);
        return YamlConfiguration.loadConfiguration(config);
    }

    @Override
    public void debug(String s) {
        if (CNConfig.debug) {
            LogUtils.info(s);
        }
    }

    public CoolDownManager getCoolDownManager() {
        return coolDownManager;
    }

    public PacketManager getPacketManager() {
        return packetManager;
    }

    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }
}
