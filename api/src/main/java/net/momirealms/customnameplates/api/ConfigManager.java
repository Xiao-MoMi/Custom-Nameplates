package net.momirealms.customnameplates.api;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.libs.org.snakeyaml.engine.v2.common.ScalarStyle;
import dev.dejvokep.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Tag;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import dev.dejvokep.boostedyaml.utils.format.NodeRole;
import net.momirealms.customnameplates.api.helper.AdventureHelper;
import net.momirealms.customnameplates.common.locale.TranslationManager;
import net.momirealms.customnameplates.common.plugin.CustomNameplatesProperties;
import net.momirealms.customnameplates.common.plugin.config.ConfigLoader;
import net.momirealms.customnameplates.common.plugin.feature.Reloadable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class ConfigManager implements ConfigLoader, Reloadable {

    private static YamlDocument MAIN_CONFIG;

    public static YamlDocument getMainConfig() {
        if (MAIN_CONFIG == null) {
            throw new IllegalStateException("Main config not loaded");
        }
        return MAIN_CONFIG;
    }

    private static ConfigManager instance;

    protected final CustomNameplates plugin;

    protected boolean debug;
    protected boolean checkUpdate;
    protected boolean metrics;
    protected boolean actionbarModule;
    protected boolean imageModule;
    protected boolean bossBarModule;
    protected boolean bubbleModule;
    protected boolean nameplateModule;
    protected boolean backgroundModule;

    protected int defaultRefreshInterval;

    public ConfigManager(CustomNameplates plugin) {
        this.plugin = plugin;
        instance = this;
    }

    @Override
    public void load() {
        String configVersion = CustomNameplatesProperties.getValue("config");
        try (InputStream inputStream = new FileInputStream(resolveConfig("config.yml").toFile())) {
            MAIN_CONFIG = YamlDocument.create(
                    inputStream,
                    plugin.getResourceStream("config.yml"),
                    GeneralSettings.builder()
                            .setRouteSeparator('.')
                            .setUseDefaults(false)
                            .build(),
                    LoaderSettings
                            .builder()
                            .setAutoUpdate(true)
                            .build(),
                    DumperSettings.builder()
                            .setScalarFormatter((tag, value, role, def) -> {
                                if (role == NodeRole.KEY) {
                                    return ScalarStyle.PLAIN;
                                } else {
                                    return tag == Tag.STR ? ScalarStyle.DOUBLE_QUOTED : ScalarStyle.PLAIN;
                                }
                            })
                            .build(),
                    UpdaterSettings
                            .builder()
                            .setVersioning(new BasicVersioning("config-version"))
                            .addIgnoredRoute(configVersion, "other-settings.placeholder-refresh-interval", '.')
                            .addIgnoredRoute(configVersion, "other-settings.font-templates", '.')
                            .build()
            );
            MAIN_CONFIG.save(resolveConfig("config.yml").toFile());
            loadSettings();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadSettings() {
        YamlDocument config = MAIN_CONFIG;
        AdventureHelper.legacySupport = config.getBoolean("other-settings.legacy-color-code-support", true);
        TranslationManager.forceLocale(TranslationManager.parseLocale(config.getString("force-locale", "")));

        // Basics
        debug = config.getBoolean("debug", false);
        metrics = config.getBoolean("metrics", false);
        checkUpdate = config.getBoolean("update-checker", false);

        // Modules
        actionbarModule = config.getBoolean("modules.actionbars", true);
        imageModule = config.getBoolean("modules.images", true);
        bossBarModule = config.getBoolean("modules.bossbars", true);
        bubbleModule = config.getBoolean("modules.bubbles", true);
        backgroundModule = config.getBoolean("modules.backgrounds", true);
        nameplateModule = config.getBoolean("modules.nameplates", true);

        // Integrations

        // Other settings
        defaultRefreshInterval = config.getInt("other-settings.default-placeholder-refresh-interval", 10);
    }

    @Override
    public void unload() {
        Reloadable.super.unload();
    }

    public static boolean actionbarModule() {
        return instance.actionbarModule;
    }

    public static boolean imageModule() {
        return instance.imageModule;
    }

    public static boolean bossbarModule() {
        return instance.bossBarModule;
    }

    public static boolean bubbleModule() {
        return instance.bubbleModule;
    }

    public static boolean backgroundModule() {
        return instance.backgroundModule;
    }

    public static boolean nameplateModule() {
        return instance.nameplateModule;
    }

    public static boolean debug() {
        return instance.debug;
    }

    public static boolean checkUpdate() {
        return instance.checkUpdate;
    }

    public static boolean metrics() {
        return instance.metrics;
    }

    public static int defaultRefreshInterval() {
        return instance.defaultRefreshInterval;
    }

    @Override
    public YamlDocument loadConfig(String filePath) {
        return loadConfig(filePath, '.');
    }

    @Override
    public YamlDocument loadConfig(String filePath, char routeSeparator) {
        try (InputStream inputStream = new FileInputStream(resolveConfig(filePath).toFile())) {
            return YamlDocument.create(
                    inputStream,
                    plugin.getResourceStream(filePath),
                    GeneralSettings.builder().setRouteSeparator(routeSeparator).build(),
                    LoaderSettings
                            .builder()
                            .setAutoUpdate(true)
                            .build(),
                    DumperSettings.builder()
                            .setScalarFormatter((tag, value, role, def) -> {
                                if (role == NodeRole.KEY) {
                                    return ScalarStyle.PLAIN;
                                } else {
                                    return tag == Tag.STR ? ScalarStyle.DOUBLE_QUOTED : ScalarStyle.PLAIN;
                                }
                            })
                            .build(),
                    UpdaterSettings
                            .builder()
                            .setVersioning(new BasicVersioning("config-version"))
                            .build()
            );
        } catch (IOException e) {
            plugin.getPluginLogger().severe("Failed to load config " + filePath, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public YamlDocument loadData(File file) {
        try (InputStream inputStream = new FileInputStream(file)) {
            return YamlDocument.create(inputStream);
        } catch (IOException e) {
            plugin.getPluginLogger().severe("Failed to load config " + file, e);
            throw new RuntimeException(e);
        }
    }

    protected Path resolveConfig(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
        filePath = filePath.replace('\\', '/');
        Path configFile = plugin.getConfigDirectory().resolve(filePath);
        // if the config doesn't exist, create it based on the template in the resources dir
        if (!Files.exists(configFile)) {
            try {
                Files.createDirectories(configFile.getParent());
            } catch (IOException e) {
                // ignore
            }
            try (InputStream is = plugin.getResourceStream(filePath)) {
                if (is == null) {
                    throw new IllegalArgumentException("The embedded resource '" + filePath + "' cannot be found");
                }
                Files.copy(is, configFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return configFile;
    }
}
