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
import net.momirealms.customnameplates.api.feature.PreParsedDynamicText;
import net.momirealms.customnameplates.backend.requirement.AbstractRequirement;

import java.util.Objects;
import java.util.Set;

public abstract class PlaceholdersRequirement extends AbstractRequirement {

    private final PreParsedDynamicText t1;
    private final PreParsedDynamicText t2;

    public PlaceholdersRequirement(int refreshInterval, PreParsedDynamicText t1, PreParsedDynamicText t2) {
        super(refreshInterval);
        this.t1 = t1;
        this.t2 = t2;
    }

    @Override
    public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
        Set<CNPlayer> set = Set.of(p2);
        p1.forceUpdatePlaceholders(t1.placeholders(), set);
        p1.forceUpdatePlaceholders(t2.placeholders(), set);
        String a1 = t1.fastCreate(p1).render(p2);
        String a2 = t2.fastCreate(p1).render(p2);
        return checkArgument(a1, a2);
    }

    protected abstract boolean checkArgument(String a1, String a2);

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        PlaceholdersRequirement that = (PlaceholdersRequirement) object;
        return Objects.equals(t1, that.t1) && Objects.equals(t2, that.t2);
    }

    @Override
    public int hashCode() {
        return 73 + t1.hashCode() * 509 + t2.hashCode() * 211 + type().hashCode() * 311;
    }
}
