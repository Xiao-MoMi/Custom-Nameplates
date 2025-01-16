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

package net.momirealms.customnameplates.backend.requirement.builtin;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.backend.requirement.AbstractRequirement;

import java.util.Objects;
import java.util.Set;

public class NameplateRequirement extends AbstractRequirement {

    private final Set<String> nameplates;

    public NameplateRequirement(int refreshInterval, Set<String> nameplates) {
        super(refreshInterval);
        this.nameplates = nameplates;
    }

    @Override
    public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
        if (!ConfigManager.nameplateModule()) return false;
        String nameplate = p1.currentNameplate();
        if (nameplate.equals("none")) nameplate = CustomNameplates.getInstance().getNameplateManager().defaultNameplateId();
        return nameplates.contains(nameplate);
    }

    @Override
    public String type() {
        return "nameplate";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NameplateRequirement that)) return false;
        return nameplates.equals(that.nameplates);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nameplates);
    }
}
