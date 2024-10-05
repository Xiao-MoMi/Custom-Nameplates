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

public interface RequirementManager extends Reloadable {

    Requirement[] parseRequirements(Section section);

    boolean registerRequirement(@NotNull RequirementFactory requirementFactory, @NotNull String... types);

    boolean unregisterRequirement(@NotNull String type);

    @Nullable RequirementFactory getRequirementFactory(@NotNull String type);

    boolean hasRequirement(@NotNull String type);

    @NotNull Requirement parseRichRequirement(@NotNull Section section);

    @NotNull Requirement parseSimpleRequirement(@NotNull String type, @NotNull Object value);
}
