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
import org.bukkit.OfflinePlayer;

import java.util.HashMap;

public class SwitchText {

    private HashMap<String, String> valueMap;
    private String toParse;
    private String defaultValue;

    public static Builder builder() {
        return new Builder();
    }

    private SwitchText() {

    }

    public SwitchText(HashMap<String, String> valueMap, String toParse) {
        this.valueMap = valueMap;
        this.toParse = toParse;
    }

    public String getValue(OfflinePlayer player) {
        String parsed = PlaceholderAPI.setPlaceholders(player, toParse);
        return PlaceholderAPI.setPlaceholders(player, valueMap.getOrDefault(parsed, defaultValue));
    }

    public static class Builder {

        private final SwitchText switchText;

        public Builder() {
            this.switchText = new SwitchText();
        }

        public static Builder of() {
            return new Builder();
        }

        public Builder toParse(String toParse) {
            this.switchText.toParse = toParse;
            return this;
        }

        public Builder defaultValue(String value) {
            this.switchText.defaultValue = value;
            return this;
        }

        public Builder valueMap(HashMap<String, String> valueMap) {
            this.switchText.valueMap = valueMap;
            return this;
        }

        public SwitchText build() {
            return switchText;
        }
    }
}
