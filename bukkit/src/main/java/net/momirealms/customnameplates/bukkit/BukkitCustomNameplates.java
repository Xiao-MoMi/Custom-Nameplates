package net.momirealms.customnameplates.bukkit;

import net.momirealms.customnameplates.api.AbstractCNPlayer;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.JoinQuitListener;
import net.momirealms.customnameplates.api.feature.actionbar.ActionBarManagerImpl;
import net.momirealms.customnameplates.api.feature.bossbar.BossBarManagerImpl;
import net.momirealms.customnameplates.api.helper.VersionHelper;
import net.momirealms.customnameplates.api.placeholder.PlaceholderManagerImpl;
import net.momirealms.customnameplates.bukkit.command.BukkitCommandManager;
import net.momirealms.customnameplates.bukkit.requirement.BukkitRequirementManager;
import net.momirealms.customnameplates.bukkit.scheduler.BukkitSchedulerAdapter;
import net.momirealms.customnameplates.common.dependency.Dependency;
import net.momirealms.customnameplates.common.dependency.DependencyManagerImpl;
import net.momirealms.customnameplates.common.locale.TranslationManager;
import net.momirealms.customnameplates.common.plugin.classpath.ClassPathAppender;
import net.momirealms.customnameplates.common.plugin.classpath.ReflectionClassPathAppender;
import net.momirealms.customnameplates.common.plugin.logging.JavaPluginLogger;
import net.momirealms.customnameplates.common.plugin.logging.PluginLogger;
import net.momirealms.customnameplates.common.plugin.scheduler.AbstractJavaScheduler;
import net.momirealms.customnameplates.common.plugin.scheduler.SchedulerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BukkitCustomNameplates extends CustomNameplates implements Listener {

    private static BukkitCustomNameplates instance;

    private final ClassPathAppender classPathAppender;
    private final PluginLogger logger;
    private final AbstractJavaScheduler<Location> scheduler;

    private BukkitSenderFactory senderFactory;
    private BukkitCommandManager commandManager;

    private final JavaPlugin bootstrap;

    private final List<JoinQuitListener> joinQuitListeners = new ArrayList<>();

    private boolean loaded = false;

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
                        Dependency.HIKARI_CP
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
        this.packetSender = networkManager;
        this.pipelineInjector = networkManager;
        this.commandManager = new BukkitCommandManager(this);
        this.senderFactory = new BukkitSenderFactory(this);
        this.platform = new BukkitPlatform(this);
        this.senderFactory = new BukkitSenderFactory(this);
        this.configManager = new BukkitConfigManager(this);
        this.trackerManager = new BukkitTrackerManager(this);
        this.translationManager = new TranslationManager(this);
        this.placeholderManager = new PlaceholderManagerImpl(this);
        this.actionBarManager = new ActionBarManagerImpl(this);
        this.bossBarManager = new BossBarManagerImpl(this);
        this.requirementManager = new BukkitRequirementManager(this);

        this.joinQuitListeners.add((JoinQuitListener) actionBarManager);
        this.joinQuitListeners.add((JoinQuitListener) bossBarManager);

        Bukkit.getPluginManager().registerEvents(this, getBootstrap());

        this.commandManager.registerDefaultFeatures();
        this.reload();

        this.loaded = true;
    }

    @Override
    public void disable() {
        if (!this.loaded) return;
        super.disable();

        this.configManager.disable();
        this.actionBarManager.disable();
        this.bossBarManager.disable();

        this.commandManager.unregisterFeatures();
        HandlerList.unregisterAll(this);

        this.loaded = false;
    }

    @Override
    public void reload() {
        super.reload();
        for (CNPlayer<?> player : getOnlinePlayers()) {
            ((AbstractCNPlayer<?>) player).reload();
        }
        this.configManager.reload();
        this.placeholderManager.reload();
        this.translationManager.reload();
        this.actionBarManager.reload();
        this.bossBarManager.reload();
        this.requirementManager.reload();
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
    public SchedulerAdapter<?> getScheduler() {
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

    @Override
    public BukkitNetworkManager getPacketSender() {
        return (BukkitNetworkManager) packetSender;
    }

    public JavaPlugin getBootstrap() {
        return bootstrap;
    }

    public static BukkitCustomNameplates getInstance() {
        return instance;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        CNPlayer<?> cnPlayer = new BukkitCNPlayer(this, event.getPlayer());
        CNPlayer<?> previous = onlinePlayerMap.put(cnPlayer.uuid(), cnPlayer);
        if (previous != null) {
            getPluginLogger().severe("Player " + event.getPlayer().getName() + " is duplicated");
        }

        pipelineInjector.inject(cnPlayer);

        for (JoinQuitListener listener : joinQuitListeners) {
            listener.onPlayerJoin(cnPlayer);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        CNPlayer<?> cnPlayer = onlinePlayerMap.remove(event.getPlayer().getUniqueId());
        if (cnPlayer == null) {
            getPluginLogger().severe("Player " + event.getPlayer().getName() + " is not recorded by CustomNameplates");
            return;
        }

        pipelineInjector.uninject(cnPlayer);

        for (JoinQuitListener listener : joinQuitListeners) {
            listener.onPlayerQuit(cnPlayer);
        }
    }

    public BukkitSenderFactory getSenderFactory() {
        return senderFactory;
    }
}
