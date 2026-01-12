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
import net.momirealms.customnameplates.api.feature.CharacterArranger;
import net.momirealms.customnameplates.api.feature.bossbar.BossBar;
import net.momirealms.customnameplates.api.helper.AdventureHelper;
import net.momirealms.customnameplates.api.helper.VersionHelper;
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
import java.util.Locale;

/**
 * Manages the loading and handling of configuration files for the Custom Nameplates plugin.
 */
public abstract class ConfigManager implements ConfigLoader, Reloadable {
    /**
     * The main configuration document that stores all settings for the plugin.
     * <p>
     * This document is loaded during the initialization of the plugin and is used to retrieve various configuration values.
     * </p>
     */
    private static YamlDocument MAIN_CONFIG;

    /**
     * Returns the main configuration document.
     * <p>
     * If the configuration has not been loaded yet, an {@code IllegalStateException} will be thrown.
     * </p>
     *
     * @return The main configuration document
     * @throws IllegalStateException If the configuration has not been loaded
     */
    public static YamlDocument getMainConfig() {
        if (MAIN_CONFIG == null) {
            throw new IllegalStateException("Main config not loaded");
        }
        return MAIN_CONFIG;
    }

    /**
     * The singleton instance of the {@code ConfigManager}.
     */
    private static ConfigManager instance;
    /**
     * The {@code CustomNameplates} plugin instance associated with this {@code ConfigManager}.
     */
    protected final CustomNameplates plugin;
    /**
     * Indicates whether debugging is enabled.
     */
    protected boolean debug;
    /**
     * Indicates whether plugin updates are checked automatically.
     */
    protected boolean checkUpdate;
    /**
     * Metrics flag to enable or disable the metrics system.
     */
    protected boolean metrics;

    /**
     * Flag to enable or disable the nametag module.
     */
    protected boolean nametagModule;

    /**
     * Flag to enable or disable the action bar module.
     */
    protected boolean actionbarModule;

    /**
     * Flag to enable or disable the image module.
     */
    protected boolean imageModule;

    /**
     * Flag to enable or disable the boss bar module.
     */
    protected boolean bossBarModule;

    /**
     * Flag to enable or disable the bubble module.
     */
    protected boolean bubbleModule;

    /**
     * Flag to enable or disable the nameplate module.
     */
    protected boolean nameplateModule;

    /**
     * Flag to enable or disable the background module.
     */
    protected boolean backgroundModule;

    /**
     * Flag to hide or display team names.
     */
    protected boolean hideTeamNames;

    /**
     * The default placeholder refresh interval (in seconds).
     */
    protected int defaultPlaceholderRefreshInterval;

    /**
     * The default condition refresh interval (in seconds).
     */
    protected int defaultConditionRefreshInterval;

    /**
     * The delay (in milliseconds) before sending updates.
     */
    protected int delaySend;

    /**
     * Flag to catch other action bar events.
     */
    protected boolean catchOtherActionBar;

    /**
     * Flag to display system chat messages.
     */
    protected boolean displaySystemChat;

    /**
     * The duration (in seconds) that other action bars will stay visible.
     */
    protected int otherActionBarStayTime;

    /**
     * The namespace used for resource identification.
     */
    protected String namespace;

    /**
     * The font used for rendering text.
     */
    protected String font;

    /**
     * Flag to generate configurations on plugin start.
     */
    protected boolean generateOnStart;

    /**
     * The initial character used in text or formatting.
     */
    protected char initialChar;

    /**
     * The path to the image resource.
     */
    protected String imagePath;

    /**
     * The path to the nameplate resource.
     */
    protected String nameplatePath;

    /**
     * The path to the bubble resource.
     */
    protected String bubblePath;

    /**
     * The path used for splitting space resources.
     */
    protected String spaceSplitPath;

    /**
     * The path to the background resource.
     */
    protected String backgroundPath;

    /**
     * Flag to enable or disable shader effects.
     */
    protected boolean enableShader;

