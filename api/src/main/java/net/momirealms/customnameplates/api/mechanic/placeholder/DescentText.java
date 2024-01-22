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
import net.momirealms.customnameplates.api.util.FontUtils;
import org.bukkit.OfflinePlayer;

public class DescentText {

    private int ascent;
    private String text;
    private boolean isUnicode;

    public DescentText(int ascent, String text, boolean isUnicode) {
        this.ascent = ascent;
        this.text = text;
        this.isUnicode = isUnicode;
    }

    private DescentText() {

    }

    public int getAscent() {
        return ascent;
    }

    public boolean isUnicode() {
        return isUnicode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getValue(OfflinePlayer player) {
        var parsed = PlaceholderAPI.setPlaceholders(player, text);
        return FontUtils.surroundAscentFont(parsed, ascent);
    }

    public static class Builder {

        private final DescentText descentText;

        public static Builder of() {
            return new Builder();
        }

        public Builder() {
            this.descentText = new DescentText();
        }

        public Builder ascent(int ascent) {
            descentText.ascent = ascent;
            return this;
        }

        public Builder descent(int descent) {
            descentText.ascent = 8 - descent;
            return this;
        }

        public Builder text(String text) {
            descentText.text = text;
            return this;
        }

        public Builder unicode(boolean unicode) {
            descentText.isUnicode = unicode;
            return this;
        }

        public DescentText build() {
            return descentText;
        }
    }
}
