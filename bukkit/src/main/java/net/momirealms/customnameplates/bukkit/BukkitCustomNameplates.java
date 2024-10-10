/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customnameplates.bukkit;

import net.momirealms.customnameplates.api.*;
import net.momirealms.customnameplates.api.event.NameplatesReloadEvent;
import net.momirealms.customnameplates.api.feature.ChatListener;
import net.momirealms.customnameplates.api.feature.JoinQuitListener;
import net.momirealms.customnameplates.api.feature.PlayerListener;
import net.momirealms.customnameplates.api.helper.AdventureHelper;
import net.momirealms.customnameplates.api.helper.VersionHelper;
import net.momirealms.customnameplates.backend.feature.actionbar.ActionBarManagerImpl;
import net.momirealms.customnameplates.backend.feature.advance.AdvanceManagerImpl;
import net.momirealms.customnameplates.backend.feature.background.BackgroundManagerImpl;
import net.momirealms.customnameplates.backend.feature.bossbar.BossBarManagerImpl;
import net.momirealms.customnameplates.backend.feature.bubble.BubbleManagerImpl;
import net.momirealms.customnameplates.backend.feature.image.ImageManagerImpl;
import net.momirealms.customnameplates.backend.feature.nameplate.NameplateManagerImpl;
import net.momirealms.customnameplates.backend.feature.pack.ResourcePackManagerImpl;
import net.momirealms.customnameplates.backend.feature.tag.UnlimitedTagManagerImpl;
import net.momirealms.customnameplates.backend.placeholder.PlaceholderManagerImpl;
import net.momirealms.customnameplates.backend.storage.StorageManagerImpl;
import net.momirealms.customnameplates.bukkit.command.BukkitCommandManager;
import net.momirealms.customnameplates.bukkit.compatibility.NameplatesExpansion;
import net.momirealms.customnameplates.bukkit.compatibility.cosmetic.MagicCosmeticsHook;
import net.momirealms.customnameplates.bukkit.requirement.BukkitRequirementManager;
import net.momirealms.customnameplates.bukkit.scheduler.BukkitSchedulerAdapter;
import net.momirealms.customnameplates.common.dependency.Dependency;
import net.momirealms.customnameplates.common.dependency.DependencyManagerImpl;
import net.momirealms.customnameplates.common.event.EventManager;
import net.momirealms.customnameplates.common.locale.TranslationManager;
import net.momirealms.customnameplates.common.plugin.classpath.ClassPathAppender;
import net.momirealms.customnameplates.common.plugin.classpath.ReflectionClassPathAppender;
import net.momirealms.customnameplates.common.plugin.logging.JavaPluginLogger;
import net.momirealms.customnameplates.common.plugin.logging.PluginLogger;
import net.momirealms.customnameplates.common.plugin.scheduler.AbstractJavaScheduler;
import net.momirealms.customnameplates.common.plugin.scheduler.SchedulerAdapter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BukkitCustomNameplates extends CustomNameplates implements Listener {

    private static BukkitCustomNameplates instance;

    private final ClassPathAppender classPathAppender;
    private final PluginLogger logger;
    private final AbstractJavaScheduler<Location> scheduler;

    private BukkitSenderFactory senderFactory;
    private BukkitCommandManager commandManager;

    private final JavaPlugin bootstrap;

    private final List<JoinQuitListener> joinQuitListeners = new ArrayList<>();
    private final List<PlayerListener> playerListeners = new ArrayList<>();

    private boolean loaded = false;

    private String buildByBit = "%%__BUILTBYBIT__%%";
    private String polymart = "%%__POLYMART__%%";
    private String time = "%%__TIMESTAMP__%%";
    private String user = "%%__USER__%%";
    private String username = "%%__USERNAME__%%";

    private boolean isLatest = false;

    public BukkitCustomNameplates(JavaPlugin bootstrap) {
        this.bootstrap = bootstrap;
        VersionHelper.init(getServerVersion());
        this.scheduler = new BukkitSchedulerAdapter(this);
        this.classPathAppender = new ReflectionClassPathAppender(this);
        this.logger = new JavaPluginLogger(getBootstrap().getLogger());
        this.dependencyManager = new DependencyManagerImpl(this);
        instance = this;
    }

    @Override
    public void load() {
        this.dependencyManager.loadDependencies(
                List.of(
                        Dependency.BOOSTED_YAML,
                        Dependency.BSTATS_BASE, Dependency.BSTATS_BUKKIT,
                        Dependency.CAFFEINE,
                        Dependency.GEANTY_REF,
                        Dependency.CLOUD_CORE, Dependency.CLOUD_SERVICES, Dependency.CLOUD_BUKKIT, Dependency.CLOUD_PAPER, Dependency.CLOUD_BRIGADIER, Dependency.CLOUD_MINECRAFT_EXTRAS,
                        Dependency.GSON,
                        Dependency.COMMONS_POOL_2,
                        Dependency.JEDIS,
                        Dependency.MYSQL_DRIVER, Dependency.MARIADB_DRIVER,
                        Dependency.SQLITE_DRIVER, Dependency.SLF4J_API, Dependency.SLF4J_SIMPLE,
                        Dependency.H2_DRIVER,
                        Dependency.MONGODB_DRIVER_CORE, Dependency.MONGODB_DRIVER_SYNC, Dependency.MONGODB_DRIVER_BSON,
                        Dependency.HIKARI_CP,
                        Dependency.BYTE_BUDDY,
                        Dependency.COMMONS_IO,
                        Dependency.LWJGL, Dependency.LWJGL_NATIVES, Dependency.LWJGL_FREETYPE, Dependency.LWJGL_FREETYPE_NATIVES
                )
        );
    }

    @Override
    public void enable() {
        if (!VersionHelper.isVersionNewerThan1_19_4()) {
            getPluginLogger().severe("CustomNameplates only supports 1.19.4+ servers");
            Bukkit.getPluginManager().disablePlugin(this.getBootstrap());
            return;
        }

        BukkitNetworkManager networkManager = new BukkitNetworkManager(this);
        this.mainTask = new MainTask(this);
        this.packetSender = networkManager;
        this.pipelineInjector = networkManager;
        this.commandManager = new BukkitCommandManager(this);
        this.senderFactory = new BukkitSenderFactory(this);
        this.platform = new BukkitPlatform(this);
        this.senderFactory = new BukkitSenderFactory(this);
        this.configManager = new BukkitConfigManager(this);
        this.translationManager = new TranslationManager(this);
        this.actionBarManager = new ActionBarManagerImpl(this);
        this.bossBarManager = new BossBarManagerImpl(this);
        this.advanceManager = new AdvanceManagerImpl(this);
        this.backgroundManager = new BackgroundManagerImpl(this);
        this.bubbleManager = new BubbleManagerImpl(this);
        this.nameplateManager = new NameplateManagerImpl(this);
        this.placeholderManager = new PlaceholderManagerImpl(this);
        this.imageManager = new ImageManagerImpl(this);
        this.unlimitedTagManager = new UnlimitedTagManagerImpl(this);
        this.requirementManager = new BukkitRequirementManager(this);
        this.storageManager = new StorageManagerImpl(this);
        this.chatManager = new BukkitChatManager(this);
        this.resourcePackManager = new ResourcePackManagerImpl(this);
        this.eventManager = EventManager.create(this);
        this.api = new CustomNameplatesAPI(this);

        this.joinQuitListeners.add((JoinQuitListener) storageManager);
        this.joinQuitListeners.add((JoinQuitListener) actionBarManager);
        this.joinQuitListeners.add((JoinQuitListener) bossBarManager);
        this.joinQuitListeners.add((JoinQuitListener) unlimitedTagManager);
        this.playerListeners.add((PlayerListener) unlimitedTagManager);
        this.chatManager.registerListener((ChatListener) bubbleManager);

        Bukkit.getPluginManager().registerEvents(this, getBootstrap());

        this.commandManager.registerDefaultFeatures();
        this.reload();

        this.loaded = true;

        if (ConfigManager.metrics()) new Metrics(getBootstrap(), 16649);
        if (ConfigManager.generateOnStart()) {
            this.resourcePackManager.generate();
        }
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new NameplatesExpansion(this).register();
        }
        if (Bukkit.getPluginManager().isPluginEnabled("MagicCosmetics")) {
            try {
                Bukkit.getPluginManager().registerEvents(new MagicCosmeticsHook(this), this.getBootstrap());
            } catch (Exception ignore) {
            }
        }

        boolean downloadFromPolymart = polymart.equals("1");
        boolean downloadFromBBB = buildByBit.equals("true");

        if (ConfigManager.checkUpdate()) {
            VersionHelper.UPDATE_CHECKER.apply(this).thenAccept(result -> {
                String link;
                if (downloadFromPolymart) {
                    link = "https://polymart.org/resource/2543/";
                } else if (downloadFromBBB) {
                    link = "https://builtbybit.com/resources/36359/";
                } else {
                    link = "https://github.com/Xiao-MoMi/Custom-Nameplates/";
                }
                if (!result) {
                    this.getPluginLogger().info("You are using the latest version.");
                    isLatest = true;
                } else {
                    this.getPluginLogger().warn("Update is available: " + link);
                    isLatest = false;
                }
            });
        }
    }

    @Override
    public void disable() {
        if (!this.loaded) return;
        if (this.scheduledMainTask != null) this.scheduledMainTask.cancel();

        if (configManager != null) this.configManager.disable();
        if (actionBarManager != null) this.actionBarManager.disable();
        if (bossBarManager != null) this.bossBarManager.disable();
        if (unlimitedTagManager != null) this.unlimitedTagManager.disable();
        if (advanceManager != null) this.advanceManager.disable();
        if (backgroundManager != null) this.backgroundManager.disable();
        if (requirementManager != null) this.requirementManager.disable();
        if (placeholderManager != null) this.placeholderManager.disable();
        if (storageManager != null) this.storageManager.disable();
        if (bubbleManager != null) this.bubbleManager.disable();
        if (nameplateManager != null) this.nameplateManager.disable();
        if (imageManager != null) this.imageManager.disable();
        if (chatManager != null) this.chatManager.disable();

        if (commandManager != null) this.commandManager.unregisterFeatures();
        this.joinQuitListeners.clear();
        HandlerList.unregisterAll(this);

        AdventureHelper.clearCache();

        this.loaded = false;
    }

    @Override
    public void reload() {
        AdventureHelper.clearCache();
        // cancel task
        if (this.scheduledMainTask != null)
            this.scheduledMainTask.cancel();
        // reset ticks
        MainTask.reset();
        // reload players
        for (CNPlayer player : getOnlinePlayers()) {
            ((AbstractCNPlayer) player).reload();
        }
        // load basics
        this.configManager.reload();
        this.storageManager.reload();
        this.translationManager.reload();
        // prepare resources
        this.backgroundManager.reload();
        this.bubbleManager.reload();
        this.nameplateManager.reload();
        this.imageManager.reload();
        this.advanceManager.reload();
        // set up placeholders
        this.placeholderManager.reload();
        // set up requirements
        this.requirementManager.reload();
        // use the placeholders
        this.actionBarManager.reload();
        this.bossBarManager.reload();
        this.unlimitedTagManager.reload();
        this.chatManager.reload();
        // dispatch the event
        this.eventManager.dispatch(NameplatesReloadEvent.class);
        // run task
        this.scheduledMainTask = getScheduler().asyncRepeating(mainTask, 50, 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isUpToDate() {
        return isLatest;
    }

    @Override
    public InputStream getResourceStream(String filePath) {
        return getBootstrap().getResource(filePath.replace("\\", "/"));
    }

    @Override
    public PluginLogger getPluginLogger() {
        return logger;
    }

    @Override
    public ClassPathAppender getClassPathAppender() {
        return classPathAppender;
    }

    @Override
    public SchedulerAdapter<Location> getScheduler() {
        return scheduler;
    }

    @Override
    public Path getDataDirectory() {
        return getBootstrap().getDataFolder().toPath().toAbsolutePath();
    }

    @Override
    public String getServerVersion() {
        return Bukkit.getServer().getBukkitVersion().split("-")[0];
    }

    @SuppressWarnings("deprecation")
    @Override
    public String getPluginVersion() {
        return getBootstrap().getDescription().getVersion();
    }

    public JavaPlugin getBootstrap() {
        return bootstrap;
    }

    public static BukkitCustomNameplates getInstance() {
        return instance;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        CNPlayer cnPlayer = new BukkitCNPlayer(this, event.getPlayer());
        CNPlayer previous = onlinePlayerMap.put(cnPlayer.uuid(), cnPlayer);
        if (previous != null) {
            getPluginLogger().severe("Player " + event.getPlayer().getName() + " is duplicated");
        }

        entityIDFastLookup.put(cnPlayer.entityID(), cnPlayer);
        pipelineInjector.inject(cnPlayer);

        for (JoinQuitListener listener : joinQuitListeners) {
            listener.onPlayerJoin(cnPlayer);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        CNPlayer cnPlayer = onlinePlayerMap.remove(event.getPlayer().getUniqueId());
        if (cnPlayer == null) {
            getPluginLogger().severe("Player " + event.getPlayer().getName() + " is not recorded by CustomNameplates");
            return;
        }

        entityIDFastLookup.remove(cnPlayer.entityID());
        pipelineInjector.uninject(cnPlayer);

        for (JoinQuitListener listener : joinQuitListeners) {
            listener.onPlayerQuit(cnPlayer);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        CNPlayer cnPlayer = getPlayer(event.getPlayer().getUniqueId());
        if (cnPlayer != null) {
            for (PlayerListener listener : playerListeners) {
                listener.onChangeWorld(cnPlayer);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onRespawn(PlayerRespawnEvent event) {
        CNPlayer cnPlayer = getPlayer(event.getPlayer().getUniqueId());
        if (cnPlayer != null) {
            for (PlayerListener listener : playerListeners) {
                listener.onRespawn(cnPlayer);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        CNPlayer cnPlayer = getPlayer(event.getPlayer().getUniqueId());
        if (cnPlayer != null) {
            for (PlayerListener listener : playerListeners) {
                listener.onTeleport(cnPlayer);
            }
        }
    }

    public BukkitSenderFactory getSenderFactory() {
        return senderFactory;
    }

    public void registerJoinQuitListener(JoinQuitListener listener) {
        this.joinQuitListeners.add(listener);
    }

    public void unregisterJoinQuitListener(JoinQuitListener listener) {
        this.joinQuitListeners.remove(listener);
    }
}
