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

package net.momirealms.customnameplates.api.mechanic.background;

import net.momirealms.customnameplates.api.mechanic.character.ConfiguredChar;
import net.momirealms.customnameplates.api.mechanic.font.OffsetFont;

public class BackGround {

    private ConfiguredChar left, offset_1, offset_2, offset_4, offset_8, offset_16, offset_32, offset_64, offset_128, right;
    private int leftMargin, rightMargin;

    private BackGround() {
    }

    public BackGround(
            ConfiguredChar left,
            ConfiguredChar offset_1,
            ConfiguredChar offset_2,
            ConfiguredChar offset_4,
            ConfiguredChar offset_8,
            ConfiguredChar offset_16,
            ConfiguredChar offset_32,
            ConfiguredChar offset_64,
            ConfiguredChar offset_128,
            ConfiguredChar right,
            int leftMargin,
            int rightMargin,
            boolean removeShadow
    ) {
        this.left = left;
        this.offset_1 = offset_1;
        this.offset_2 = offset_2;
        this.offset_4 = offset_4;
        this.offset_8 = offset_8;
        this.offset_16 = offset_16;
        this.offset_32 = offset_32;
        this.offset_64 = offset_64;
        this.offset_128 = offset_128;
        this.right = right;
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
    }

    public ConfiguredChar getLeft() {
        return left;
    }

    public ConfiguredChar getOffset_1() {
        return offset_1;
    }

    public ConfiguredChar getOffset_2() {
        return offset_2;
    }

    public ConfiguredChar getOffset_4() {
        return offset_4;
    }

    public ConfiguredChar getOffset_8() {
        return offset_8;
    }

    public ConfiguredChar getOffset_16() {
        return offset_16;
    }

    public ConfiguredChar getOffset_32() {
        return offset_32;
    }

    public ConfiguredChar getOffset_64() {
        return offset_64;
    }

    public ConfiguredChar getOffset_128() {
        return offset_128;
    }

    public ConfiguredChar getRight() {
        return right;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getBackGroundImage(int n) {
        String offset = OffsetFont.getShortestNegChars(n + rightMargin + 2);
        n = n + leftMargin + rightMargin + 2;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(left.getCharacter());
        while (n >= 128) {
            stringBuilder.append(OffsetFont.NEG_1.getCharacter());
            stringBuilder.append(offset_128.getCharacter());
            n -= 128;
        }
        if (n - 64 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.getCharacter());
            stringBuilder.append(offset_64.getCharacter());
            n -= 64;
        }
        if (n - 32 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.getCharacter());
            stringBuilder.append(offset_32.getCharacter());
            n -= 32;
        }
        if (n - 16 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.getCharacter());
            stringBuilder.append(offset_16.getCharacter());
            n -= 16;
        }
        if (n - 8 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.getCharacter());
            stringBuilder.append(offset_8.getCharacter());
            n -= 8;
        }
        if (n - 4 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.getCharacter());
            stringBuilder.append(offset_4.getCharacter());
            n -= 4;
        }
        if (n - 2 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.getCharacter());
            stringBuilder.append(offset_2.getCharacter());
            n -= 2;
        }
        if (n - 1 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.getCharacter());
            stringBuilder.append(offset_1.getCharacter());
        }
        stringBuilder.append(OffsetFont.NEG_1.getCharacter());
        stringBuilder.append(right.getCharacter());
        stringBuilder.append(offset);
        return stringBuilder.toString();
    }

    public static class Builder {

        private final BackGround backGround;

        public static Builder of() {
            return new Builder();
        }

        public Builder() {
            this.backGround = new BackGround();
        }

        public Builder left(ConfiguredChar configuredChar) {
            backGround.left = configuredChar;
            return this;
        }

        public Builder right(ConfiguredChar configuredChar) {
            backGround.right = configuredChar;
            return this;
        }

        public Builder offset_1(ConfiguredChar configuredChar) {
            backGround.offset_1 = configuredChar;
            return this;
        }

        public Builder offset_2(ConfiguredChar configuredChar) {
            backGround.offset_2 = configuredChar;
            return this;
        }

        public Builder offset_4(ConfiguredChar configuredChar) {
            backGround.offset_4 = configuredChar;
            return this;
        }

        public Builder offset_8(ConfiguredChar configuredChar) {
            backGround.offset_8 = configuredChar;
            return this;
        }

        public Builder offset_16(ConfiguredChar configuredChar) {
            backGround.offset_16 = configuredChar;
            return this;
        }

        public Builder offset_32(ConfiguredChar configuredChar) {
            backGround.offset_32 = configuredChar;
            return this;
        }

        public Builder offset_64(ConfiguredChar configuredChar) {
            backGround.offset_64 = configuredChar;
            return this;
        }

        public Builder offset_128(ConfiguredChar configuredChar) {
            backGround.offset_128 = configuredChar;
            return this;
        }

        public Builder leftMargin(int margin) {
            backGround.leftMargin = margin;
            return this;
        }

        public Builder rightMargin(int margin) {
            backGround.rightMargin = margin;
            return this;
        }

        public BackGround build() {
            return backGround;
        }
    }
}
