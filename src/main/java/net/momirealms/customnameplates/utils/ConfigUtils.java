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

package net.momirealms.customnameplates.utils;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.helper.Log;
import net.momirealms.customnameplates.object.carrier.TextDisplayMeta;
import net.momirealms.customnameplates.object.requirements.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class ConfigUtils {

    public static void update(String file_name){
        try {
            YamlDocument.create(
                    new File(CustomNameplates.getInstance().getDataFolder(), file_name),
                    Objects.requireNonNull(CustomNameplates.getInstance().getResource(file_name)),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")
            ).build());
        } catch (IOException e){
            Log.warn(e.getMessage());
        }
    }

    public static YamlConfiguration getConfig(String config_name) {
        File file = new File(CustomNameplates.getInstance().getDataFolder(), config_name);
        if (!file.exists()) CustomNameplates.getInstance().saveResource(config_name, false);
        return YamlConfiguration.loadConfiguration(file);
    }

    public static YamlConfiguration readData(File file) {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                if (!file.createNewFile()) {
                    AdventureUtils.consoleMessage("<red>[CustomNameplates] Failed to generate data files!</red>");
                }
            } catch (IOException e) {
                e.printStackTrace();
                AdventureUtils.consoleMessage("<red>[CustomNameplates] Failed to generate data files!</red>");
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public static Requirement[] getRequirements(ConfigurationSection section) {
        List<Requirement> requirements = new ArrayList<>();
        if (section != null) {
            for (String type : section.getKeys(false)) {
                switch (type) {
                    case "biome" -> requirements.add(new BiomeImpl(new HashSet<>(section.getStringList(type))));
                    case "weather" -> requirements.add(new WeatherImpl(section.getStringList(type)));
                    case "ypos" -> requirements.add(new YPosImpl(section.getStringList(type)));
                    case "world" -> requirements.add(new WorldImpl(section.getStringList(type)));
                    case "permission" -> requirements.add(new PermissionImpl(section.getString(type)));
                    case "time" -> requirements.add(new TimeImpl(section.getStringList(type)));
                    case "date" -> requirements.add(new DateImpl(new HashSet<>(section.getStringList(type))));
                    case "papi-condition" -> requirements.add(new CustomPapiImpl(Objects.requireNonNull(section.getConfigurationSection(type)).getValues(false)));
                }
            }
        }
        return requirements.toArray(new Requirement[0]);
    }

    public static TextDisplayMeta getTextDisplayMeta(ConfigurationSection section) {
        if (section == null) return TextDisplayMeta.defaultValue;
        return new TextDisplayMeta(
                section.getBoolean("has-shadow", false),
                section.getBoolean("is-see-through", false),
                section.getBoolean("use-default-background-color", false),
                ConfigUtils.rgbToDecimal(section.getString("background-color", "0,0,0,128")),
                (byte) section.getInt("text-opacity")
        );
    }

    public static int rgbToDecimal(String rgba) {
        String[] split = rgba.split(",");
        int r = Integer.parseInt(split[0]);
        int g = Integer.parseInt(split[1]);
        int b = Integer.parseInt(split[2]);
        int a = Integer.parseInt(split[3]);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
