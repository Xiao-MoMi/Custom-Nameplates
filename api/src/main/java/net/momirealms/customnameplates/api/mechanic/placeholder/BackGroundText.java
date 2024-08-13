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
import net.momirealms.customnameplates.api.mechanic.background.BackGround;
import net.momirealms.customnameplates.api.util.FontUtils;
import org.bukkit.OfflinePlayer;

public class BackGroundText {

    private String text;
    private BackGround backGround;
    private boolean removeShadow;

    private BackGroundText() {
    }

    public BackGroundText(String text, BackGround backGround, boolean removeShadow) {
        this.text = text;
        this.backGround = backGround;
        this.removeShadow = removeShadow;
    }

    public String getText() {
        return text;
    }

    public BackGround getBackGround() {
        return backGround;
    }

    public String getValue(OfflinePlayer player) {
        String parsed = PlaceholderAPI.setPlaceholders(player, text);
        if (parsed.equals("")) return "";
        int parsedWidth = FontUtils.getTextWidth(parsed);
        String bg = FontUtils.surroundNameplateFont(backGround.getBackGroundImage(parsedWidth));
        return (removeShadow ? "<#FFFEFD>" + bg + "</#FFFEFD>" : bg)+ parsed;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final BackGroundText text;

        public Builder() {
            this.text = new BackGroundText();
        }

        public static Builder of() {
            return new Builder();
        }

        public Builder text(String value) {
            text.text = value;
            return this;
        }

        public Builder background(BackGround backGround) {
            text.backGround = backGround;
            return this;
        }

        public Builder removeShadow(boolean remove) {
            text.removeShadow = remove;
            return this;
        }

        public BackGroundText build() {
            return text;
        }
    }
}
