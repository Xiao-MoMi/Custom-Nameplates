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

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Satisfied (shows) when any tracked placeholder changed recently.
 * After {@code timeoutMs} of inactivity, returns false to hide the actionbar.
 */
public class PlaceholderChangeTimeoutRequirement extends AbstractRequirement {

    private final List<PreParsedDynamicText> texts;
    private final long timeoutMs;
    private final ConcurrentHashMap<UUID, PlayerState> states = new ConcurrentHashMap<>();

    public PlaceholderChangeTimeoutRequirement(int refreshInterval, List<PreParsedDynamicText> texts, long timeoutMs) {
        super(refreshInterval);
        this.texts = texts;
        this.timeoutMs = timeoutMs;
    }

    @Override
    public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
        Set<CNPlayer> viewers = Set.of(p2);
        StringBuilder combined = new StringBuilder();
        for (PreParsedDynamicText text : texts) {
            p1.forceUpdatePlaceholders(text.placeholders(), viewers);
            combined.append(text.fastCreate(p1).render(p2));
        }
        String currentValue = combined.toString();

        long now = System.currentTimeMillis();
        PlayerState state = states.compute(p1.uuid(), (uuid, existing) -> {
            if (existing == null) {
                return new PlayerState(currentValue, now);
            }
            if (!currentValue.equals(existing.lastValue)) {
                existing.lastValue = currentValue;
                existing.lastChangeTime = now;
            }
            return existing;
        });

        return now - state.lastChangeTime < timeoutMs;
    }

    @Override
    public String type() {
        return "placeholder-change-timeout";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        PlaceholderChangeTimeoutRequirement that = (PlaceholderChangeTimeoutRequirement) object;
        return timeoutMs == that.timeoutMs && Objects.equals(texts, that.texts);
    }

    @Override
    public int hashCode() {
        return 397 + texts.hashCode() * 17 + Long.hashCode(timeoutMs) * 43;
    }

    private static class PlayerState {
        String lastValue;
        long lastChangeTime;

        PlayerState(String lastValue, long lastChangeTime) {
            this.lastValue = lastValue;
            this.lastChangeTime = lastChangeTime;
        }
    }
}
