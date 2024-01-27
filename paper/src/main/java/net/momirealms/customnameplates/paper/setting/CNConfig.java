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

package net.momirealms.customnameplates.paper.setting;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.mechanic.character.CharacterArranger;
import net.momirealms.customnameplates.api.util.FontUtils;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.paper.mechanic.bossbar.BarColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

public class CNConfig {

    public static String configVersion = "25";
    public static int cacheSize;
    public static int corePoolSize;
    public static long keepAliveTime;
    public static int maximumPoolSize;
    public static boolean debug;
    public static String language;
    public static boolean updateChecker;
    public static boolean metrics;
    public static boolean legacyColorSupport;
    public static boolean generatePackOnStart;
    public static int sendDelay;
    public static String namespace;
    public static String font;
    public static char initChar;
    public static boolean copyPackIA;
    public static boolean copyPackIAOld;
    public static boolean copyPackOraxen;
    public static boolean trChatChannel;
    public static boolean ventureChatChannel;
    public static boolean nameplateModule;
    public static boolean bossBarModule;
    public static boolean actionBarModule;
    public static boolean bubbleModule;
    public static boolean backgroundModule;
    public static boolean imageModule;
    public static boolean tabTeam;
    public static boolean cmiTeam;
    public static String folderNameplate;
    public static String folderImage;
    public static String folderBubble;
    public static String folderBackground;
    public static String folderSplit;
    public static boolean legacyUnicodes;
    public static BarColor barColorToRemove;
    public static boolean legacyBossBarImage;
    public static boolean newBossBarImage;
    public static boolean hideScoreboardNumber;
    public static boolean animatedImage;
    public static boolean textEffects;
    public static boolean disableTeamManage;
    public static boolean velocitab;
    public static boolean unknownTeam;
    public static boolean createRealTeam;
    public static boolean enableShader;

    public static void load() {
        try {
            YamlDocument.create(
                    new File(CustomNameplatesPlugin.getInstance().getDataFolder(), "config.yml"),
                    Objects.requireNonNull(CustomNameplatesPlugin.getInstance().getResource("config.yml")),
                    GeneralSettings.DEFAULT,
                    LoaderSettings
                            .builder()
                            .setAutoUpdate(true)
                            .build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings
                            .builder()
                            .setVersioning(new BasicVersioning("config-version"))
                            .build()
            );
            loadSettings(CustomNameplatesPlugin.getInstance().getConfig("config.yml"));
        } catch (IOException e) {
            LogUtils.warn(e.getMessage());
        }
    }

    private static void loadSettings(YamlConfiguration config) {
        debug = config.getBoolean("debug", false);

        language = config.getString("lang", "english");
        updateChecker = config.getBoolean("update-checker", true);
        metrics = config.getBoolean("metrics");

        ConfigurationSection moduleSection = config.getConfigurationSection("modules");
        if (moduleSection != null) {
            nameplateModule = moduleSection.getBoolean("nameplates");
            bossBarModule = moduleSection.getBoolean("bossbars");
            actionBarModule = moduleSection.getBoolean("actionbars");
            bubbleModule = moduleSection.getBoolean("bubbles");
            backgroundModule = moduleSection.getBoolean("backgrounds");
            imageModule = moduleSection.getBoolean("images");
        }

        ConfigurationSection integrationSection = config.getConfigurationSection("integrations");
        if (integrationSection != null) {
            copyPackIA = integrationSection.getBoolean("resource-pack.ItemsAdder", false);
            copyPackIAOld = integrationSection.getBoolean("resource-pack.ItemsAdder-old-method", false);
            copyPackOraxen = integrationSection.getBoolean("resource-pack.Oraxen", false);
            trChatChannel = integrationSection.getBoolean("chat.TrChat", false);
            ventureChatChannel = integrationSection.getBoolean("chat.VentureChat", false);
            tabTeam = integrationSection.getBoolean("team.TAB", false);
            cmiTeam = integrationSection.getBoolean("team.CMI", false);
            velocitab = integrationSection.getBoolean("team.Velocitab", false);
            unknownTeam = integrationSection.getBoolean("team.unknown", false);
        }

        ConfigurationSection packSection = config.getConfigurationSection("resource-pack");
        if (packSection != null) {
            generatePackOnStart = !packSection.getBoolean("disable-generation-on-start", false);
            namespace = packSection.getString("namespace", "nameplates");
            font = packSection.getString("font", "default");
            FontUtils.setNameSpaceAndFont(namespace, font);

            initChar = packSection.getString("initial-char", "뀁").charAt(0);
            CharacterArranger.reset(initChar);

            folderNameplate = packSection.getString("image-path.nameplates","font\\nameplates\\");
            folderBubble = packSection.getString("image-path.bubbles","font\\bubbles\\");
            folderBackground = packSection.getString("image-path.backgrounds","font\\backgrounds\\");
            folderImage = packSection.getString("image-path.images","font\\images\\");
            folderSplit = packSection.getString("image-path.space-split","font\\base\\");

            barColorToRemove = BarColor.valueOf(packSection.getString("transparent-bossbar.color", "yellow").toUpperCase(Locale.ENGLISH));
            legacyBossBarImage = packSection.getBoolean("transparent-bossbar.1_17-1_20_1", true);
            newBossBarImage = packSection.getBoolean("transparent-bossbar.1_20_2+", true);

            legacyUnicodes = packSection.getBoolean("legacy-unicodes", true);

            enableShader = packSection.getBoolean("shader.enable", true);
            hideScoreboardNumber = packSection.getBoolean("shader.hide-scoreboard-number", false);
            animatedImage = packSection.getBoolean("shader.animated-text", false);
            textEffects = packSection.getBoolean("shader.ItemsAdder-text-effects", false);
        }

        disableTeamManage = config.getBoolean("other-settings.disable-team-management", false);
        corePoolSize = config.getInt("other-settings.thread-pool-settings.corePoolSize", 10);
        maximumPoolSize = config.getInt("other-settings.thread-pool-settings.maximumPoolSize", 10);
        keepAliveTime = config.getInt("other-settings.thread-pool-settings.keepAliveTime", 30);
        cacheSize = config.getInt("other-settings.cache-size", 100);

        legacyColorSupport = config.getBoolean("other-settings.legacy-color-code-support");
        createRealTeam = config.getBoolean("other-settings.create-real-teams", false);
    }

    public static boolean isOtherTeamPluginHooked() {
        return tabTeam || cmiTeam || unknownTeam;
    }
}
