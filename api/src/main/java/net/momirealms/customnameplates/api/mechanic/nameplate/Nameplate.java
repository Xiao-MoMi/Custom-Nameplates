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

package net.momirealms.customnameplates.api.mechanic.nameplate;

import net.momirealms.customnameplates.api.mechanic.character.ConfiguredChar;
import net.momirealms.customnameplates.api.mechanic.font.OffsetFont;
import net.momirealms.customnameplates.api.util.FontUtils;
import net.momirealms.customnameplates.common.team.TeamColor;

public class Nameplate {

    private String displayName;
    private TeamColor teamColor;
    private String namePrefix;
    private String nameSuffix;
    private ConfiguredChar left;
    private ConfiguredChar middle;
    private ConfiguredChar right;

    private Nameplate() {
    }

    public Nameplate(
            String displayName,
            TeamColor teamColor,
            String namePrefix,
            String nameSuffix,
            ConfiguredChar left,
            ConfiguredChar middle,
            ConfiguredChar right
    ) {
        this.displayName = displayName;
        this.teamColor = teamColor;
        this.left = left;
        this.middle = middle;
        this.right = right;
        this.namePrefix = namePrefix;
        this.nameSuffix = nameSuffix;
    }

    public String getDisplayName() {
        return displayName;
    }

    public TeamColor getTeamColor() {
        return teamColor;
    }

    public ConfiguredChar getLeft() {
        return left;
    }

    public ConfiguredChar getMiddle() {
        return middle;
    }

    public ConfiguredChar getRight() {
        return right;
    }

    public String getNamePrefix() {
        if (teamColor == TeamColor.NONE) {
            return "";
        }
        if (teamColor == TeamColor.CUSTOM) {
            return namePrefix;
        }
        return "<" + teamColor.name() + ">";
    }

    public String getNameSuffix() {
        if (teamColor == TeamColor.NONE) {
            return "";
        }
        if (teamColor == TeamColor.CUSTOM) {
            return nameSuffix;
        }
        return "</" + teamColor.name() + ">";
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getPrefixWithFont(int textWidth) {
        return FontUtils.surroundNameplateFont(getPrefix(textWidth));
    }

    public String getPrefix(int textWidth) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<#FEFEFE>");
        stringBuilder.append(OffsetFont.getShortestNegChars(textWidth + left.getWidth() + 1));
        stringBuilder.append(left.getCharacter());
        stringBuilder.append(OffsetFont.NEG_1.getCharacter());
        int mid_amount = (textWidth+2) / (middle.getWidth());
        if (mid_amount != 0) {
            for (int i = 0; i < mid_amount; i++) {
                stringBuilder.append(middle.getCharacter());
                stringBuilder.append(OffsetFont.NEG_1.getCharacter());
            }
            stringBuilder.append(OffsetFont.getShortestNegChars(middle.getWidth() - (textWidth+2) % middle.getWidth() + 1)); // +1
        }
        stringBuilder.append("</#FEFEFE>");
        stringBuilder.append("<#FDFEFE>");
        stringBuilder.append(middle.getCharacter());
        stringBuilder.append(OffsetFont.NEG_1.getCharacter());
        stringBuilder.append(right.getCharacter());
        stringBuilder.append(OffsetFont.getShortestNegChars(textWidth + right.getWidth() + 1)); // -1;
        stringBuilder.append("</#FDFEFE>");
        return stringBuilder.toString();
    }

    public String getSuffixWithFont(int textWidth) {
        return FontUtils.surroundNameplateFont(getSuffix(textWidth));
    }

    public String getSuffix(int textWidth) {
        return OffsetFont.getShortestNegChars(textWidth + textWidth % 2 + 1);
    }

    public static class Builder {

        private final Nameplate nameplate;

        public Builder() {
            this.nameplate = new Nameplate();
        }

        public static Builder of() {
            return new Builder();
        }

        public Builder displayName(String display) {
            nameplate.displayName = display;
            return this;
        }

        public Builder teamColor(TeamColor teamColor) {
            nameplate.teamColor = teamColor;
            return this;
        }

        public Builder namePrefix(String namePrefix) {
            nameplate.namePrefix = namePrefix;
            return this;
        }

        public Builder nameSuffix(String nameSuffix) {
            nameplate.nameSuffix = nameSuffix;
            return this;
        }

        public Builder left(ConfiguredChar configuredChar) {
            nameplate.left = configuredChar;
            return this;
        }

        public Builder middle(ConfiguredChar configuredChar) {
            nameplate.middle = configuredChar;
            return this;
        }

        public Builder right(ConfiguredChar configuredChar) {
            nameplate.right = configuredChar;
            return this;
        }

        public Nameplate build() {
            return nameplate;
        }
    }
}
