package net.momirealms.customnameplates.sponge;

import com.google.inject.Injector;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.helper.VersionHelper;
import net.momirealms.customnameplates.common.dependency.Dependency;
import net.momirealms.customnameplates.common.plugin.classpath.ClassPathAppender;
import net.momirealms.customnameplates.common.plugin.classpath.ReflectionClassPathAppender;
import net.momirealms.customnameplates.common.plugin.logging.Log4jPluginLogger;
import net.momirealms.customnameplates.common.plugin.logging.PluginLogger;
import net.momirealms.customnameplates.common.plugin.scheduler.AbstractJavaScheduler;
import net.momirealms.customnameplates.common.plugin.scheduler.SchedulerAdapter;
import net.momirealms.customnameplates.sponge.scheduler.SpongeSchedulerAdaptor;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Platform;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.metadata.PluginMetadata;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

public class SpongeCustomNameplates extends CustomNameplates {

    private final AbstractJavaScheduler<Void> scheduler;
    private Supplier<Injector> loader;
    private final PluginContainer pluginContainer;
    private final PluginLogger logger;
    private final ClassPathAppender classPathAppender;
    private final Game game;

    public SpongeCustomNameplates(Supplier<Injector> loader) {
        this.loader = loader;
        Injector injector = loader.get();
        this.logger = new Log4jPluginLogger(injector.getInstance(Logger.class));
        this.game = injector.getInstance(Game.class);
        this.pluginContainer = injector.getInstance(PluginContainer.class);
        injector.injectMembers(this);
        this.scheduler = new SpongeSchedulerAdaptor(this);
        this.classPathAppender = new ReflectionClassPathAppender(getClass().getClassLoader());
    }

    public PluginContainer pluginContainer() {
        return pluginContainer;
    }

    public Game game() {
        return game;
    }

    @Override
    public void enable() {
        if (!VersionHelper.isVersionNewerThan1_19_4()) {
            getPluginLogger().severe("CustomNameplates only supports 1.19.4+ servers");
            return;
        }
        super.enable();
    }

    @Override
    public void reload() {

    }

    @Override
    public void load() {
        this.dependencyManager.loadDependencies(
                List.of(
                        Dependency.BOOSTED_YAML,
                        Dependency.BSTATS_BASE, Dependency.BSTATS_SPONGE,
                        Dependency.CAFFEINE,
                        Dependency.GEANTY_REF,
                        Dependency.CLOUD_CORE, Dependency.CLOUD_SERVICES, Dependency.CLOUD_SPONGE, Dependency.CLOUD_BRIGADIER, Dependency.CLOUD_MINECRAFT_EXTRAS,
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
    public void disable() {

    }

    @Override
    public InputStream getResourceStream(String filePath) {
        return getClass().getClassLoader().getResourceAsStream(filePath.replace("\\", "/"));
    }

    @Override
    public PluginLogger getPluginLogger() {
        return logger;
    }

    @Override
    public ClassPathAppender getClassPathAppender() {
        return this.classPathAppender;
    }

    @Override
    public SchedulerAdapter<?> getScheduler() {
        return this.scheduler;
    }

    @Override
    public Path getDataDirectory() {
        return this.game.gameDirectory().toAbsolutePath().resolve("customnameplates");
    }

    @Override
    public String getServerVersion() {
        PluginMetadata api = this.game.platform().container(Platform.Component.API).metadata();
        PluginMetadata impl = this.game.platform().container(Platform.Component.IMPLEMENTATION).metadata();
        return api.name().orElse("API") + ": " + api.version() + " - " + impl.name().orElse("Impl") + ": " + impl.version();
    }

    @Override
    public String getPluginVersion() {
        return this.pluginContainer.metadata().version().toString();
    }
}


