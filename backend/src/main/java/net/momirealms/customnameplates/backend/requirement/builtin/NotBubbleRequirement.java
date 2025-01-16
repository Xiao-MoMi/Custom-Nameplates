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

public class NotBubbleRequirement extends AbstractRequirement {

    private final Set<String> bubbles;

    public NotBubbleRequirement(int refreshInterval, Set<String> bubbles) {
        super(refreshInterval);
        this.bubbles = bubbles;
    }

    @Override
    public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
        if (!ConfigManager.bubbleModule()) return false;
        String bubble = p1.currentBubble();
        if (bubble.equals("none")) bubble = CustomNameplates.getInstance().getBubbleManager().defaultBubbleId();
        return !bubbles.contains(bubble);
    }

    @Override
    public String type() {
        return "!bubble";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotBubbleRequirement that)) return false;
        return bubbles.equals(that.bubbles);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(bubbles);
    }
}
