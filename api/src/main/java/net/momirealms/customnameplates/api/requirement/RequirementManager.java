package net.momirealms.customnameplates.api.requirement;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customnameplates.common.plugin.feature.Reloadable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RequirementManager extends Reloadable {

    Requirement[] parseRequirements(Section section);

    boolean registerRequirement(@NotNull RequirementFactory requirementFactory, @NotNull String... types);

    boolean unregisterRequirement(@NotNull String type);

    @Nullable RequirementFactory getRequirementFactory(@NotNull String type);

    boolean hasRequirement(@NotNull String type);

    @NotNull Requirement parseRichRequirement(@NotNull Section section);

    @NotNull Requirement parseSimpleRequirement(@NotNull String type, @NotNull Object value);
}
