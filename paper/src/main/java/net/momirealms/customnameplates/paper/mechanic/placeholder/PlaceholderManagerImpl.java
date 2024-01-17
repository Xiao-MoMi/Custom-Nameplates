package net.momirealms.customnameplates.paper.mechanic.placeholder;

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.common.Pair;
import net.momirealms.customnameplates.api.manager.PlaceholderManager;
import net.momirealms.customnameplates.api.mechanic.placeholder.*;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.api.util.LogUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderManagerImpl implements PlaceholderManager {

    private final Pattern placeholderPattern = Pattern.compile("%([^%]*)%");
    private final PluginPlaceholders pluginPlaceholders;
    private final HashMap<String, StaticText> staticTextMap;
    private final HashMap<String, SwitchText> switchTextMap;
    private final HashMap<String, DescentText> descentTextMap;
    private final HashMap<String, ConditionalText> conditionalTextMap;
    private final HashMap<String, NameplateText> nameplateTextMap;
    private final HashMap<String, BackGroundText> backGroundTextMap;
    private final CustomNameplatesPlugin plugin;

    public PlaceholderManagerImpl(CustomNameplatesPlugin plugin) {
        this.plugin = plugin;
        this.pluginPlaceholders = new PluginPlaceholders(plugin, this);
        this.staticTextMap = new HashMap<>();
        this.switchTextMap = new HashMap<>();
        this.descentTextMap = new HashMap<>();
        this.conditionalTextMap = new HashMap<>();
        this.nameplateTextMap = new HashMap<>();
        this.backGroundTextMap = new HashMap<>();
    }

    @NotNull
    @Override
    public List<String> detectPlaceholders(String text){
        List<String> placeholders = new ArrayList<>();
        Matcher matcher = placeholderPattern.matcher(text);
        while (matcher.find()) placeholders.add(matcher.group());
        return placeholders;
    }

    public void reload() {
        unload();
        load();
    }

    public void load() {
        this.loadConfigs();

        if (!pluginPlaceholders.isRegistered())
            pluginPlaceholders.register();
    }

    public void unload() {
        if (pluginPlaceholders.isRegistered())
            pluginPlaceholders.unregister();

        this.staticTextMap.clear();
        this.switchTextMap.clear();
        this.descentTextMap.clear();
        this.conditionalTextMap.clear();
    }

    @Override
    public StaticText getStaticText(String key) {
        return staticTextMap.get(key);
    }

    @Override
    public SwitchText getSwitchText(String key) {
        return switchTextMap.get(key);
    }

    @Override
    public DescentText getDescentText(String key) {
        return descentTextMap.get(key);
    }

    @Override
    public ConditionalText getConditionalText(String key) {
        return conditionalTextMap.get(key);
    }

    @Override
    public NameplateText getNameplateText(String key) {
        return nameplateTextMap.get(key);
    }

    @Override
    public BackGroundText getBackGroundText(String key) {
        return backGroundTextMap.get(key);
    }

    public void loadConfigs() {
        YamlConfiguration config = plugin.getConfig("configs" + File.separator + "custom-placeholders.yml");
        ConfigurationSection staticSection = config.getConfigurationSection("static-text");
        if (staticSection != null) {
            loadStaticTexts(staticSection);
        }

        ConfigurationSection switchSection = config.getConfigurationSection("switch-text");
        if (switchSection != null) {
            loadSwitchTexts(switchSection);
        }

        ConfigurationSection descentSection = config.getConfigurationSection("descent-text");
        if (descentSection != null) {
            loadDescentTexts(descentSection, false);
        }

        ConfigurationSection unicodeSection = config.getConfigurationSection("descent-unicode");
        if (unicodeSection != null) {
            loadDescentTexts(unicodeSection, true);
        }

        ConfigurationSection conditionalSection = config.getConfigurationSection("conditional-text");
        if (conditionalSection != null) {
            loadConditionalTexts(conditionalSection);
        }

        ConfigurationSection nameplateSection = config.getConfigurationSection("nameplate-text");
        if (nameplateSection != null) {
            loadNameplateTexts(nameplateSection);
        }

        ConfigurationSection backgroundSection = config.getConfigurationSection("background-text");
        if (backgroundSection != null) {
            loadBackGroundTexts(backgroundSection);
        }
    }

    private void loadBackGroundTexts(ConfigurationSection section) {
        for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
            if (!(entry.getValue() instanceof ConfigurationSection innerSection))
                continue;

            backGroundTextMap.put(entry.getKey(),
                    BackGroundText.builder()
                            .background(Objects.requireNonNull(plugin.getBackGroundManager().getBackGround(innerSection.getString("background"))))
                            .text(innerSection.getString("text", ""))
                            .build()
            );
        }
    }

    private void loadNameplateTexts(ConfigurationSection section) {
        for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
            if (!(entry.getValue() instanceof ConfigurationSection innerSection))
                continue;

            var nameplate = plugin.getNameplateManager().getNameplate(innerSection.getString("nameplate"));
            if (nameplate == null) {
                LogUtils.warn("Nameplate: " + innerSection.getString("nameplate") + " doesn't exist. nameplate-text: " + entry.getKey() + " would not take effect");
                continue;
            }

            nameplateTextMap.put(entry.getKey(),
                    NameplateText.builder()
                            .nameplate(nameplate)
                            .text(innerSection.getString("text", ""))
                            .build()
            );
        }
    }

    private void loadDescentTexts(ConfigurationSection section, boolean unicode) {
        for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
            if (!(entry.getValue() instanceof ConfigurationSection innerSection))
                continue;

            descentTextMap.put(
                    entry.getKey(),
                    DescentText.builder()
                            .descent(innerSection.getInt("descent", 0))
                            .text(innerSection.getString("text", ""))
                            .unicode(unicode)
                            .build()
            );
        }
    }

    private void loadStaticTexts(ConfigurationSection section) {
        for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
            if (!(entry.getValue() instanceof ConfigurationSection innerSection))
                continue;

            staticTextMap.put(
                    entry.getKey(),
                    StaticText.builder()
                            .value(innerSection.getInt("value"))
                            .text(innerSection.getString("text"))
                            .state(StaticText.StaticState.valueOf(innerSection.getString("position", "middle").toUpperCase(Locale.ENGLISH)))
                            .build()
            );
        }
    }

    private void loadSwitchTexts(ConfigurationSection section) {
        for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
            if (!(entry.getValue() instanceof ConfigurationSection innerSection))
                continue;

            HashMap<String, String> valueMap = new HashMap<>();
            ConfigurationSection valueSection = innerSection.getConfigurationSection("case");
            if (valueSection != null) {
                for (String key : valueSection.getKeys(false)) {
                    valueMap.put(key, valueSection.getString(key));
                }
            }

            switchTextMap.put(
                    entry.getKey(),
                    SwitchText.builder()
                            .toParse(Objects.requireNonNull(innerSection.getString("switch")))
                            .defaultValue(innerSection.getString("default"))
                            .valueMap(valueMap)
                            .build()
            );
        }
    }

    private void loadConditionalTexts(ConfigurationSection section) {
        for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
            if (!(entry.getValue() instanceof ConfigurationSection innerSection))
                continue;

            ArrayList<Pair<String, Requirement[]>> list = new ArrayList<>();
            for (Map.Entry<String, Object> innerEntry : innerSection.getValues(false).entrySet()) {
                if (!(innerEntry.getValue() instanceof ConfigurationSection prioritySection)) {
                    continue;
                }
                list.add(Pair.of(
                    prioritySection.getString("text"),
                    plugin.getRequirementManager().getRequirements(prioritySection.getConfigurationSection("conditions"))
                ));
            }

            conditionalTextMap.put(
                    entry.getKey(),
                    ConditionalText.builder()
                            .textList(list)
                            .build()
            );
        }
    }
}