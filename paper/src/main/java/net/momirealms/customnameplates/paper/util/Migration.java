package net.momirealms.customnameplates.paper.util;

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.util.LogUtils;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Migration {

    public static boolean check() {
        File readMe = new File(CustomNameplatesPlugin.get().getDataFolder(), "README.text");
        if (readMe.exists()) {
            return true;
        }
        File file = new File(CustomNameplatesPlugin.get().getDataFolder(), "config.yml");
        if (!file.exists()) {
            return false;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        boolean version = config.contains("other-settings.default-character-width");
        if (!version) {
            return false;
        }
        config.set("other-settings.default-character-width", null);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        CustomNameplatesPlugin.get().saveResource("README.txt", true);
        updateBossBar();
        updateActionBar();
        updateBubble();
        updateNameplate();
        updateCustomPlaceholders();
        deleteFiles();
        return true;
    }

    private static void deleteFiles() {
        try {
            FileUtils.delete(new File(CustomNameplatesPlugin.get().getDataFolder(), "database.yml"));
            FileUtils.deleteDirectory(new File(CustomNameplatesPlugin.get().getDataFolder(), "unicodes"));
            FileUtils.deleteDirectory(new File(CustomNameplatesPlugin.get().getDataFolder(), "templates"));
            FileUtils.deleteDirectory(new File(CustomNameplatesPlugin.get().getDataFolder(), "ResourcePack"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateBossBar() {
        File file = new File(CustomNameplatesPlugin.get().getDataFolder(), "configs" + File.separator + "bossbar.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (Map.Entry<String, Object> entry : config.getValues(false).entrySet()) {
            if (entry.getValue() instanceof ConfigurationSection section) {
                section.set("check-frequency", 1);
                int refresh = section.getInt("refresh-rate");
                if (section.contains("text")) {
                    String text = section.getString("text");
                    section.set("text-display-order.1.duration", 1000);
                    section.set("text-display-order.1.refresh-frequency", refresh);
                    section.set("text-display-order.1.text", text);
                } else if (section.contains("dynamic-text")) {
                    int switchInt = section.getInt("switch-interval", 15) * 20;
                    int i = 0;
                    for (String text : section.getStringList("dynamic-text")) {
                        i++;
                        section.set("text-display-order."+i+".duration", switchInt);
                        section.set("text-display-order."+i+".refresh-frequency", refresh);
                        section.set("text-display-order."+i+".text", text);
                    }
                }
                section.set("switch-interval", null);
                section.set("dynamic-text", null);
                section.set("refresh-rate", null);
                section.set("text", null);
            }
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateActionBar() {
        File file = new File(CustomNameplatesPlugin.get().getDataFolder(), "configs" + File.separator + "actionbar.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (Map.Entry<String, Object> entry : config.getValues(false).entrySet()) {
            if (entry.getValue() instanceof ConfigurationSection section) {
                String text = section.getString("text", "");
                section.set("text", null);
                section.set("check-frequency", 1);
                section.set("text-display-order.1.text", text);
                section.set("text-display-order.1.refresh-frequency", 1);
                section.set("text-display-order.1.duration", 1000);
            }
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateBubble() {
        File file = new File(CustomNameplatesPlugin.get().getDataFolder(), "configs" + File.separator + "bubble.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("mode", null);
        config.set("text-display-options", null);
        config.set("default-bubble", config.getString("default-bubbles","none"));
        config.set("default-bubbles", null);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateNameplate() {
        File file = new File(CustomNameplatesPlugin.get().getDataFolder(), "configs" + File.separator + "nameplate.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String mode = config.getString("mode","");
        if (!mode.equalsIgnoreCase("team")) {
            config.set("mode", "Unlimited");
        }

        config.set("nameplate.player-name", config.getString("player-name","%player_name%"));
        config.set("nameplate.refresh-frequency", 10);
        config.set("nameplate.prefix", config.getString("prefix", ""));
        config.set("nameplate.prefix", config.getString("suffix",""));

        if (mode.equalsIgnoreCase("team")) {
            config.createSection("unlimited");
            ConfigurationSection section = config.createSection("team");
            section.set("refresh-frequency", 20);
            section.set("prefix", "");
            section.set("suffix", "");
        } else {
            ConfigurationSection old;
            if (mode.equalsIgnoreCase("armor_stand")) {
                old = config.getConfigurationSection("armor_stand");
            } else if (mode.equalsIgnoreCase("text_display")) {
                old = config.getConfigurationSection("text_display");
            } else {
                LogUtils.severe("No mode is set for nameplate. Failed to migrate!");
                return;
            }
            ConfigurationSection newSection = config.createSection("unlimited", old.getValues(false));
            for (Map.Entry<String, Object> entry : newSection.getValues(false).entrySet()) {
                if (entry.getValue() instanceof ConfigurationSection inner) {
                    ConfigurationSection conditions = inner.getConfigurationSection("conditions");
                    if (conditions != null) {
                        inner.createSection("owner-conditions", conditions.getValues(false));
                        inner.createSection("viewer-conditions");
                    }
                }
            }
        }
        try {
            config.set("player-name", null);
            config.set("prefix", null);
            config.set("suffix", null);
            config.set("hide-prefix-when-equipping-nameplate", null);
            config.set("hide-suffix-when-equipping-nameplate", null);
            config.set("create-fake-team", null);
            config.set("text_display", null);
            config.set("armor_stand", null);
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateCustomPlaceholders() {
        File file = new File(CustomNameplatesPlugin.get().getDataFolder(), "configs" + File.separator + "custom-placeholders.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.createSection("switch-text");
        ConfigurationSection descentTSection = config.getConfigurationSection("descent-text");
        ConfigurationSection descentUSection = config.getConfigurationSection("descent-unicode");
        if (descentTSection != null && descentUSection != null) {
            for (Map.Entry<String, Object> entry : descentUSection.getValues(false).entrySet()) {
                if (entry.getValue() instanceof ConfigurationSection inner) {
                    String text = inner.getString("text");
                    int descent = inner.getInt("descent");
                    ConfigurationSection newSection;
                    if (descentTSection.contains(entry.getKey())) {
                        newSection = descentTSection.createSection(entry.getKey() + "_");
                        LogUtils.warn("Duplicated key '" + entry.getKey() + "' found during migration. Please manually fix that.");
                    } else {
                        newSection = descentTSection.createSection(entry.getKey());
                    }
                    newSection.set("text", text);
                    newSection.set("descent", descent);
                    newSection.set("is-unicode", true);
                }
            }
        }
        config.set("descent-unicode", null);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
