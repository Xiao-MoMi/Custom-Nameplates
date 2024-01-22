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
import net.momirealms.customnameplates.api.mechanic.nameplate.Nameplate;
import net.momirealms.customnameplates.api.util.FontUtils;
import org.bukkit.OfflinePlayer;

public class NameplateText {

    private String text;
    private Nameplate nameplate;

    private NameplateText() {
    }

    public NameplateText(String text, Nameplate nameplate) {
        this.text = text;
        this.nameplate = nameplate;
    }

    public String getText() {
        return text;
    }

    public Nameplate getNameplate() {
        return nameplate;
    }

    public String getValue(OfflinePlayer player) {
        String temp;
        switch (nameplate.getTeamColor()) {
            case CUSTOM -> temp = nameplate.getNamePrefix() + text + nameplate.getNameSuffix();
            case NONE -> temp = text;
            default -> temp = "<" + nameplate.getTeamColor().name() + ">" + text + "</" + nameplate.getTeamColor().name() + ">";
        }
        String parsed = PlaceholderAPI.setPlaceholders(player, temp);
        int parsedWidth = FontUtils.getTextWidth(parsed);
        return nameplate.getPrefixWithFont(parsedWidth) + parsed + nameplate.getSuffixWithFont(parsedWidth);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final NameplateText text;

        public Builder() {
            this.text = new NameplateText();
        }

        public static Builder of() {
            return new Builder();
        }

        public Builder text(String value) {
            text.text = value;
            return this;
        }

        public Builder nameplate(Nameplate value) {
            text.nameplate = value;
            return this;
        }

        public NameplateText build() {
            return text;
        }
    }
}
