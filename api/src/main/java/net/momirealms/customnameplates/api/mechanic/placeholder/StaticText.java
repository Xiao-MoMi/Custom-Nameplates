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
import net.momirealms.customnameplates.api.mechanic.font.OffsetFont;
import net.momirealms.customnameplates.api.util.FontUtils;
import org.bukkit.OfflinePlayer;

public class StaticText {

    private String text;
    private int value;
    private StaticState staticState;

    private StaticText() {
    }

    public StaticText(String text, int value, StaticState staticState) {
        this.text = text;
        this.value = value;
        this.staticState = staticState;
    }

    public String getText() {
        return text;
    }

    public int getValue() {
        return value;
    }

    public static Builder builder() {
        return new Builder();
    }

    public StaticState getStaticState() {
        return staticState;
    }

    public String getValue(OfflinePlayer player) {
        String parsed = PlaceholderAPI.setPlaceholders(player, text);
        int parsedWidth = FontUtils.getTextWidth(parsed);
        switch (staticState) {
            case LEFT -> {
                return parsed + FontUtils.surroundNameplateFont(OffsetFont.getOffsetChars(value - parsedWidth));
            }
            case RIGHT -> {
                return FontUtils.surroundNameplateFont(OffsetFont.getOffsetChars(value - parsedWidth)) + parsed;
            }
            case MIDDLE -> {
                int half = (value - parsedWidth) / 2;
                String left = FontUtils.surroundNameplateFont(OffsetFont.getOffsetChars(half));
                String right = FontUtils.surroundNameplateFont(OffsetFont.getOffsetChars(value - parsedWidth - half));
                return left + parsed + right;
            }
            default -> {
                return "";
            }
        }
    }

    public static class Builder {

        private final StaticText text;

        public Builder() {
            this.text = new StaticText();
        }

        public static Builder of() {
            return new Builder();
        }

        public Builder value(int value) {
            text.value = value;
            return this;
        }

        public Builder text(String value) {
            text.text = value;
            return this;
        }

        public Builder state(StaticState state) {
            text.staticState = state;
            return this;
        }

        public StaticText build() {
            return text;
        }
    }

    public enum StaticState {
        LEFT,
        MIDDLE,
        RIGHT
    }
}
