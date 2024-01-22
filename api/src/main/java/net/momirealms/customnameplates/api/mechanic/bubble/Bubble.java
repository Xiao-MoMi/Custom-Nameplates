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

package net.momirealms.customnameplates.api.mechanic.bubble;

import net.momirealms.customnameplates.api.mechanic.character.ConfiguredChar;
import net.momirealms.customnameplates.api.mechanic.font.OffsetFont;
import net.momirealms.customnameplates.api.util.FontUtils;

public class Bubble {

    private String startFormat, endFormat;
    private  String displayName;
    private ConfiguredChar left, middle, right, tail;

    private Bubble() {
    }

    public Bubble(String startFormat, String endFormat, String displayName, ConfiguredChar left, ConfiguredChar middle, ConfiguredChar right, ConfiguredChar tail) {
        this.startFormat = startFormat;
        this.endFormat = endFormat;
        this.displayName = displayName;
        this.left = left;
        this.middle = middle;
        this.right = right;
        this.tail = tail;
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
        int mid_amount;
        if (textWidth - 1 <= tail.getWidth()) {
            mid_amount = -1;
        } else {
            mid_amount = (textWidth - 1 - tail.getWidth()) / (middle.getWidth());
        }
        if (mid_amount == -1) {
            stringBuilder.append("<#FDFEFE>").append(tail.getCharacter()).append("</#FDFEFE>").append(OffsetFont.NEG_1.getCharacter());
        } else if (mid_amount == 0) {
            stringBuilder.append("<#FDFEFE>").append(tail.getCharacter()).append("</#FDFEFE>").append(OffsetFont.NEG_1.getCharacter());
            stringBuilder.append(OffsetFont.getShortestNegChars(middle.getWidth() - (textWidth - 1 - tail.getWidth()) % middle.getWidth()));
            stringBuilder.append(middle.getCharacter()).append(OffsetFont.NEG_1.getCharacter());
        } else {
            stringBuilder.append(middle.getCharacter()).append(OffsetFont.NEG_1.getCharacter());
            for (int i = 0; i < mid_amount; i++) {
                if (i == mid_amount / 2) stringBuilder.append("<#FDFEFE>").append(tail.getCharacter()).append("</#FDFEFE>").append(OffsetFont.NEG_1.getCharacter());
                else stringBuilder.append(middle.getCharacter()).append(OffsetFont.NEG_1.getCharacter());
            }
            stringBuilder.append(OffsetFont.getShortestNegChars(middle.getWidth() - (textWidth - 1 - tail.getWidth()) % middle.getWidth()));
            stringBuilder.append(middle.getCharacter()).append(OffsetFont.NEG_1.getCharacter());
        }
        stringBuilder.append("<#FDFEFE>");
        stringBuilder.append(right.getCharacter());
        stringBuilder.append("</#FDFEFE>");
        stringBuilder.append(OffsetFont.getShortestNegChars(textWidth + right.getWidth()));
        stringBuilder.append("</#FEFEFE>");
        return stringBuilder.toString();
    }

    public String getSuffixWithFont(int textWidth) {
        return FontUtils.surroundNameplateFont(getSuffix(textWidth));
    }

    public String getSuffix(int textWidth) {
        return OffsetFont.getShortestNegChars(textWidth + textWidth % 2 + 1);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Bubble bubble;

        public static Builder of() {
            return new Builder();
        }

        public Builder() {
            this.bubble = new Bubble();
        }

        public Builder startFormat(String startFormat) {
            this.bubble.startFormat = startFormat;
            return this;
        }

        public Builder endFormat(String endFormat) {
            this.bubble.endFormat = endFormat;
            return this;
        }

        public Builder left(ConfiguredChar left) {
            this.bubble.left = left;
            return this;
        }

        public Builder displayName(String displayName) {
            this.bubble.displayName = displayName;
            return this;
        }

        public Builder middle(ConfiguredChar middle) {
            this.bubble.middle = middle;
            return this;
        }

        public Builder right(ConfiguredChar right) {
            this.bubble.right = right;
            return this;
        }

        public Builder tail(ConfiguredChar tail) {
            this.bubble.tail = tail;
            return this;
        }

        public Bubble build() {
            return bubble;
        }
    }

    public String getStartFormat() {
        return startFormat;
    }

    public String getEndFormat() {
        return endFormat;
    }

    public String getDisplayName() {
        return displayName;
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

    public ConfiguredChar getTail() {
        return tail;
    }
}
