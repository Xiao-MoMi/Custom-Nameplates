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

package net.momirealms.customnameplates.paper.mechanic.placeholder;

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;
import net.momirealms.customnameplates.api.manager.PlaceholderManager;
import net.momirealms.customnameplates.api.mechanic.character.ConfiguredChar;
import net.momirealms.customnameplates.api.mechanic.placeholder.*;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.api.util.LogUtils;
import net.momirealms.customnameplates.common.Pair;
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
    private final HashMap<String, VanillaHud> vanillaHudMap;
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
        this.vanillaHudMap = new HashMap<>();
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
        this.nameplateTextMap.clear();
        this.backGroundTextMap.clear();
        this.vanillaHudMap.clear();
    }

    @Override
    public StaticText getStaticText(String key) {
        return staticTextMap.get(key);
    }

    @Override
    public Collection<StaticText> getStaticTexts() {
        return staticTextMap.values();
    }

    @Override
    public SwitchText getSwitchText(String key) {
        return switchTextMap.get(key);
    }

    @Override
    public Collection<SwitchText> getSwitchTexts() {
        return switchTextMap.values();
    }

    @Override
    public DescentText getDescentText(String key) {
        return descentTextMap.get(key);
    }

    @Override
    public Collection<DescentText> getDescentTexts() {
        return descentTextMap.values();
    }

    @Override
    public ConditionalText getConditionalText(String key) {
        return conditionalTextMap.get(key);
    }

    @Override
    public Collection<ConditionalText> getConditionalTexts() {
        return conditionalTextMap.values();
    }

    @Override
    public NameplateText getNameplateText(String key) {
        return nameplateTextMap.get(key);
    }

    @Override
    public Collection<NameplateText> getNameplateTexts() {
        return nameplateTextMap.values();
    }

    @Override
    public BackGroundText getBackGroundText(String key) {
        return backGroundTextMap.get(key);
    }

    @Override
    public Collection<BackGroundText> getBackGroundTexts() {
        return backGroundTextMap.values();
    }

    @Override
    public VanillaHud getVanillaHud(String key) {
        return vanillaHudMap.get(key);
    }

    @Override
    public Collection<VanillaHud> getVanillaHuds() {
        return vanillaHudMap.values();
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

        ConfigurationSection vanillaSection = config.getConfigurationSection("vanilla-hud");
        if (vanillaSection != null) {
            loadVanillaHuds(vanillaSection);
        }
    }

    private void loadVanillaHuds(ConfigurationSection section) {
        for (Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
            if (!(entry.getValue() instanceof ConfigurationSection innerSection))
                continue;

            ConfiguredChar fullC = plugin.getImageManager().getImage(innerSection.getString("images.full",""));
            if (fullC == null) {
                LogUtils.warn("Vanilla hud placeholder wouldn't work because image " + innerSection.getString("full","") + " doesn't exist.");
                continue;
            }

            ConfiguredChar emptyC = plugin.getImageManager().getImage(innerSection.getString("images.empty",""));
            if (emptyC == null) {
                LogUtils.warn("Vanilla hud placeholder wouldn't work because image " + innerSection.getString("empty","") + " doesn't exist.");
                continue;
            }

            ConfiguredChar halfC = plugin.getImageManager().getImage(innerSection.getString("images.half",""));
            if (halfC == null) {
                LogUtils.warn("Vanilla hud placeholder wouldn't work because image " + innerSection.getString("half","") + " doesn't exist.");
                continue;
            }

            vanillaHudMap.put(entry.getKey(),
                    VanillaHud.builder()
                            .reverse(innerSection.getBoolean("reverse", true))
                            .half(halfC.getCharacter())
                            .empty(emptyC.getCharacter())
                            .full(fullC.getCharacter())
                            .max(innerSection.getString("placeholder.max-value"))
                            .current(innerSection.getString("placeholder.value"))
                            .build()
            );
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
                            .removeShadow(innerSection.getBoolean("remove-shadow"))
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
                            .unicode(unicode || innerSection.getBoolean("is-unicode"))
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