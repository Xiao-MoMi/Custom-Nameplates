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

package net.momirealms.customnameplates.api.requirement;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.customnameplates.common.plugin.feature.Reloadable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages the registration, parsing, and retrieval of requirements. Supports both rich and simple requirements.
 */
public interface RequirementManager extends Reloadable {

    /**
     * Parses the requirements from the specified YAML section.
     *
     * @param section the YAML section to parse requirements from
     * @return an array of parsed requirements
     */
    Requirement[] parseRequirements(Section section);

    /**
     * Registers a new requirement factory for one or more types.
     *
     * @param requirementFactory the factory responsible for creating requirements
     * @param types              the types of requirements that the factory can create
     * @return true if the registration was successful, false otherwise
     */
    boolean registerRequirement(@NotNull RequirementFactory requirementFactory, @NotNull String... types);

    /**
     * Unregisters a requirement type.
     *
     * @param type the type of requirement to unregister
     * @return true if the requirement was unregistered successfully, false otherwise
     */
    boolean unregisterRequirement(@NotNull String type);

    /**
     * Retrieves a requirement factory for a specific type.
     *
     * @param type the type of the requirement
     * @return the corresponding requirement factory, or null if not found
     */
    @Nullable
    RequirementFactory getRequirementFactory(@NotNull String type);

    /**
     * Checks if a requirement type is registered.
     *
     * @param type the type of requirement to check
     * @return true if the requirement type is registered, false otherwise
     */
    boolean hasRequirement(@NotNull String type);

    /**
     * Parses a rich requirement from the specified YAML section. Rich requirements can have complex structures.
     *
     * @param section the YAML section to parse
     * @return the parsed rich requirement
     */
    @NotNull
    Requirement parseRichRequirement(@NotNull Section section);

    /**
     * Parses a simple requirement based on its type and value.
     *
     * @param type  the type of the requirement
     * @param value the value of the requirement
     * @return the parsed simple requirement
     */
    @NotNull
    Requirement parseSimpleRequirement(@NotNull String type, @NotNull Object value);

    /**
     * Gets the count id for the requirement for faster lookup
     *
     * @param requirement requirement
     * @return the id
     */
    int countId(Requirement requirement);
}
