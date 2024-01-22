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
import net.momirealms.customnameplates.api.util.LogUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class CNLocale {

    public static String MSG_RELOAD;
    public static String MSG_PREFIX;
    public static String MSG_PREVIEW_COOLDOWN;
    public static String MSG_PREVIEW_START;
    public static String MSG_GENERATING;
    public static String MSG_NO_NAMEPLATE;
    public static String MSG_PACK_GENERATED;
    public static String MSG_EQUIP_NAMEPLATE;
    public static String MSG_UNEQUIP_NAMEPLATE;
    public static String MSG_FORCE_EQUIP_NAMEPLATE;
    public static String MSG_FORCE_UNEQUIP_NAMEPLATE;
    public static String MSG_NAMEPLATE_NOT_EXISTS;
    public static String MSG_NAMEPLATE_NOT_AVAILABLE;
    public static String MSG_AVAILABLE_NAMEPLATE;
    public static String MSG_HAVE_NO_NAMEPLATE;
    public static String MSG_FORCE_PREVIEW;
    public static String MSG_EQUIP_BUBBLE;
    public static String MSG_UNEQUIP_BUBBLE;
    public static String MSG_FORCE_EQUIP_BUBBLE;
    public static String MSG_FORCE_UNEQUIP_BUBBLE;
    public static String MSG_BUBBLE_NOT_EXIST;
    public static String MSG_BUBBLE_NOT_AVAILABLE;
    public static String MSG_AVAILABLE_BUBBLE;
    public static String MSG_HAVE_NO_BUBBLE;

    public static void load() {
        try {
            YamlDocument.create(
                    new File(CustomNameplatesPlugin.getInstance().getDataFolder(), "messages/" + CNConfig.language + ".yml"),
                    Objects.requireNonNull(CustomNameplatesPlugin.getInstance().getResource("messages/" + CNConfig.language + ".yml")),
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
        } catch (IOException e) {
            LogUtils.warn(e.getMessage());
        }
        loadSettings(CustomNameplatesPlugin.get().getConfig("messages/" + CNConfig.language + ".yml"));
    }

    public static void loadSettings(YamlConfiguration config) {
        ConfigurationSection section = config.getConfigurationSection("messages");
        if (section != null) {
            MSG_RELOAD = section.getString("reload");
            MSG_PREFIX = section.getString("prefix");
            MSG_PREVIEW_COOLDOWN = config.getString("messages.cooldown");
            MSG_PREVIEW_START = config.getString("messages.preview");
            MSG_GENERATING = config.getString("messages.generate");
            MSG_PACK_GENERATED = config.getString("messages.generate-done");
            MSG_NO_NAMEPLATE = config.getString("messages.no-nameplate");
            MSG_EQUIP_NAMEPLATE = config.getString("messages.equip-nameplates");
            MSG_UNEQUIP_NAMEPLATE = config.getString("messages.unequip-nameplates");
            MSG_FORCE_EQUIP_NAMEPLATE = config.getString("messages.force-equip-nameplates");
            MSG_FORCE_UNEQUIP_NAMEPLATE = config.getString("messages.force-unequip-nameplates");
            MSG_NAMEPLATE_NOT_EXISTS = config.getString("messages.not-exist-nameplates");
            MSG_NAMEPLATE_NOT_AVAILABLE = config.getString("messages.not-available-nameplates");
            MSG_AVAILABLE_NAMEPLATE = config.getString("messages.available-nameplates");
            MSG_HAVE_NO_NAMEPLATE = config.getString("messages.have-no-nameplates");
            MSG_FORCE_PREVIEW = config.getString("messages.force-preview");
            MSG_EQUIP_BUBBLE = config.getString("messages.equip-bubbles");
            MSG_UNEQUIP_BUBBLE = config.getString("messages.unequip-bubbles");
            MSG_FORCE_EQUIP_BUBBLE = config.getString("messages.force-equip-bubbles");
            MSG_FORCE_UNEQUIP_BUBBLE = config.getString("messages.force-unequip-bubbles");
            MSG_BUBBLE_NOT_EXIST = config.getString("messages.not-exist-bubbles");
            MSG_BUBBLE_NOT_AVAILABLE = config.getString("messages.not-available-bubbles");
            MSG_AVAILABLE_BUBBLE = config.getString("messages.available-bubbles");
            MSG_HAVE_NO_BUBBLE = config.getString("messages.have-no-bubbles");
        }
    }
}
