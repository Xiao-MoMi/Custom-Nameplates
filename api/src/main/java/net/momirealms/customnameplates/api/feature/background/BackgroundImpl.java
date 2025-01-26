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

import java.util.Objects;

/**
 * Implements the {@link Background} interface, representing the detailed configuration of a background.
 */
@SuppressWarnings("DuplicatedCode")
public record BackgroundImpl(String id, ConfiguredCharacter left, ConfiguredCharacter width_1,
                             ConfiguredCharacter width_2, ConfiguredCharacter width_4, ConfiguredCharacter width_8,
                             ConfiguredCharacter width_16, ConfiguredCharacter width_32, ConfiguredCharacter width_64,
                             ConfiguredCharacter width_128, ConfiguredCharacter right) implements Background {

    @Override
    public String createImagePrefix(float advance, float leftMargin, float rightMargin) {
        if (advance <= 0) return "";
        String offset1 = OffsetFont.shortestNegChars(advance + rightMargin + right.advance());
        advance = leftMargin + advance + rightMargin;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(left.character());
        while (advance >= 128) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_128.character());
            advance -= 128;
        }
        if (advance - 64 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_64.character());
            advance -= 64;
        }
        if (advance - 32 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_32.character());
            advance -= 32;
        }
        if (advance - 16 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_16.character());
            advance -= 16;
        }
        if (advance - 8 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_8.character());
            advance -= 8;
        }
        if (advance - 4 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_4.character());
            advance -= 4;
        }
        if (advance - 2 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_2.character());
            advance -= 2;
        }
        if (advance - 1 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_1.character());
        }
        stringBuilder.append(OffsetFont.NEG_1.character());
        stringBuilder.append(right.character());
        stringBuilder.append(offset1);
        return stringBuilder.toString();
    }

    @Override
    public String createImageSuffix(float advance, float leftMargin, float rightMargin) {
        if (advance <= 0) return "";
        return OffsetFont.shortestPosChars(rightMargin + right.advance());
    }

    @Override
    public String createImage(float advance, float leftMargin, float rightMargin) {
        if (advance <= 0) return "";
        advance = advance + leftMargin + rightMargin + 2;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(left.character());
        while (advance >= 128) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_128.character());
            advance -= 128;
        }
        if (advance - 64 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_64.character());
            advance -= 64;
        }
        if (advance - 32 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_32.character());
            advance -= 32;
        }
        if (advance - 16 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_16.character());
            advance -= 16;
        }
        if (advance - 8 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_8.character());
            advance -= 8;
        }
        if (advance - 4 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_4.character());
            advance -= 4;
        }
        if (advance - 2 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_2.character());
            advance -= 2;
        }
        if (advance - 1 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.character());
            stringBuilder.append(width_1.character());
        }
        stringBuilder.append(OffsetFont.NEG_1.character());
        stringBuilder.append(right.character());
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        BackgroundImpl that = (BackgroundImpl) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /**
     * The builder implementation
     */
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
