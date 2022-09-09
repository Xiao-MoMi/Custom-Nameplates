package net.momirealms.customnameplates.utils;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.helper.Log;

import java.io.File;
import java.io.IOException;

public class ConfigUtil {

    public static void update(){
        try {
            YamlDocument.create(new File(CustomNameplates.instance.getDataFolder(), "config.yml"), CustomNameplates.instance.getResource("config.yml"), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());
            YamlDocument.create(new File(CustomNameplates.instance.getDataFolder(), "nameplate.yml"), CustomNameplates.instance.getResource("nameplate.yml"), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());
        }catch (IOException e){
            Log.warn(e.getMessage());
        }
    }
}
