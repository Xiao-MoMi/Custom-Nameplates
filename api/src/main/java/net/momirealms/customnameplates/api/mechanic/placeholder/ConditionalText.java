/*
 *  Copyright (C) <2022> <XiaoMoMi>
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

package net.momirealms.customnameplates.api.mechanic.placeholder;

import me.clip.placeholderapi.PlaceholderAPI;
import net.momirealms.customnameplates.api.manager.RequirementManager;
import net.momirealms.customnameplates.api.requirement.Condition;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.common.Pair;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class ConditionalText {

    private List<Pair<String, Requirement[]>> textList;

    private ConditionalText() {
    }

    public ConditionalText(List<Pair<String, Requirement[]>> textList) {
        this.textList = textList;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getValue(OfflinePlayer player) {
        Condition condition = new Condition(player);
        for (Pair<String, Requirement[]> pair : textList) {
            if (RequirementManager.isRequirementMet(condition, pair.right())) {
                return PlaceholderAPI.setPlaceholders(player, pair.left());
            }
        }
        return "";
    }

    public static class Builder {

        private final ConditionalText conditionalText;

        public static Builder of() {
            return new Builder();
        }

        public Builder() {
            this.conditionalText = new ConditionalText();
        }

        public Builder textList(List<Pair<String, Requirement[]>> textList) {
            conditionalText.textList = textList;
            return this;
        }

        public ConditionalText build() {
            return conditionalText;
        }
    }
}
