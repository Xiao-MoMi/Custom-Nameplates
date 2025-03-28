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

package net.momirealms.customnameplates.backend.requirement;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.PreParsedDynamicText;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.api.requirement.RequirementFactory;
import net.momirealms.customnameplates.api.requirement.RequirementManager;
import net.momirealms.customnameplates.api.util.ConfigUtils;
import net.momirealms.customnameplates.backend.requirement.builtin.*;
import net.momirealms.customnameplates.common.util.ListUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public abstract class AbstractRequirementManager implements RequirementManager {

    protected final CustomNameplates plugin;
    private final Object2ObjectOpenHashMap<String, RequirementFactory> requirementFactoryMap = new Object2ObjectOpenHashMap<>(128);
    private int countId = 0;
    private final Map<Requirement, Integer> registeredRequirements = new Object2IntOpenHashMap<>(128);

    public AbstractRequirementManager(CustomNameplates plugin) {
        this.plugin = plugin;
        this.registerInternalRequirements();
        this.registerPlatformRequirements();
    }

    @Override
    public void reload() {
        this.countId = 0;
        this.registeredRequirements.clear();
    }

    @Override
    public int countId(Requirement requirement) {
        if (requirement == null) return -1;
        if (this.registeredRequirements.containsKey(requirement)) return this.registeredRequirements.get(requirement);
        this.countId++;
        this.registeredRequirements.put(requirement, countId);
        return this.countId;
    }

    private void registerInternalRequirements() {
        this.registerRequirement((args, interval) -> new LaggyRequirement(interval, (int) args), "laggy-test");
        this.registerRequirement((args, interval) -> {
            Section section = ConfigUtils.safeCast(args, Section.class);
            Requirement[] requirements = parseRequirements(section);
            return new InvertedRequirement(interval, requirements);
        }, "inverted");
        this.registerRequirement((args, interval) -> {
            Section section = ConfigUtils.safeCast(args, Section.class);
            if (section == null) return Requirement.empty();
            PreParsedDynamicText dynamicText1 = new PreParsedDynamicText(section.getString("papi", ""), true);
            return new NotInListRequirement(interval, dynamicText1, new HashSet<>(section.getStringList("values")));
        }, "!in-list");
        this.registerRequirement((args, interval) -> {
            Section section = ConfigUtils.safeCast(args, Section.class);
            if (section == null) return Requirement.empty();
            PreParsedDynamicText dynamicText1 = new PreParsedDynamicText(section.getString("papi", ""), true);
            return new InListRequirement(interval, dynamicText1, new HashSet<>(section.getStringList("values")));
        }, "in-list");
        this.registerRequirement((args, interval) -> {
            Section section = ConfigUtils.safeCast(args, Section.class);
            if (section == null) return Requirement.empty();
            PreParsedDynamicText dynamicText1 = new PreParsedDynamicText(section.getString("value1", ""), true);
            String value2 = section.getString("value2", "");
            return new NotEndWithRequirement(interval, dynamicText1, value2);
        }, "!ends-with", "!endsWith", "!end-with", "!endWith");
        this.registerRequirement((args, interval) -> {
            Section section = ConfigUtils.safeCast(args, Section.class);
            if (section == null) return Requirement.empty();
            PreParsedDynamicText dynamicText1 = new PreParsedDynamicText(section.getString("value1", ""), true);
            String value2 = section.getString("value2", "");
            return new EndWithRequirement(interval, dynamicText1, value2);
        }, "ends-with", "endsWith", "end-with", "endWith");
        this.registerRequirement((args, interval) -> {
            Section section = ConfigUtils.safeCast(args, Section.class);
            if (section == null) return Requirement.empty();
            PreParsedDynamicText dynamicText1 = new PreParsedDynamicText(section.getString("value1", ""), true);
            String value2 = section.getString("value2", "");
            return new NotStartWithRequirement(interval, dynamicText1, value2);
        }, "!starts-with", "!startsWith", "!start-with", "!startWith");
        this.registerRequirement((args, interval) -> {
            Section section = ConfigUtils.safeCast(args, Section.class);
            if (section == null) return Requirement.empty();
            PreParsedDynamicText dynamicText1 = new PreParsedDynamicText(section.getString("value1", ""), true);
            String value2 = section.getString("value2", "");
            return new StartWithRequirement(interval, dynamicText1, value2);
        }, "starts-with", "startsWith", "start-with", "startWith");
        this.registerRequirement((args, interval) -> {
            Section section = ConfigUtils.safeCast(args, Section.class);
            if (section == null) return Requirement.empty();
            PreParsedDynamicText dynamicText1 = new PreParsedDynamicText(section.getString("value1", ""), true);
            PreParsedDynamicText dynamicText2 = new PreParsedDynamicText(section.getString("value2", ""), true);
            return new NotContainsRequirement(interval, dynamicText1, dynamicText2);
        }, "!contains", "!contain");
        this.registerRequirement((args, interval) -> {
            Section section = ConfigUtils.safeCast(args, Section.class);
            if (section == null) return Requirement.empty();
            PreParsedDynamicText dynamicText1 = new PreParsedDynamicText(section.getString("value1", ""), true);
            PreParsedDynamicText dynamicText2 = new PreParsedDynamicText(section.getString("value2", ""), true);
            return new ContainsRequirement(interval, dynamicText1, dynamicText2);
        }, "contains", "contain");
        this.registerRequirement((args, interval) -> {
            Section section = ConfigUtils.safeCast(args, Section.class);
            if (section == null) return Requirement.empty();
            PreParsedDynamicText dynamicText1 = new PreParsedDynamicText(section.getString("papi", ""), true);
            String regex = section.getString("regex", "");
            return new RegexRequirement(interval, dynamicText1, regex);
        }, "regex");
        this.registerRequirement((args, interval) -> {
            Section section = ConfigUtils.safeCast(args, Section.class);
            if (section == null) return Requirement.empty();
            PreParsedDynamicText dynamicText1 = new PreParsedDynamicText(section.getString("papi", ""), true);
            String regex = section.getString("regex", "");
            return new NotRegexRequirement(interval, dynamicText1, regex);
        }, "!regex");
        this.registerRequirement((args, interval) -> {
            Section section = ConfigUtils.safeCast(args, Section.class);
            if (section == null) return Requirement.empty();
            PreParsedDynamicText dynamicText1 = new PreParsedDynamicText(section.getString("value1", ""), true);
            PreParsedDynamicText dynamicText2 = new PreParsedDynamicText(section.getString("value2", ""), true);
            return new NumberLessRequirement(interval, dynamicText1, dynamicText2);
        }, "<");
        this.registerRequirement((args, interval) -> {
            Section section = ConfigUtils.safeCast(args, Section.class);
            if (section == null) return Requirement.empty();
            PreParsedDynamicText dynamicText1 = new PreParsedDynamicText(section.getString("value1", ""), true);
            PreParsedDynamicText dynamicText2 = new PreParsedDynamicText(section.getString("value2", ""), true);
            return new NumberNoGreaterRequirement(interval, dynamicText1, dynamicText2);
        }, "<=");
        this.registerRequirement((args, interval) -> {
            Section section = ConfigUtils.safeCast(args, Section.class);
            if (section == null) return Requirement.empty();
            PreParsedDynamicText dynamicText1 = new PreParsedDynamicText(section.getString("value1", ""), true);
            PreParsedDynamicText dynamicText2 = new PreParsedDynamicText(section.getString("value2", ""), true);
            return new NumberGreaterRequirement(interval, dynamicText1, dynamicText2);
        }, ">");
        this.registerRequirement((args, interval) -> {
            Section section = ConfigUtils.safeCast(args, Section.class);
            if (section == null) return Requirement.empty();
            PreParsedDynamicText dynamicText1 = new PreParsedDynamicText(section.getString("value1", ""), true);
            PreParsedDynamicText dynamicText2 = new PreParsedDynamicText(section.getString("value2", ""), true);
            return new NumberNoLessRequirement(interval, dynamicText1, dynamicText2);
        }, ">=");
        this.registerRequirement((args, interval) -> {
            Section section = ConfigUtils.safeCast(args, Section.class);
            if (section == null) return Requirement.empty();
            PreParsedDynamicText dynamicText1 = new PreParsedDynamicText(section.getString("value1", ""), true);
            PreParsedDynamicText dynamicText2 = new PreParsedDynamicText(section.getString("value2", ""), true);
            return new NumberEqualsRequirement(interval, dynamicText1, dynamicText2);
        }, "=", "==");
        this.registerRequirement((args, interval) -> {
            Section section = ConfigUtils.safeCast(args, Section.class);
            if (section == null) return Requirement.empty();
            PreParsedDynamicText dynamicText1 = new PreParsedDynamicText(section.getString("value1", ""), true);
            PreParsedDynamicText dynamicText2 = new PreParsedDynamicText(section.getString("value2", ""), true);
            return new NumberNotEqualsRequirement(interval, dynamicText1, dynamicText2);
        }, "!=");
        this.registerRequirement((args, interval) -> {
            Section section = ConfigUtils.safeCast(args, Section.class);
            if (section == null) return Requirement.empty();
            PreParsedDynamicText dynamicText1 = new PreParsedDynamicText(section.getString("value1", ""), true);
            PreParsedDynamicText dynamicText2 = new PreParsedDynamicText(section.getString("value2", ""), true);
            return new EqualsRequirement(interval, dynamicText1, dynamicText2);
        }, "equals", "equal");
        this.registerRequirement((args, interval) -> {
            Section section = ConfigUtils.safeCast(args, Section.class);
            if (section == null) return Requirement.empty();
            PreParsedDynamicText dynamicText1 = new PreParsedDynamicText(section.getString("value1", ""), true);
            PreParsedDynamicText dynamicText2 = new PreParsedDynamicText(section.getString("value2", ""), true);
            return new NotEqualsRequirement(interval, dynamicText1, dynamicText2);
        }, "!equals", "!equal");
        this.registerRequirement((args, interval) -> {
            Section section = ConfigUtils.safeCast(args, Section.class);
            Requirement[] requirements = parseRequirements(section);
            return new OrRequirement(interval, requirements);
        }, "||", "or");
        this.registerRequirement((args, interval) -> {
            Section section = ConfigUtils.safeCast(args, Section.class);
            Requirement[] requirements = parseRequirements(section);
            return new AndRequirement(interval, requirements);
        }, "&&", "and");
        this.registerRequirement((args, interval) -> {
            boolean has = (boolean) args;
            return new HasNameplateRequirement(interval, has);
        }, "has-nameplate");
        this.registerRequirement((args, interval) -> {
            boolean has = (boolean) args;
            return new HasBubbleRequirement(interval, has);
        }, "has-bubble");
        this.registerRequirement((args, interval) -> new NameplateRequirement(interval, new HashSet<>(ListUtils.toList(args))), "nameplate");
        this.registerRequirement((args, interval) -> new NotNameplateRequirement(interval, new HashSet<>(ListUtils.toList(args))), "!nameplate");
        this.registerRequirement((args, interval) -> new BubbleRequirement(interval, new HashSet<>(ListUtils.toList(args))), "bubble");
        this.registerRequirement((args, interval) -> new NotBubbleRequirement(interval, new HashSet<>(ListUtils.toList(args))), "!bubble");
    }

    protected abstract void registerPlatformRequirements();

    @Override
    public void disable() {
        this.requirementFactoryMap.clear();
    }

    @Override
    public boolean registerRequirement(@NotNull RequirementFactory requirementFactory, @NotNull String... types) {
        for (String type : types) {
            if (this.requirementFactoryMap.containsKey(type)) return false;
        }
        for (String type : types) {
            this.requirementFactoryMap.put(type, requirementFactory);
        }
        return true;
    }

    @Override
    public boolean unregisterRequirement(@NotNull String type) {
        return this.requirementFactoryMap.remove(type) != null;
    }

    @Override
    public @Nullable RequirementFactory getRequirementFactory(@NotNull String type) {
        return requirementFactoryMap.get(type);
    }

    @Override
    public boolean hasRequirement(@NotNull String type) {
        return requirementFactoryMap.containsKey(type);
    }

    @NotNull
    @Override
    public Requirement[] parseRequirements(Section section) {
        List<Requirement> requirements = new ArrayList<>();
        if (section != null)
            for (Map.Entry<String, Object> entry : section.getStringRouteMappedValues(false).entrySet()) {
                String typeOrName = entry.getKey();
                if (hasRequirement(typeOrName)) {
                    requirements.add(parseSimpleRequirement(typeOrName, entry.getValue()));
                } else {
                    Section inner = section.getSection(typeOrName);
                    if (inner != null) {
                        requirements.add(parseRichRequirement(inner));
                    } else {
                        plugin.getPluginLogger().warn("Invalid requirement type found at " + section.getRouteAsString() + "." + typeOrName);
                    }
                }
            }
        return requirements.toArray(new Requirement[0]);
    }

    @Override
    public @NotNull Requirement parseRichRequirement(@Nullable Section section) {
        if (section == null) {
            return Requirement.empty();
        }
        String type = section.getString("type");
        if (type == null) {
            plugin.getPluginLogger().warn("No requirement type found at " + section.getRouteAsString());
            return Requirement.empty();
        }
        var factory = getRequirementFactory(type);
        if (factory == null) {
            plugin.getPluginLogger().warn("Requirement type: " + type + " not exists");
            return Requirement.empty();
        }
        return factory.process(section.get("value"), section.getInt("refresh-interval", ConfigManager.defaultConditionRefreshInterval()));
    }

    @Override
    public @NotNull Requirement parseSimpleRequirement(@NotNull String type, @NotNull Object value) {
        RequirementFactory factory = getRequirementFactory(type);
        if (factory == null) {
            plugin.getPluginLogger().warn("Requirement type: " + type + " doesn't exist.");
            return Requirement.empty();
        }
        return factory.process(value, ConfigManager.defaultConditionRefreshInterval());
    }
}
