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

public abstract class PlaceholderRequirement<T> extends AbstractRequirement {

    private final PreParsedDynamicText text;
    private final T any;

    public PlaceholderRequirement(int refreshInterval, PreParsedDynamicText text, T any) {
        super(refreshInterval);
        this.text = text;
        this.any = any;
    }

    @Override
    public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
        p1.forceUpdatePlaceholders(text.placeholders(), Set.of(p2));
        String a1 = text.fastCreate(p1).render(p2);
        return checkArgument(a1, any);
    }

    protected abstract boolean checkArgument(String a1, T a2);

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        PlaceholderRequirement that = (PlaceholderRequirement) object;
        return Objects.equals(text, that.text) && Objects.equals(any, that.any);
    }

    @Override
    public int hashCode() {
        return 283 + text.hashCode() * 53 + any.hashCode() * 457 + type().hashCode() * 3;
    }
}
