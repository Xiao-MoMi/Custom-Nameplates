package net.momirealms.customnameplates.api.requirement;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customnameplates.api.CustomNameplates;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractRequirementManager implements RequirementManager {

    protected final CustomNameplates plugin;
    private final HashMap<String, RequirementFactory> requirementFactoryMap = new HashMap<>();

    public AbstractRequirementManager(CustomNameplates plugin) {
        this.plugin = plugin;
        this.registerBuiltInRequirements();
    }

    protected abstract void registerBuiltInRequirements();

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
                    requirements.add(parseRichRequirement(section.getSection(typeOrName)));
                }
            }
        return requirements.toArray(new Requirement[0]);
    }

    @Override
    public @NotNull Requirement parseRichRequirement(@NotNull Section section) {
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
        return factory.process(section.get("value"), section.getInt("refresh-interval", 10));
    }

    @Override
    public @NotNull Requirement parseSimpleRequirement(@NotNull String type, @NotNull Object value) {
        RequirementFactory factory = getRequirementFactory(type);
        if (factory == null) {
            plugin.getPluginLogger().warn("Requirement type: " + type + " doesn't exist.");
            return Requirement.empty();
        }
        return factory.process(value, 10);
    }
}
