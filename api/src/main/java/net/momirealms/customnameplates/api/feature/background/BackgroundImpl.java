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

package net.momirealms.customnameplates.api.feature.background;

import net.momirealms.customnameplates.api.feature.ConfiguredCharacter;
import net.momirealms.customnameplates.api.feature.OffsetFont;

@SuppressWarnings("DuplicatedCode")
public class BackgroundImpl implements Background {

    private final String id;
    private final ConfiguredCharacter left;
    private final ConfiguredCharacter width_1;
    private final ConfiguredCharacter width_2;
    private final ConfiguredCharacter width_4;
    private final ConfiguredCharacter width_8;
    private final ConfiguredCharacter width_16;
    private final ConfiguredCharacter width_32;
    private final ConfiguredCharacter width_64;
    private final ConfiguredCharacter width_128;
    private final ConfiguredCharacter right;

    public BackgroundImpl(
            String id,
            ConfiguredCharacter left,
            ConfiguredCharacter width_1,
            ConfiguredCharacter width_2,
            ConfiguredCharacter width_4,
            ConfiguredCharacter width_8,
            ConfiguredCharacter width_16,
            ConfiguredCharacter width_32,
            ConfiguredCharacter width_64,
            ConfiguredCharacter width_128,
            ConfiguredCharacter right
    ) {
        this.id = id;
        this.left = left;
        this.width_1 = width_1;
        this.width_2 = width_2;
        this.width_4 = width_4;
        this.width_8 = width_8;
        this.width_16 = width_16;
        this.width_32 = width_32;
        this.width_64 = width_64;
        this.width_128 = width_128;
        this.right = right;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public ConfiguredCharacter left() {
        return left;
    }

    @Override
    public ConfiguredCharacter width_1() {
        return width_1;
    }

    @Override
    public ConfiguredCharacter width_2() {
        return width_2;
    }

    @Override
    public ConfiguredCharacter width_4() {
        return width_4;
    }

    @Override
    public ConfiguredCharacter width_8() {
        return width_8;
    }

    @Override
    public ConfiguredCharacter width_16() {
        return width_16;
    }

    @Override
    public ConfiguredCharacter width_32() {
        return width_32;
    }

    @Override
    public ConfiguredCharacter width_64() {
        return width_64;
    }

    @Override
    public ConfiguredCharacter width_128() {
        return width_128;
    }

    @Override
    public ConfiguredCharacter right() {
        return right;
    }

    @Override
    public String createBackgroundWithOffsets(float n, float leftMargin, float rightMargin) {
        String offset = OffsetFont.shortestNegChars(n + rightMargin + 2);
        n = n + leftMargin + rightMargin + 2;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(left.character());
        while (n >= 128) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_128.character());
            n -= 128;
        }
        if (n - 64 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_64.character());
            n -= 64;
        }
        if (n - 32 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_32.character());
            n -= 32;
        }
        if (n - 16 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_16.character());
            n -= 16;
        }
        if (n - 8 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_8.character());
            n -= 8;
        }
        if (n - 4 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_4.character());
            n -= 4;
        }
        if (n - 2 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_2.character());
            n -= 2;
        }
        if (n - 1 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_1.character());
        }
        stringBuilder.append(OffsetFont.NEG_1.character());
        stringBuilder.append(right.character());
        stringBuilder.append(offset);
        return stringBuilder.toString();
    }

    @Override
    public String createBackground(float n, float leftMargin, float rightMargin) {
        n = n + leftMargin + rightMargin + 2;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(left.character());
        while (n >= 128) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_128.character());
            n -= 128;
        }
        if (n - 64 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_64.character());
            n -= 64;
        }
        if (n - 32 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_32.character());
            n -= 32;
        }
        if (n - 16 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_16.character());
            n -= 16;
        }
        if (n - 8 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_8.character());
            n -= 8;
        }
        if (n - 4 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_4.character());
            n -= 4;
        }
        if (n - 2 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_2.character());
            n -= 2;
        }
        if (n - 1 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_1.character());
        }
        stringBuilder.append(OffsetFont.NEG_1.character());
        stringBuilder.append(right.character());
        return stringBuilder.toString();
    }

    public static class BuilderImpl implements Builder {

        private String id;
        private ConfiguredCharacter left;
        private ConfiguredCharacter width_1;
        private ConfiguredCharacter width_2;
        private ConfiguredCharacter width_4;
        private ConfiguredCharacter width_8;
        private ConfiguredCharacter width_16;
        private ConfiguredCharacter width_32;
        private ConfiguredCharacter width_64;
        private ConfiguredCharacter width_128;
        private ConfiguredCharacter right;

        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public Builder left(ConfiguredCharacter character) {
            this.left = character;
            return this;
        }

        @Override
        public Builder width_1(ConfiguredCharacter character) {
            this.width_1 = character;
            return this;
        }

        @Override
        public Builder width_2(ConfiguredCharacter character) {
            this.width_2 = character;
            return this;
        }

        @Override
        public Builder width_4(ConfiguredCharacter character) {
            this.width_4 = character;
            return this;
        }

        @Override
        public Builder width_8(ConfiguredCharacter character) {
            this.width_8 = character;
            return this;
        }

        @Override
        public Builder width_16(ConfiguredCharacter character) {
            this.width_16 = character;
            return this;
        }

        @Override
        public Builder width_32(ConfiguredCharacter character) {
            this.width_32 = character;
            return this;
        }

        @Override
        public Builder width_64(ConfiguredCharacter character) {
            this.width_64 = character;
            return this;
        }

        @Override
        public Builder width_128(ConfiguredCharacter character) {
            this.width_128 = character;
            return this;
        }

        @Override
        public Builder right(ConfiguredCharacter character) {
            this.right = character;
            return this;
        }

        @Override
        public Background build() {
            return new BackgroundImpl(id, left, width_1, width_2, width_4, width_8, width_16, width_32, width_64, width_128, right);
        }
    }
}