    /**
     * Flag to hide or show scoreboard numbers.
     */
    protected boolean hideScoreBoardNumber;

    /**
     * Flag to enable or disable animated text.
     */
    protected boolean animatedText;

    /**
     * Flag to enable or disable the ItemsAdder effect.
     */
    protected boolean itemsAdderEffect;

    /**
     * The color used for the boss bar when removed.
     */
    protected BossBar.Color removedBarColor;

    /**
     * Flag to enable compatibility for version 1.20.2 of Minecraft.
     */
    protected boolean bossBar1_20_2;

    /**
     * Flag to enable compatibility for version 1.17 of Minecraft.
     */
    protected boolean bossBar1_17;

    /**
     * Flag to enable or disable legacy Unicode support.
     */
    protected boolean legacyUnicodes;

    /**
     * Flag to enable or disable ItemsAdder pack.
     */
    protected boolean packItemsAdder;

    /**
     * Flag to enable or disable legacy ItemsAdder pack.
     */
    protected boolean packItemsAdderLegacy;

    /**
     * Flag to enable or disable Oraxen pack.
     */
    protected boolean packOraxen;

    /**
     * Flag to enable or disable Nexo pack.
     */
    protected boolean packNexo;

    /**
     * Flag to enable or disable Creative Central pack.
     */
    protected boolean packCreativeCentral;

    /**
     * Flag to enable or disable CraftEngine pack.
     */
    protected boolean packCraftEngine;

    /**
     * Flag to enable or disable unsafe chat features.
     */
    protected boolean chatUnsafe;

    /**
     * Flag to enable or disable chat features for TR (text roleplay).
     */
    protected boolean chatTR;

    /**
     * Flag to enable or disable chat features for Venture.
     */
    protected boolean chatVenture;

    /**
     * Flag to enable or disable chat features for Husk.
     */
    protected boolean chatHusk;

    /**
     * Flag to enable or disable chat features for Carbon.
     */
    protected boolean chatCarbon;

    /**
     * Flag to enable or disable chat features for Advanced.
     */
    protected boolean chatAdvanced;

    /**
     * Flag to enable or disable chat features for Essentials.
     */
    protected boolean chatEss;

    /**
     * Flag to enable or disable chat features for ChatControlRed.
     */
    protected boolean chatChatControlRed;

    /**
     * Flag to enable or disable chat features for Chatty.
     */
    protected boolean chatChatty;

    /**
     * Flag to enable or disable chat features for Zel.
     */
    protected boolean chatZel;

    /**
     * Flag to enable or disable dialogue features for TW (text world).
     */
    protected boolean twDialogue;

    /**
     * Flag to enable or disable cinematic features for TW (text world).
     */
    protected boolean twCinematic;

    /**
     * The version of the current configuration.
     */
    protected String configVersion;

    protected float minPackVersion;
    protected boolean stripChatColorTags;

    /**
     * Constructs a new {@code ConfigManager} for the specified {@code CustomNameplates} plugin instance.
     *
     * @param plugin The {@code CustomNameplates} plugin instance
     */
    public ConfigManager(CustomNameplates plugin) {
        this.plugin = plugin;
        instance = this;
    }

