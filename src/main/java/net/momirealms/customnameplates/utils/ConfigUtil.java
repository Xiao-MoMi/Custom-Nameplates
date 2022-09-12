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
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.helper.Log;

import java.io.File;
import java.io.IOException;

public class ConfigUtil {

    public static void update(){
        try {
            YamlDocument.create(new File(CustomNameplates.instance.getDataFolder(), "config.yml"), CustomNameplates.instance.getResource("config.yml"), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());
            YamlDocument.create(new File(CustomNameplates.instance.getDataFolder(), "nameplate.yml"), CustomNameplates.instance.getResource("nameplate.yml"), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());
            YamlDocument.create(new File(CustomNameplates.instance.getDataFolder(), "bubble.yml"), CustomNameplates.instance.getResource("bubble.yml"), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());
            YamlDocument.create(new File(CustomNameplates.instance.getDataFolder(), "MODULES.yml"), CustomNameplates.instance.getResource("MODULES.yml"), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());
            YamlDocument.create(new File(CustomNameplates.instance.getDataFolder(), "database.yml"), CustomNameplates.instance.getResource("database.yml"), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());
        }catch (IOException e){
            Log.warn(e.getMessage());
        }
    }

    public static void lang(){
        try {
            YamlDocument.create(new File(CustomNameplates.instance.getDataFolder(), "messages" + File.separator + ConfigManager.Main.lang +".yml"), CustomNameplates.instance.getResource("messages" + File.separator + ConfigManager.Main.lang +".yml"), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());
        }catch (IOException e){
            Log.warn(e.getMessage());
        }
    }
}
