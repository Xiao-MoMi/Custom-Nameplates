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

package net.momirealms.customnameplates.api.placeholder;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.MainTask;
import net.momirealms.customnameplates.api.feature.*;
import net.momirealms.customnameplates.api.feature.background.Background;
import net.momirealms.customnameplates.api.feature.bubble.Bubble;
import net.momirealms.customnameplates.api.feature.bubble.BubbleConfig;
import net.momirealms.customnameplates.api.feature.image.Image;
import net.momirealms.customnameplates.api.feature.nameplate.Nameplate;
import net.momirealms.customnameplates.api.helper.AdventureHelper;
import net.momirealms.customnameplates.api.placeholder.internal.*;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.common.util.Pair;

import java.io.File;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("DuplicatedCode")
public class PlaceholderManagerImpl implements PlaceholderManager {

    private static final Pattern PATTERN = Pattern.compile("%([^%]*)%");

    private final CustomNameplates plugin;
    private final HashMap<String, Integer> refreshIntervals = new HashMap<>();
    private final Map<String, Placeholder> registeredPlaceholders = new HashMap<>();
    private final HashMap<Placeholder, List<PreParsedDynamicText>> childrenText = new HashMap<>();
    private final List<PreParsedDynamicText> delayedInitTexts = new ArrayList<>();
    private final HashMap<String, Placeholder> nestedPlaceholders = new HashMap<>();

