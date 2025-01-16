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

public class HasBubbleRequirement extends AbstractRequirement {

    private final boolean has;

    public HasBubbleRequirement(int refreshInterval, boolean has) {
        super(refreshInterval);
        this.has = has;
    }

    @Override
    public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
        if (!ConfigManager.bubbleModule()) return !has;
        String bubble = p1.currentBubble();
        if (bubble.equals("none")) bubble = CustomNameplates.getInstance().getBubbleManager().defaultBubbleId();
        if (has) {
            return !bubble.equals("none");
        } else {
            return bubble.equals("none");
        }
    }

    @Override
    public String type() {
        return "has-bubble";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        HasBubbleRequirement that = (HasBubbleRequirement) object;
        return has == that.has;
    }

    @Override
    public int hashCode() {
        return (has ? 781 : 3);
    }
}
