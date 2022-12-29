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
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigUtil {

    private static YamlConfiguration module;

    public static void load() {
        module = getConfig("MODULES.yml");
    }

    public static boolean isModuleEnabled(String moduleName) {
        return module.getBoolean(moduleName, false);
    }

    public static void update(String fileName){
        try {
            YamlDocument.create(new File(CustomNameplates.plugin.getDataFolder(), fileName), CustomNameplates.plugin.getResource(fileName), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());
        } catch (IOException e){
            Log.warn(e.getMessage());
        }
    }

    public static YamlConfiguration getConfig(String configName) {
        File file = new File(CustomNameplates.plugin.getDataFolder(), configName);
        if (!file.exists()) CustomNameplates.plugin.saveResource(configName, false);
        return YamlConfiguration.loadConfiguration(file);
    }

    public static YamlConfiguration readData(File file) {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                AdventureUtil.consoleMessage("<red>[CustomNameplates] Failed to generate data files!</red>");
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public static void reloadConfigs() {
        load();
        CustomNameplates.plugin.getConfigManager().unload();
        CustomNameplates.plugin.getConfigManager().load();
        CustomNameplates.plugin.getMessageManager().unload();
        CustomNameplates.plugin.getMessageManager().load();
        CustomNameplates.plugin.getBossBarManager().unload();
        CustomNameplates.plugin.getBossBarManager().load();
        CustomNameplates.plugin.getActionBarManager().unload();
        CustomNameplates.plugin.getActionBarManager().load();
        CustomNameplates.plugin.getPlaceholderManager().unload();
        CustomNameplates.plugin.getPlaceholderManager().load();
        CustomNameplates.plugin.getNameplateManager().unload();
        CustomNameplates.plugin.getNameplateManager().load();
        CustomNameplates.plugin.getChatBubblesManager().unload();
        CustomNameplates.plugin.getChatBubblesManager().load();
        CustomNameplates.plugin.getWidthManager().unload();
        CustomNameplates.plugin.getWidthManager().load();
        CustomNameplates.plugin.getDataManager().unload();
        CustomNameplates.plugin.getDataManager().load();
        CustomNameplates.plugin.getResourceManager().generateResourcePack();
    }
}