    public PlaceholderManagerImpl(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        YamlDocument document = ConfigManager.getMainConfig();
        Section section = document.getSection("other-settings.placeholder-refresh-interval");
        if (section != null) {
            for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
                String id = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof Integer) {
                    refreshIntervals.put(id, (Integer) value);
                }
            }
        }

        this.loadInternalPlaceholders();
        this.loadNestNameplatePlaceholders();
        this.loadCustomPlaceholders();

        for (Map.Entry<Placeholder, List<PreParsedDynamicText>> entry : this.childrenText.entrySet()) {
            Placeholder placeholder = entry.getKey();
            for (PreParsedDynamicText preParsedText : entry.getValue()) {
                preParsedText.init();
                placeholder.addChildren(preParsedText.placeholders());
            }
            this.nestedPlaceholders.put(placeholder.id().replace("%np_", "%nameplates_").replace("%rel_np_", "%rel_nameplates_"), placeholder);
        }
        this.childrenText.clear();

        for (PreParsedDynamicText preParsedText : this.delayedInitTexts) {
            preParsedText.init();
        }
        this.delayedInitTexts.clear();
    }

    @Override
    public void unload() {
        this.refreshIntervals.clear();
        this.registeredPlaceholders.clear();
        this.delayedInitTexts.clear();
        this.childrenText.clear();
        this.nestedPlaceholders.clear();
        PlaceholderCounter.reset();
    }

    private void loadNestNameplatePlaceholders() {
        PreParsedDynamicText nameTag = new PreParsedDynamicText(plugin.getNameplateManager().playerNameTag());
        Placeholder placeholder1 = this.registerPlayerPlaceholder("%np_tag-image%", (player -> {
            String equippedNameplate = player.equippedNameplate();
            if (equippedNameplate.equals("none")) equippedNameplate = plugin.getNameplateManager().defaultNameplateId();
            Nameplate nameplate = plugin.getNameplateManager().getNameplate(equippedNameplate);
            if (nameplate == null) return "";
            String tag = nameTag.fastCreate(player).render(player);
            float advance = plugin.getAdvanceManager().getLineAdvance(tag);
            return AdventureHelper.surroundWithNameplatesFont(nameplate.createImage(advance, 1, 0));
        }));
        Placeholder placeholder2 = this.registerRelationalPlaceholder("%rel_np_tag-image%", (p1, p2) -> {
            String equippedNameplate = p1.equippedNameplate();
            if (equippedNameplate.equals("none")) equippedNameplate = plugin.getNameplateManager().defaultNameplateId();
            Nameplate nameplate = plugin.getNameplateManager().getNameplate(equippedNameplate);
            if (nameplate == null) return "";
            String tag = nameTag.fastCreate(p1).render(p2);
            float advance = plugin.getAdvanceManager().getLineAdvance(tag);
            return AdventureHelper.surroundWithNameplatesFont(nameplate.createImage(advance, 1, 0));
        });
        Placeholder placeholder3 = this.registerPlayerPlaceholder("%np_tag-text%", (player -> nameTag.fastCreate(player).render(player)));
        Placeholder placeholder4 = this.registerRelationalPlaceholder("%rel_np_tag-text%", (p1, p2) -> nameTag.fastCreate(p1).render(p2));
        Placeholder placeholder5 = this.registerPlayerPlaceholder("%np_tag%", (player -> {
            String equippedNameplate = player.equippedNameplate();
            if (equippedNameplate.equals("none")) equippedNameplate = plugin.getNameplateManager().defaultNameplateId();
            Nameplate nameplate = plugin.getNameplateManager().getNameplate(equippedNameplate);
            String tag = nameTag.fastCreate(player).render(player);
            if (nameplate == null) return tag;
            float advance = plugin.getAdvanceManager().getLineAdvance(tag);
            return AdventureHelper.surroundWithNameplatesFont(nameplate.createImagePrefix(advance, 1, 0))
                    + tag + AdventureHelper.surroundWithNameplatesFont(nameplate.createImageSuffix(advance, 1, 0));
        }));
        Placeholder placeholder6 = this.registerRelationalPlaceholder("%rel_np_tag%", (p1, p2) -> {
            String equippedNameplate = p1.equippedNameplate();
            Nameplate nameplate = plugin.getNameplateManager().getNameplate(equippedNameplate);
            if (nameplate == null) return p1.name();
            String tag = nameTag.fastCreate(p1).render(p2);
            float advance = plugin.getAdvanceManager().getLineAdvance(tag);
            return AdventureHelper.surroundWithNameplatesFont(nameplate.createImagePrefix(advance, 1, 0))
                    + tag + AdventureHelper.surroundWithNameplatesFont(nameplate.createImageSuffix(advance, 1, 0));
        });
        List<PreParsedDynamicText> list = List.of(nameTag);
        childrenText.put(placeholder1, list);
        childrenText.put(placeholder2, list);
        childrenText.put(placeholder3, list);
        childrenText.put(placeholder4, list);
        childrenText.put(placeholder5, list);
        childrenText.put(placeholder6, list);
    }

    private void loadInternalPlaceholders() {
        this.registerPlayerPlaceholder("%player_name%", CNPlayer::name);
        this.registerPlayerPlaceholder("%player_x%", (player -> String.valueOf((int) Math.floor(player.position().x()))));
        this.registerPlayerPlaceholder("%player_y%", (player -> String.valueOf((int) Math.floor(player.position().y()))));
        this.registerPlayerPlaceholder("%player_z%", (player -> String.valueOf((int) Math.floor(player.position().z()))));
        this.registerPlayerPlaceholder("%player_world%", (CNPlayer::world));
        this.registerPlayerPlaceholder("%player_remaining_air%", (player -> String.valueOf(player.remainingAir())));
        for (int i = -256; i <= 256; i++) {
            String characters = OffsetFont.createOffsets(i);
            this.registerPlayerPlaceholder("%np_offset_" + i + "%", (p) -> AdventureHelper.surroundWithNameplatesFont(characters));
        }
        this.registerPlayerPlaceholder("%np_equipped_nameplate%", CNPlayer::equippedNameplate);
        this.registerPlayerPlaceholder("%np_equipped_bubble%", CNPlayer::equippedBubble);
        this.registerPlayerPlaceholder("%np_equipped_nameplate-name%", (player) -> {
            Nameplate nameplate = plugin.getNameplateManager().getNameplate(player.equippedNameplate());
            return Optional.ofNullable(nameplate).map(Nameplate::displayName).orElse("");
        });
        this.registerPlayerPlaceholder("%np_equipped_bubble-name%", (player) -> {
            BubbleConfig bubble = plugin.getBubbleManager().getBubbleConfig(player.equippedBubble());
            return Optional.ofNullable(bubble).map(BubbleConfig::displayName).orElse("");
        });
        this.registerSharedPlaceholder("%shared_np_is-latest%", () -> String.valueOf(plugin.isUpToDate()));
        this.registerPlayerPlaceholder("%np_is-latest%", (player) -> String.valueOf(plugin.isUpToDate()));
        this.registerPlayerPlaceholder("%np_time%", (player) -> {
            long time = player.playerTime() % 24_000;
            String ap = time >= 6000 && time < 18000 ? " PM" : " AM";
            int hours = (int) (time / 1000) ;
            int minutes = (int) ((time - hours * 1000 ) * 0.06);
            hours += 6;
            while (hours >= 12) hours -= 12;
            if (minutes < 10) return hours + ":0" + minutes + ap;
            else return hours + ":" + minutes + ap;
        });
        this.registerPlayerPlaceholder("%np_actionbar%", (player) -> plugin.getActionBarManager().getExternalActionBar(player));
        for (Image image : plugin.getImageManager().getImages()) {
            this.registerSharedPlaceholder("%shared_np_image_" + image.id() + "%",
                    () -> AdventureHelper.surroundWithNameplatesFont(String.valueOf(image.character().character())));
            this.registerPlayerPlaceholder("%np_image_" + image.id() + "%",
                    (player) -> AdventureHelper.surroundWithNameplatesFont(String.valueOf(image.character().character())));
            this.registerSharedPlaceholder("%shared_np_img_" + image.id() + "%",
                    () -> String.valueOf(image.character().character()));
            this.registerPlayerPlaceholder("%np_img_" + image.id() + "%",
                    (player) -> String.valueOf(image.character().character()));
        }
    }

    private void loadCustomPlaceholders() {
        plugin.getConfigManager().saveResource("configs" + File.separator + "custom-placeholders.yml");
        YamlDocument document = plugin.getConfigManager().loadData(new File(plugin.getDataDirectory().toFile(), "configs" + File.separator + "custom-placeholders.yml"));
        Section section1 = document.getSection("conditional-text");
        if (section1 != null) loadConditionTextSection(section1);
        Section section2 = document.getSection("background-text");
        if (section2 != null) loadBackgroundTextSection(section2);
        Section section3 = document.getSection("nameplate-text");
        if (section3 != null) loadNameplateTextSection(section3);
        Section section4 = document.getSection("bubble-text");
        if (section4 != null) loadBubbleTextSection(section4);
        Section section5 = document.getSection("switch-text");
        if (section5 != null) loadSwitchTextSection(section5);
        Section section6 = document.getSection("vanilla-hud");
        if (section6 != null) loadVanillaHud(section6);
        Section section7 = document.getSection("static-text");
        if (section7 != null) loadStaticText(section7);
        Section section8 = document.getSection("shift-text");
        if (section8 != null) loadShiftText(section8);
    }

    private void loadShiftText(Section section) {
        for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
            String id = entry.getKey();
            if (entry.getValue() instanceof Section inner) {
                String text = inner.getString("text");
                String font = inner.getString("font");
                ShiftText shiftText = new ShiftText(font, text);
                Placeholder placeholder1 = registerSharedPlaceholder("%shared_np_shift_" + id + "%", () -> {
                    String parsed = shiftText.getText().fastCreate(null).render(null);
                    return AdventureHelper.surroundWithMiniMessageFont(parsed, ConfigManager.namespace() + ":" + font);
                });
                Placeholder placeholder2 = registerPlayerPlaceholder("%np_shift_" + id + "%", (player) -> {
                    String parsed = shiftText.getText().fastCreate(player).render(player);
                    return AdventureHelper.surroundWithMiniMessageFont(parsed, ConfigManager.namespace() + ":" + font);
                });
                Placeholder placeholder3 = registerRelationalPlaceholder("%rel_np_shift_" + id + "%", (p1, p2) -> {
                    String parsed = shiftText.getText().fastCreate(p1).render(p2);
                    return AdventureHelper.surroundWithMiniMessageFont(parsed, ConfigManager.namespace() + ":" + font);
                });
                List<PreParsedDynamicText> list = List.of(shiftText.getText());
                childrenText.put(placeholder1, list);
                childrenText.put(placeholder2, list);
                childrenText.put(placeholder3, list);
            }
        }
    }

    private void loadStaticText(Section section) {
        for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
            String id = entry.getKey();
            if (entry.getValue() instanceof Section inner) {
                StaticPosition position = StaticPosition.valueOf(inner.getString("position", "middle").toUpperCase(Locale.ENGLISH));
                int totalWidth = inner.getInt("value", 100);
                StaticText staticText = new StaticText(totalWidth, position, inner.getString("text"));
                Placeholder placeholder1 = registerSharedPlaceholder("%shared_np_static_" + id + "%", staticText::create);
                Placeholder placeholder2 = registerPlayerPlaceholder("%np_static_" + id + "%", staticText::create);
                Placeholder placeholder3 = registerRelationalPlaceholder("%rel_np_static_" + id + "%", staticText::create);
                List<PreParsedDynamicText> list = List.of(staticText.getText());
                childrenText.put(placeholder1, list);
                childrenText.put(placeholder2, list);
                childrenText.put(placeholder3, list);
            }
        }
    }

    private void loadVanillaHud(Section section) {
        for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
            String id = entry.getKey();
            if (entry.getValue() instanceof Section inner) {
                boolean reverse = inner.getBoolean("reverse", true);
                Image empty = requireNonNull(plugin.getImageManager().getImage(inner.getString("images.empty")), "image.empty should not be null");
                Image half = requireNonNull(plugin.getImageManager().getImage(inner.getString("images.half")), "image.half should not be null");
                Image full = requireNonNull(plugin.getImageManager().getImage(inner.getString("images.full")), "image.full should not be null");
                String currentValue = section.getString("placeholder.value", "1");
                String maxValue = section.getString("placeholder.max-value", currentValue);
                boolean removeShadow = section.getBoolean("remove-shadow", false);
                VanillaHud vanillaHud = new VanillaHud(empty, half, full, reverse, currentValue, maxValue, removeShadow);
                List<PreParsedDynamicText> list = List.of(vanillaHud.getCurrent(), vanillaHud.getMax());
                Placeholder placeholder1 = registerSharedPlaceholder("%shared_np_vanilla_" + id + "%", vanillaHud::create);
                Placeholder placeholder2 = registerPlayerPlaceholder("%np_vanilla_" + id + "%", vanillaHud::create);
                Placeholder placeholder3 = registerRelationalPlaceholder("%np_vanilla_" + id + "%", vanillaHud::create);
                childrenText.put(placeholder1, list);
                childrenText.put(placeholder2, list);
                childrenText.put(placeholder3, list);
            }
        }
    }

    private void loadSwitchTextSection(Section section) {
        for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
            String id = entry.getKey();
            if (entry.getValue() instanceof Section inner) {
                PreParsedDynamicText placeholderToSwitch = new PreParsedDynamicText(inner.getString("switch"));
                PreParsedDynamicText defaultValue = new PreParsedDynamicText(inner.getString("default", ""));
                ArrayList<PreParsedDynamicText> list = new ArrayList<>();
                list.add(placeholderToSwitch);
                this.delayedInitTexts.add(defaultValue);
                Map<String, PreParsedDynamicText> valueMap = new HashMap<>();
                Section results = inner.getSection("case");
                if (results != null) {
                    for (Map.Entry<String, Object> strEntry : results.getStringRouteMappedValues(false).entrySet()) {
                        if (strEntry.getValue() instanceof String string) {
                            PreParsedDynamicText preParsedDynamicText = new PreParsedDynamicText(string);
                            valueMap.put(strEntry.getKey(), preParsedDynamicText);
                            this.delayedInitTexts.add(preParsedDynamicText);
                        }
                    }
                }
                Placeholder placeholder1 = registerSharedPlaceholder("%shared_np_switch_" + id + "%", () -> {
                    String value = placeholderToSwitch.fastCreate(null).render(null);
                    PreParsedDynamicText text = valueMap.getOrDefault(value, defaultValue);
                    return text.fastCreate(null).render(null);
                });
                Placeholder placeholder2 = registerPlayerPlaceholder("%np_switch_" + id + "%", (player) -> {
                    String value = placeholderToSwitch.fastCreate(player).render(player);
                    PreParsedDynamicText text = valueMap.getOrDefault(value, defaultValue);
                    player.forceUpdate(text.placeholders(), Collections.emptySet());
                    return text.fastCreate(player).render(player);
                });
                Placeholder placeholder3 = registerRelationalPlaceholder("%rel_np_switch_" + id + "%", (p1, p2) -> {
                    String value = placeholderToSwitch.fastCreate(p1).render(p2);
                    PreParsedDynamicText text = valueMap.getOrDefault(value, defaultValue);
                    p1.forceUpdate(text.placeholders(), Set.of(p2));
                    return text.fastCreate(p1).render(p2);
                });
                childrenText.put(placeholder1, list);
                childrenText.put(placeholder2, list);
                childrenText.put(placeholder3, list);
            }
        }
    }

    private void loadBubbleTextSection(Section section) {
        for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
            String id = entry.getKey();
            if (entry.getValue() instanceof Section inner) {
                String bbID = inner.getString("bubble");
                Bubble bubble = plugin.getBubbleManager().getBubble(bbID);
                if (bubble != null) {
                    AdaptiveImageText<Bubble> adaptiveImageText = AdaptiveImageText.create(
                            id, inner.getString("text"), bubble, inner.getBoolean("remove-shadow"),
                            inner.getInt("left-margin", 1), inner.getInt("right-margin", 1)
                    );
                    List<PreParsedDynamicText> list = new ArrayList<>();
                    list.add(adaptiveImageText.getPreParsedDynamicText());
                    Placeholder placeholder1 = this.registerSharedPlaceholder("%shared_np_bubble_" + id + "%", () -> adaptiveImageText.getFull(null, null));
                    Placeholder placeholder2 = this.registerPlayerPlaceholder("%np_bubble_" + id + "%", (p) -> adaptiveImageText.getFull(p, p));
                    Placeholder placeholder3 = this.registerRelationalPlaceholder("%rel_np_bubble_" + id + "%", adaptiveImageText::getFull);
                    Placeholder placeholder4 = this.registerSharedPlaceholder("%shared_np_bubble-text_" + id + "%", () -> adaptiveImageText.getText(null, null));
                    Placeholder placeholder5 = this.registerPlayerPlaceholder("%np_bubble-text_" + id + "%", (p) -> adaptiveImageText.getText(p, p));
                    Placeholder placeholder6 = this.registerRelationalPlaceholder("%rel_np_bubble-text_" + id + "%", adaptiveImageText::getText);
                    Placeholder placeholder7 = this.registerSharedPlaceholder("%shared_np_bubble-image_" + id + "%", () -> adaptiveImageText.getImage(null, null));
                    Placeholder placeholder8 = this.registerPlayerPlaceholder("%np_bubble-image_" + id + "%", (p) -> adaptiveImageText.getImage(p, p));
                    Placeholder placeholder9 = this.registerRelationalPlaceholder("%rel_np_bubble-image_" + id + "%", adaptiveImageText::getImage);
                    childrenText.put(placeholder1, list);
                    childrenText.put(placeholder2, list);
                    childrenText.put(placeholder3, list);
                    childrenText.put(placeholder4, list);
                    childrenText.put(placeholder5, list);
                    childrenText.put(placeholder6, list);
                    childrenText.put(placeholder7, list);
                    childrenText.put(placeholder8, list);
                    childrenText.put(placeholder9, list);
                } else {
                    plugin.getPluginLogger().warn("Nameplate [" + bbID + "] not exists");
                }
            }
        }
    }

    private void loadNameplateTextSection(Section section) {
        for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
            String id = entry.getKey();
            if (entry.getValue() instanceof Section inner) {
                String npID = inner.getString("nameplate");
                Nameplate nameplate = plugin.getNameplateManager().getNameplate(npID);
                if (nameplate != null) {
                    AdaptiveImageText<Nameplate> adaptiveImageText = AdaptiveImageText.create(
                            id, inner.getString("text"), nameplate, inner.getBoolean("remove-shadow"),
                            inner.getInt("left-margin", 1), inner.getInt("right-margin", 1)
                    );
                    List<PreParsedDynamicText> list = new ArrayList<>();
                    list.add(adaptiveImageText.getPreParsedDynamicText());
                    Placeholder placeholder1 = this.registerSharedPlaceholder("%shared_np_nameplate_" + id + "%", () -> adaptiveImageText.getFull(null, null));
                    Placeholder placeholder2 = this.registerPlayerPlaceholder("%np_nameplate_" + id + "%", (p) -> adaptiveImageText.getFull(p, p));
                    Placeholder placeholder3 = this.registerRelationalPlaceholder("%rel_np_nameplate_" + id + "%", adaptiveImageText::getFull);
                    Placeholder placeholder4 = this.registerSharedPlaceholder("%shared_np_nameplate-text_" + id + "%", () -> adaptiveImageText.getText(null, null));
                    Placeholder placeholder5 = this.registerPlayerPlaceholder("%np_nameplate-text_" + id + "%", (p) -> adaptiveImageText.getText(p, p));
                    Placeholder placeholder6 = this.registerRelationalPlaceholder("%rel_np_nameplate-text_" + id + "%", adaptiveImageText::getText);
                    Placeholder placeholder7 = this.registerSharedPlaceholder("%shared_np_nameplate-image_" + id + "%", () -> adaptiveImageText.getImage(null, null));
                    Placeholder placeholder8 = this.registerPlayerPlaceholder("%np_nameplate-image_" + id + "%", (p) -> adaptiveImageText.getImage(p, p));
                    Placeholder placeholder9 = this.registerRelationalPlaceholder("%rel_np_nameplate-image_" + id + "%", adaptiveImageText::getImage);
                    childrenText.put(placeholder1, list);
                    childrenText.put(placeholder2, list);
                    childrenText.put(placeholder3, list);
                    childrenText.put(placeholder4, list);
                    childrenText.put(placeholder5, list);
                    childrenText.put(placeholder6, list);
                    childrenText.put(placeholder7, list);
                    childrenText.put(placeholder8, list);
                    childrenText.put(placeholder9, list);
                } else {
                    plugin.getPluginLogger().warn("Nameplate [" + npID + "] not exists");
                }
            }
        }
    }

    private void loadBackgroundTextSection(Section section) {
        for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
            String id = entry.getKey();
            if (entry.getValue() instanceof Section inner) {
                String bgID = inner.getString("background");
                Background background = plugin.getBackgroundManager().getBackground(bgID);
                if (background != null) {
                    AdaptiveImageText<Background> adaptiveImageText = AdaptiveImageText.create(
                            id, inner.getString("text"), background, inner.getBoolean("remove-shadow"),
                            inner.getInt("left-margin", 2), inner.getInt("right-margin", 0)
                    );
                    List<PreParsedDynamicText> list = new ArrayList<>();
                    list.add(adaptiveImageText.getPreParsedDynamicText());
                    Placeholder placeholder1 = this.registerSharedPlaceholder("%shared_np_background_" + id + "%", () -> adaptiveImageText.getFull(null, null));
                    Placeholder placeholder2 = this.registerPlayerPlaceholder("%np_background_" + id + "%", (p) -> adaptiveImageText.getFull(p, p));
                    Placeholder placeholder3 = this.registerRelationalPlaceholder("%rel_np_background_" + id + "%", adaptiveImageText::getFull);
                    Placeholder placeholder4 = this.registerSharedPlaceholder("%shared_np_background-text_" + id + "%", () -> adaptiveImageText.getText(null, null));
                    Placeholder placeholder5 = this.registerPlayerPlaceholder("%np_background-text_" + id + "%", (p) -> adaptiveImageText.getText(p, p));
                    Placeholder placeholder6 = this.registerRelationalPlaceholder("%rel_np_background-text_" + id + "%", adaptiveImageText::getText);
                    Placeholder placeholder7 = this.registerSharedPlaceholder("%shared_np_background-image_" + id + "%", () -> adaptiveImageText.getImage(null, null));
                    Placeholder placeholder8 = this.registerPlayerPlaceholder("%np_background-image_" + id + "%", (p) -> adaptiveImageText.getImage(p, p));
                    Placeholder placeholder9 = this.registerRelationalPlaceholder("%rel_np_background-image_" + id + "%", adaptiveImageText::getImage);
                    childrenText.put(placeholder1, list);
                    childrenText.put(placeholder2, list);
                    childrenText.put(placeholder3, list);
                    childrenText.put(placeholder4, list);
                    childrenText.put(placeholder5, list);
                    childrenText.put(placeholder6, list);
                    childrenText.put(placeholder7, list);
                    childrenText.put(placeholder8, list);
                    childrenText.put(placeholder9, list);
                } else {
                    plugin.getPluginLogger().warn("Background [" + bgID + "] not exists");
                }
            }
        }
    }

    private void loadConditionTextSection(Section section) {
        for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
            String id = entry.getKey();
            if (entry.getValue() instanceof Section placeholderSection) {
                ArrayList<Pair<PreParsedDynamicText, Requirement[]>> orderedTexts = new ArrayList<>();
                for (Map.Entry<String, Object> conditionEntry : placeholderSection.getStringRouteMappedValues(false).entrySet()) {
                    if (conditionEntry.getValue() instanceof Section inner) {
                        String text = inner.getString("text");
                        Requirement[] requirements = plugin.getRequirementManager().parseRequirements(inner.getSection("conditions"));
                        PreParsedDynamicText preParsedDynamicText = new PreParsedDynamicText(text);
                        orderedTexts.add(Pair.of(preParsedDynamicText, requirements));
                        this.delayedInitTexts.add(preParsedDynamicText);
                    }
                }
                this.registerSharedPlaceholder("%shared_np_conditional_" + id + "%", () -> {
                    outer:
                    for (Pair<PreParsedDynamicText, Requirement[]> orderedText : orderedTexts) {
                        for (Requirement requirement : orderedText.right()) {
                            if (!requirement.isSatisfied(null, null)) {
                                continue outer;
                            }
                        }
                        return orderedText.left().fastCreate(null).render(null);
                    }
                    return "";
                });
                this.registerPlayerPlaceholder("%np_conditional_" + id + "%", (player) -> {
                    for (Pair<PreParsedDynamicText, Requirement[]> orderedText : orderedTexts) {
                        if (!player.isMet(orderedText.right())) {
                            continue;
                        }
                        PreParsedDynamicText text = orderedText.left();
                        player.forceUpdate(text.placeholders(), Collections.emptySet());
                        return text.fastCreate(player).render(player);
                    }
                    return "";
                });
                this.registerRelationalPlaceholder("%rel_np_conditional_" + id + "%", (p1, p2) -> {
                    for (Pair<PreParsedDynamicText, Requirement[]> orderedText : orderedTexts) {
                        if (!p1.isMet(p2, orderedText.right())) {
                            continue;
                        }
                        PreParsedDynamicText text = orderedText.left();
                        p1.forceUpdate(text.placeholders(), Set.of(p2));
                        return text.fastCreate(p1).render(p2);
                    }
                    return "";
                });
            }
        }
    }

    @Override
    public void refreshPlaceholders() {
        for (CNPlayer player : plugin.getOnlinePlayers()) {
            if (!player.isOnline()) continue;
            Set<Feature> featuresToNotifyUpdates = new HashSet<>();
            Map<Feature, List<CNPlayer>> relationalFeaturesToNotifyUpdates = new HashMap<>();
            List<Placeholder> placeholdersToUpdate = player.getRefreshValueTask();
            List<RelationalPlaceholder> delayedPlaceholdersToUpdate = new ArrayList<>();
            for (Placeholder placeholder : placeholdersToUpdate) {
                if (placeholder instanceof PlayerPlaceholder playerPlaceholder) {
                    TickStampData<String> previous = player.getValue(placeholder);
                    if (previous == null) {
                        String value = playerPlaceholder.request(player);
                        player.setValue(placeholder, new TickStampData<>(value, MainTask.getTicks(), true));
                        featuresToNotifyUpdates.addAll(player.getUsedFeatures(placeholder));
                    } else {
                        if (previous.ticks() == MainTask.getTicks()) {
                            if (previous.hasValueChanged()) {
                                featuresToNotifyUpdates.addAll(player.getUsedFeatures(placeholder));
                            }
                            continue;
                        }
                        String value = playerPlaceholder.request(player);
                        if (!previous.data().equals(value)) {
                            previous.data(value);
                            previous.updateTicks(true);
                            featuresToNotifyUpdates.addAll(player.getUsedFeatures(placeholder));
                        } else {
                            previous.updateTicks(false);
                        }
                    }
                } else if (placeholder instanceof RelationalPlaceholder relationalPlaceholder) {
                    delayedPlaceholdersToUpdate.add(relationalPlaceholder);
                } else if (placeholder instanceof SharedPlaceholder sharedPlaceholder) {
                    TickStampData<String> previous = player.getValue(placeholder);
                    if (previous == null) {
                        String value;
                        // if the shared placeholder has been updated by other players
                        if (MainTask.hasRequested(sharedPlaceholder.countId())) {
                            value = sharedPlaceholder.getLatestValue();
                        } else {
                            value = sharedPlaceholder.request();
                        }
                        player.setValue(placeholder, new TickStampData<>(value, MainTask.getTicks(), true));
                        featuresToNotifyUpdates.addAll(player.getUsedFeatures(placeholder));
                    } else {
                        // The placeholder has been refreshed by other codes
                        if (previous.ticks() == MainTask.getTicks()) {
                            if (previous.hasValueChanged()) {
                                featuresToNotifyUpdates.addAll(player.getUsedFeatures(placeholder));
                            }
                            continue;
                        }
                        String value;
                        // if the shared placeholder has been updated by other players
                        if (MainTask.hasRequested(sharedPlaceholder.countId())) {
                            value = sharedPlaceholder.getLatestValue();
                        } else {
                            value = sharedPlaceholder.request();
                        }
                        if (!previous.data().equals(value)) {
                            previous.data(value);
                            previous.updateTicks(true);
                            featuresToNotifyUpdates.addAll(player.getUsedFeatures(placeholder));
                        } else {
                            previous.updateTicks(false);
                        }
                    }
                }
            }

            for (RelationalPlaceholder placeholder : delayedPlaceholdersToUpdate) {
                for (CNPlayer nearby : player.nearbyPlayers()) {
                    TickStampData<String> previous = player.getRelationalValue(placeholder, nearby);
                    if (previous == null) {
                        String value = placeholder.request(player, nearby);
                        player.setRelationalValue(placeholder, nearby, new TickStampData<>(value, MainTask.getTicks(), true));
                        for (Feature feature : player.getUsedFeatures(placeholder)) {
                            // Filter features that will not be updated for all players
                            if (!featuresToNotifyUpdates.contains(feature)) {
                                List<CNPlayer> players = relationalFeaturesToNotifyUpdates.computeIfAbsent(feature, k -> new ArrayList<>());
                                players.add(nearby);
                            }
                        }
                    } else {
                        if (previous.ticks() == MainTask.getTicks()) {
                            if (previous.hasValueChanged()) {
                                for (Feature feature : player.getUsedFeatures(placeholder)) {
                                    // Filter features that will not be updated for all players
                                    if (!featuresToNotifyUpdates.contains(feature)) {
                                        List<CNPlayer> players = relationalFeaturesToNotifyUpdates.computeIfAbsent(feature, k -> new ArrayList<>());
                                        players.add(nearby);
                                    }
                                }
                            }
                            continue;
                        }
                        String value = placeholder.request(player, nearby);
                        if (!previous.data().equals(value)) {
                            previous.data(value);
                            previous.updateTicks(true);
                            for (Feature feature : player.getUsedFeatures(placeholder)) {
                                // Filter features that will not be updated for all players
                                if (!featuresToNotifyUpdates.contains(feature)) {
                                    List<CNPlayer> players = relationalFeaturesToNotifyUpdates.computeIfAbsent(feature, k -> new ArrayList<>());
                                    players.add(nearby);
                                }
                            }
                        } else {
                            previous.updateTicks(false);
                        }
                    }
                }
            }

            // Switch to another thread for updating
            plugin.getScheduler().async().execute(() -> {
                // Async task takes time and the player might have been offline
                if (!player.isOnline()) return;
                for (Feature feature : featuresToNotifyUpdates) {
                    feature.notifyPlaceholderUpdates(player, false);
                }
                for (Map.Entry<Feature, List<CNPlayer>> innerEntry : relationalFeaturesToNotifyUpdates.entrySet()) {
                    Feature feature = innerEntry.getKey();
                    if (feature instanceof RelationalFeature relationalFeature) {
                        for (CNPlayer other : innerEntry.getValue()) {
                            relationalFeature.notifyPlaceholderUpdates(player, other, false);
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getRefreshInterval(String id) {
        return refreshIntervals.getOrDefault(id, ConfigManager.defaultRefreshInterval());
    }

    @Override
    public <T extends Placeholder> T registerPlaceholder(T placeholder) {
        Placeholder nested = nestedPlaceholders.get(placeholder.id());
        if (nested != null) {
            placeholder.addChildren(nested.children());
        }
        registeredPlaceholders.put(placeholder.id(), placeholder);
        return placeholder;
    }

    @Override
    public PlayerPlaceholder registerPlayerPlaceholder(String id, int refreshInterval, Function<CNPlayer, String> function) {
        PlayerPlaceholderImpl impl = new PlayerPlaceholderImpl(this, id, refreshInterval, function);
        return registerPlaceholder(impl);
    }

    @Override
    public RelationalPlaceholder registerRelationalPlaceholder(String id, int refreshInterval, BiFunction<CNPlayer, CNPlayer, String> function) {
        RelationalPlaceholderImpl impl = new RelationalPlaceholderImpl(this, id, refreshInterval, function);
        return registerPlaceholder(impl);
    }

    @Override
    public SharedPlaceholder registerSharedPlaceholder(String id, int refreshInterval, Supplier<String> contextSupplier) {
        SharedPlaceholderImpl impl = new SharedPlaceholderImpl(this, id, refreshInterval, contextSupplier);
        return registerPlaceholder(impl);
    }

    @Override
    public Placeholder getPlaceholder(String id) {
        Placeholder placeholder = registeredPlaceholders.get(id);
        if (placeholder != null) return placeholder;
        return plugin.getPlatform().registerPlatformPlaceholder(id);
    }

    @Override
    public Placeholder getRegisteredPlaceholder(String id) {
        return registeredPlaceholders.get(id);
    }

    @Override
    public void unregisterPlaceholder(String id) {
        registeredPlaceholders.remove(id);
    }

    @Override
    public List<String> detectPlaceholders(String text) {
        int firstPercent = text.indexOf('%');
        if (firstPercent == -1) return Collections.emptyList();
        if (firstPercent == 0 && text.charAt(text.length() - 1) == '%' && text.indexOf('%', 1) == text.length() - 1) {
            return Collections.singletonList(text);
        }
        List<String> placeholders = new ArrayList<>();
        Matcher m = PATTERN.matcher(text);
        while (m.find()) {
            placeholders.add(m.group());
        }
        return placeholders;
    }
}