    /**
     * Loads the configuration file from the specified path and initializes various settings.
     * <p>
     * This method reads the {@code config.yml} file and sets up all configuration options such as modules,
     * integrations, and resource packs. It also handles versioning and saves the loaded configuration back
     * to the file system.
     * </p>
     *
     * @throws RuntimeException If an error occurs while loading or saving the configuration
     */
    @Override
    public void load() {
        configVersion = CustomNameplatesProperties.getValue("config");
        MAIN_CONFIG = loadConfig("config.yml",
                GeneralSettings.builder()
                        .setRouteSeparator('.')
                        .setUseDefaults(false)
                        .build(),
                LoaderSettings
                        .builder()
                        .setAutoUpdate(true)
                        .build(),
                DumperSettings.builder()
                        .setEscapeUnprintable(false)
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
                        .addIgnoredRoute(configVersion, "other-settings.shift-fonts", '.')
                        .build());
        try {
            MAIN_CONFIG.save(resolveConfig("config.yml").toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadSettings();
    }

    /**
     * Loads the settings from the configuration document into various fields.
     * <p>
     * This method parses the {@code MAIN_CONFIG} document and assigns values to the class fields.
     * It also applies settings such as locale and legacy support.
     * </p>
     */
    private void loadSettings() {
        YamlDocument config = MAIN_CONFIG;
        AdventureHelper.legacySupport = config.getBoolean("other-settings.legacy-color-code-support", true);
        TranslationManager.forceLocale(TranslationManager.parseLocale(config.getString("force-locale", "")));

        // Basics
        debug = config.getBoolean("debug", false);
        metrics = config.getBoolean("metrics", false);
        checkUpdate = config.getBoolean("update-checker", false);

        // Modules
        nametagModule = config.getBoolean("modules.nametags", true);
        actionbarModule = config.getBoolean("modules.actionbars", true);
        imageModule = config.getBoolean("modules.images", true);
        bossBarModule = config.getBoolean("modules.bossbars", true);
        bubbleModule = config.getBoolean("modules.bubbles", true);
        backgroundModule = config.getBoolean("modules.backgrounds", true);
        nameplateModule = config.getBoolean("modules.nameplates", true);

        // Integrations
        packItemsAdder = config.getBoolean("integrations.resource-pack.ItemsAdder", false);
        packItemsAdderLegacy = config.getBoolean("integrations.resource-pack.ItemsAdder-old-method", false);
        if (packItemsAdder) packItemsAdderLegacy = false;
        packOraxen = config.getBoolean("integrations.resource-pack.Oraxen", false);
        packCreativeCentral = config.getBoolean("integrations.resource-pack.Creative-Central");
        packNexo = config.getBoolean("integrations.resource-pack.Nexo");
        packCraftEngine = false;

        chatUnsafe = config.getBoolean("other-settings.unsafe-chat-event", false);
        chatEss = config.getBoolean("integrations.chat.Essentials", false);
        chatCarbon = config.getBoolean("integrations.chat.CarbonChat", false);
        chatHusk = config.getBoolean("integrations.chat.HuskChat", false);
        chatAdvanced = config.getBoolean("integrations.chat.AdvancedChat", false);
        chatTR = config.getBoolean("integrations.chat.TrChat", false);
        chatVenture = config.getBoolean("integrations.chat.VentureChat", false);
        chatChatControlRed = config.getBoolean("integrations.chat.ChatControlRed", false);
        chatChatty = config.getBoolean("integrations.chat.Chatty", false);
        chatZel = config.getBoolean("integrations.chat.ZelChat", false);

        twDialogue = config.getBoolean("integrations.typewriter.dialogue", true);
        twCinematic = config.getBoolean("integrations.typewriter.cinematic", true);

        // Packs
        generateOnStart = !config.getBoolean("resource-pack.disable-generation-on-start", false);
        namespace = config.getString("resource-pack.namespace", "nameplates");
        font = config.getString("resource-pack.font", "default");
        initialChar = config.getChar("resource-pack.initial-char", '뀁');
        CharacterArranger.reset(initialChar);

        nameplatePath = config.getString("resource-pack.image-path.nameplates", "font/nameplates/");
        backgroundPath = config.getString("resource-pack.image-path.backgrounds", "font/backgrounds/");
        imagePath = config.getString("resource-pack.image-path.images", "font/images/");
        bubblePath = config.getString("resource-pack.image-path.bubbles", "font/bubbles/");
        spaceSplitPath = config.getString("resource-pack.image-path.space-split", "font/base/");

        enableShader = config.getBoolean("resource-pack.shader.enable", true);
        hideScoreBoardNumber = config.getBoolean("resource-pack.shader.hide-scoreboard-number", false);
        animatedText = config.getBoolean("resource-pack.shader.animated-text", false);
        itemsAdderEffect = config.getBoolean("resource-pack.shader.ItemsAdder-text-effects", false);

        removedBarColor = BossBar.Color.valueOf(config.getString("resource-pack.transparent-bossbar.color", "YELLOW").toUpperCase(Locale.ENGLISH));
        bossBar1_20_2 = config.getBoolean("resource-pack.transparent-bossbar.1_20_2+", true);
        bossBar1_17 = config.getBoolean("resource-pack.transparent-bossbar.1_17-1_20_1", true);

        legacyUnicodes = config.getBoolean("resource-pack.legacy-unicodes", true);

        minPackVersion = getVersion(config.getString("resource-pack.supported-version.min", "SERVER_VERSION"));

        // Other settings
        delaySend = config.getInt("other-settings.send-delay", 0);
        defaultPlaceholderRefreshInterval = config.getInt("other-settings.default-placeholder-refresh-interval", 1);
        defaultConditionRefreshInterval = config.getInt("other-settings.default-condition-refresh-interval", 20);
        catchOtherActionBar = config.getBoolean("other-settings.catch-other-plugin-actionbar", true);
        otherActionBarStayTime = config.getInt("other-settings.other-actionbar-stay-time", 3000);
        displaySystemChat = config.getBoolean("other-settings.display-system-actionbar", true);
        hideTeamNames = config.getBoolean("other-settings.hide-team-names", true);
        stripChatColorTags = config.getBoolean("other-settings.strip-chat-color-tags", false);
    }

    @Override
    public void unload() {
        Reloadable.super.unload();
    }

    private static float getVersion(String version) {
        if (version.equalsIgnoreCase("SERVER_VERSION")) {
            return VersionHelper.version();
        }
        String[] split = version.split("\\.", 2);
        if (split.length != 2) {
            throw new IllegalArgumentException("Invalid version: " + version);
        }
        return Float.parseFloat(split[1]);
    }

    public static float minPackVersion() {
        return instance.minPackVersion;
    }

    /**
     * Retrieves the delay before sending updates.
     * @return the delay time (in milliseconds).
     */
    public static int delaySend() {
        return instance.delaySend;
    }

    /**
     * Retrieves the path used for splitting space resources.
     * @return the space split path.
     */
    public static String spaceSplitPath() {
        return instance.spaceSplitPath;
    }

    /**
     * Retrieves the path to the bubble resource.
     * @return the bubble path.
     */
    public static String bubblePath() {
        return instance.bubblePath;
    }

    /**
     * Retrieves the path to the image resource.
     * @return the image path.
     */
    public static String imagePath() {
        return instance.imagePath;
    }

    /**
     * Retrieves the path to the background resource.
     * @return the background path.
     */
    public static String backgroundPath() {
        return instance.backgroundPath;
    }

    /**
     * Retrieves the path to the nameplate resource.
     * @return the nameplate path.
     */
    public static String nameplatePath() {
        return instance.nameplatePath;
    }

    /**
     * Retrieves whether configuration generation is enabled on startup.
     * @return true if configuration generation is enabled on start; false otherwise.
     */
    public static boolean generateOnStart() {
        return instance.generateOnStart;
    }

    /**
     * Retrieves the namespace used for resource identification.
     * @return the namespace.
     */
    public static String namespace() {
        return instance.namespace;
    }

    /**
     * Retrieves the font used for rendering text.
     * @return the font.
     */
    public static String font() {
        return instance.font;
    }

    /**
     * Retrieves the version of the current configuration.
     * @return the config version.
     */
    public static String configVersion() {
        return instance.configVersion;
    }

    /**
     * Retrieves the initial character used for formatting.
     * @return the initial character.
     */
    public static char initialChar() {
        return instance.initialChar;
    }

    /**
     * Retrieves whether team names are hidden.
     * @return true if team names are hidden; false otherwise.
     */
    public static boolean hideTeamNames() {
        return instance.hideTeamNames;
    }

    /**
     * Retrieves whether the action bar module is enabled.
     * @return true if the action bar module is enabled; false otherwise.
     */
    public static boolean actionbarModule() {
        return instance.actionbarModule;
    }

    /**
     * Retrieves whether the nametag module is enabled.
     * @return true if the nametag module is enabled; false otherwise.
     */
    public static boolean nametagModule() {
        return instance.nametagModule;
    }

    /**
     * Retrieves whether other action bars should be caught.
     * @return true if other action bars are caught; false otherwise.
     */
    public static boolean catchOtherActionBar() {
        return instance.catchOtherActionBar;
    }

    /**
     * Retrieves whether system chat should be displayed.
     * @return true if system chat is displayed; false otherwise.
     */
    public static boolean displaySystemChat() {
        return instance.displaySystemChat;
    }

    /**
     * Retrieves whether the ItemsAdder pack is enabled.
     * @return true if the ItemsAdder pack is enabled; false otherwise.
     */
    public static boolean packItemsAdder() {
        return instance.packItemsAdder;
    }

    /**
     * Retrieves whether the legacy ItemsAdder pack is enabled.
     * @return true if the legacy ItemsAdder pack is enabled; false otherwise.
     */
    public static boolean packItemsAdderLegacy() {
        return instance.packItemsAdderLegacy;
    }

    /**
     * Retrieves whether the Oraxen pack is enabled.
     * @return true if the Oraxen pack is enabled; false otherwise.
     */
    public static boolean packOraxen() {
        return instance.packOraxen;
    }

    /**
     * Retrieves whether the Nexo pack is enabled.
     * @return true if the Nexo pack is enabled; false otherwise.
     */
    public static boolean packNexo() {
        return instance.packNexo;
    }

    /**
     * Retrieves whether the CraftEngine pack is enabled.
     * @return true if the CraftEngine pack is enabled; false otherwise.
     */
    public static boolean packCraftEngine() {
        return instance.packCraftEngine;
    }

    /**
     * Retrieves whether the Creative Central pack is enabled.
     * @return true if the Creative Central pack is enabled; false otherwise.
     */
    public static boolean packCreativeCentral() {
        return instance.packCreativeCentral;
    }

    /**
     * Retrieves whether the scoreboard numbers should be hidden.
     * @return true if scoreboard numbers are hidden; false otherwise.
     */
    public static boolean hideScoreBoardNumber() {
        return instance.hideScoreBoardNumber;
    }

    /**
     * Retrieves whether animated text is enabled.
     * @return true if animated text is enabled; false otherwise.
     */
    public static boolean animatedText() {
        return instance.animatedText;
    }

    /**
     * Retrieves whether the ItemsAdder effect is enabled.
     * @return true if the ItemsAdder effect is enabled; false otherwise.
     */
    public static boolean itemsAdderEffect() {
        return instance.itemsAdderEffect;
    }

    /**
     * Retrieves the color to be used for a removed boss bar.
     * @return the color for the removed bar.
     */
    public static BossBar.Color removedBarColor() {
        return instance.removedBarColor;
    }

    /**
     * Retrieves whether the image module is enabled.
     * @return true if the image module is enabled; false otherwise.
     */
    public static boolean imageModule() {
        return instance.imageModule;
    }

    /**
     * Retrieves whether the boss bar module is enabled.
     * @return true if the boss bar module is enabled; false otherwise.
     */
    public static boolean bossbarModule() {
        return instance.bossBarModule;
    }

    /**
     * Retrieves whether the bubble module is enabled.
     * @return true if the bubble module is enabled; false otherwise.
     */
    public static boolean bubbleModule() {
        return instance.bubbleModule;
    }

    /**
     * Retrieves whether the background module is enabled.
     * @return true if the background module is enabled; false otherwise.
     */
    public static boolean backgroundModule() {
        return instance.backgroundModule;
    }

    /**
     * Retrieves whether the nameplate module is enabled.
     * @return true if the nameplate module is enabled; false otherwise.
     */
    public static boolean nameplateModule() {
        return instance.nameplateModule;
    }

    /**
     * Retrieves whether debugging is enabled.
     * @return true if debugging is enabled; false otherwise.
     */
    public static boolean debug() {
        return instance.debug;
    }

    /**
     * Retrieves whether the plugin checks for updates.
     * @return true if update checks are enabled; false otherwise.
     */
    public static boolean checkUpdate() {
        return instance.checkUpdate;
    }

    /**
     * Retrieves whether the metrics system is enabled.
     * @return true if the metrics system is enabled; false otherwise.
     */
    public static boolean metrics() {
        return instance.metrics;
    }

    /**
     * Retrieves whether dialogue features for text world (TW) are enabled.
     * @return true if TW dialogue is enabled; false otherwise.
     */
    public static boolean twDialogue() {
        return instance.twDialogue;
    }

    /**
     * Retrieves whether cinematic features for text world (TW) are enabled.
     * @return true if TW cinematic is enabled; false otherwise.
     */
    public static boolean twCinematic() {
        return instance.twCinematic;
    }

    /**
     * Retrieves the default placeholder refresh interval (in seconds).
     * @return the default placeholder refresh interval.
     */
    public static int defaultPlaceholderRefreshInterval() {
        return instance.defaultPlaceholderRefreshInterval;
    }

    /**
     * Retrieves the default condition refresh interval (in seconds).
     * @return the default condition refresh interval.
     */
    public static int defaultConditionRefreshInterval() {
        return instance.defaultConditionRefreshInterval;
    }

    /**
     * Retrieves the duration (in seconds) that other action bars will stay visible.
     * @return the duration for other action bars to stay visible.
     */
    public static int otherActionBarStayTime() {
        return instance.otherActionBarStayTime;
    }

    /**
     * Retrieves whether shader effects are enabled.
     * @return true if shader effects are enabled; false otherwise.
     */
    public static boolean enableShader() {
        return instance.enableShader;
    }

    /**
     * Retrieves whether legacy Unicode support is enabled.
     * @return true if legacy Unicode support is enabled; false otherwise.
     */
    public static boolean legacyUnicodes() {
        return instance.legacyUnicodes;
    }

    /**
     * Retrieves whether compatibility with Minecraft 1.20.2 is enabled for the boss bar.
     * @return true if the boss bar is compatible with Minecraft 1.20.2; false otherwise.
     */
    public static boolean bossBar1_20_2() {
        return instance.bossBar1_20_2;
    }

    /**
     * Retrieves whether compatibility with Minecraft 1.17 is enabled for the boss bar.
     * @return true if the boss bar is compatible with Minecraft 1.17; false otherwise.
     */
    public static boolean bossBar1_17() {
        return instance.bossBar1_17;
    }

    /**
     * Retrieves whether unsafe chat features are enabled.
     * @return true if unsafe chat is enabled; false otherwise.
     */
    public static boolean chatUnsafe() {
        return instance.chatUnsafe;
    }

    /**
     * Retrieves whether TrChat features are enabled.
     * @return true if TR chat is enabled; false otherwise.
     */
    public static boolean chatTrChat() {
        return instance.chatTR;
    }

    /**
     * Retrieves whether VentureChat features are enabled.
     * @return true if Venture chat is enabled; false otherwise.
     */
    public static boolean chatVenture() {
        return instance.chatVenture;
    }

    /**
     * Retrieves whether HuskChat features are enabled.
     * @return true if Husk chat is enabled; false otherwise.
     */
    public static boolean chatHusk() {
        return instance.chatHusk;
    }

    /**
     * Retrieves whether Essentials chat features are enabled.
     * @return true if Essentials chat is enabled; false otherwise.
     */
    public static boolean chatEss() {
        return instance.chatEss;
    }

    /**
     * Retrieves whether CarbonChat features are enabled.
     * @return true if Carbon chat is enabled; false otherwise.
     */
    public static boolean chatCarbon() {
        return instance.chatCarbon;
    }

    /**
     * Retrieves whether AdvancedChat features are enabled.
     * @return true if Advanced chat is enabled; false otherwise.
     */
    public static boolean chatAdvanced() {
        return instance.chatAdvanced;
    }

    /**
     * Retrieves whether RedChatControl is enabled.
     * @return true if Red chat control is enabled; false otherwise.
     */
    public static boolean chatChatControlRed() {
        return instance.chatChatControlRed;
    }

    /**
     * Retrieves whether Chatty chat features are enabled.
     * @return true if Chatty chat is enabled; false otherwise.
     */
    public static boolean chatChatty() {
        return instance.chatChatty;
    }

    /**
     * Retrieves whether ZelChat chat features are enabled.
     * @return true if ZelChat chat is enabled; false otherwise.
     */
    public static boolean chatZel() {
        return instance.chatZel;
    }

    public static boolean stripChatColorTags() {
        return instance.stripChatColorTags;
    }

    /**
     * Returns the configuration file at the specified path with a custom route separator.
     *
     * @param filePath The path to the configuration file
     * @return The {@code YamlDocument} representing the loaded configuration
     */
    @Override
    public YamlDocument loadConfig(String filePath) {
        return loadConfig(filePath, '.');
    }

    /**
     * Returns the configuration file at the specified path with a custom route separator.
     *
     * @param filePath The path to the configuration file
     * @param routeSeparator The character used to separate route segments in the YAML file
     * @return The {@code YamlDocument} representing the loaded configuration
     */
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

    /**
     * Loads the configuration file from the specified file path.
     *
     * @param filePath the relative or absolute path to the configuration file.
     * @param generalSettings settings for general YAML loading behavior.
     * @param loaderSettings settings for YAML loader behavior.
     * @param dumperSettings settings for YAML dumping behavior.
     * @param updaterSettings settings for YAML updating behavior.
     * @return a {@link YamlDocument} containing the loaded YAML content.
     * @throws RuntimeException if the configuration file could not be loaded or an error occurs.
     */
    @Override
    public YamlDocument loadConfig(String filePath, GeneralSettings generalSettings, LoaderSettings loaderSettings, DumperSettings dumperSettings, UpdaterSettings updaterSettings) {
        try (InputStream inputStream = new FileInputStream(resolveConfig(filePath).toFile())) {
            return YamlDocument.create(
                    inputStream,
                    plugin.getResourceStream(filePath),
                    generalSettings,
                    loaderSettings,
                    dumperSettings,
                    updaterSettings
            );
        } catch (IOException e) {
            plugin.getPluginLogger().severe("Failed to load config " + filePath, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads the YAML data from the specified file.
     *
     * @param file the file containing the YAML data.
     * @return a {@link YamlDocument} containing the loaded YAML data.
     * @throws RuntimeException if the data file could not be loaded or an error occurs.
     */
    @Override
    public YamlDocument loadData(File file) {
        try (InputStream inputStream = new FileInputStream(file)) {
            return YamlDocument.create(inputStream);
        } catch (IOException e) {
            plugin.getPluginLogger().severe("Failed to load config " + file, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Resolves the configuration file path and ensures the file exists.
     * If the configuration file does not exist, it is created from an embedded resource.
     *
     * @param filePath the relative or absolute path to the configuration file.
     * @return the resolved {@link Path} to the configuration file.
     * @throws IllegalArgumentException if the filePath is null or empty, or if the embedded resource cannot be found.
     * @throws RuntimeException if an error occurs during file creation or copying.
     */
    public Path resolveConfig(String filePath) {
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
