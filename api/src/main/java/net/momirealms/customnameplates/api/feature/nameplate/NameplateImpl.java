/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customnameplates.api.feature.nameplate;

import net.momirealms.customnameplates.api.feature.ConfiguredCharacter;
import net.momirealms.customnameplates.api.feature.OffsetFont;

import java.util.Objects;

/**
 * Implementation of Nameplate
 */
public class NameplateImpl implements Nameplate {
    private final String id;
    private final String displayName;
    private final ConfiguredCharacter left;
    private final ConfiguredCharacter middle;
    private final ConfiguredCharacter right;
    private final int minWidth;

    private NameplateImpl(String id, String displayName, int minWidth, ConfiguredCharacter left, ConfiguredCharacter middle, ConfiguredCharacter right) {
        this.id = id;
        this.displayName = displayName;
        this.minWidth = minWidth;
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public ConfiguredCharacter left() {
        return left;
    }

    @Override
    public ConfiguredCharacter middle() {
        return middle;
    }

    @Override
    public ConfiguredCharacter right() {
        return right;
    }

    @Override
    public int minWidth() {
        return minWidth;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        NameplateImpl nameplate = (NameplateImpl) object;
        return Objects.equals(id, nameplate.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public String createImagePrefix(float advance, float leftMargin, float rightMargin) {
        if (advance <= 0) return "";
        advance = Math.max(minWidth, advance);
        StringBuilder sb = new StringBuilder();
        sb.append(left.character());
        sb.append(OffsetFont.NEG_1.character());
        int mid_amount = (int) Math.ceil((advance + leftMargin + rightMargin) / (middle.advance() - 1));
        float exceed = mid_amount * (middle.advance() - 1) - advance - leftMargin - rightMargin;
        for (int i = 0; i < mid_amount; i++) {
            sb.append(middle.character()).append(OffsetFont.NEG_1.character());
        }
        sb.append(right.character());
        float delta = (right.advance() + rightMargin + advance + (float) Math.floor(exceed / 2));
        if (delta != 0) {
            sb.append(OffsetFont.shortestNegChars(delta));
        }
        return sb.toString();
    }

    @Override
    public String createImageSuffix(float advance, float leftMargin, float rightMargin) {
        if (advance <= 0) return "";
        advance = Math.max(minWidth, advance);
        int mid_amount = (int) Math.ceil((advance + leftMargin + rightMargin) / (middle.advance() - 1));
        float exceed = mid_amount * (middle.advance() - 1) - advance - leftMargin - rightMargin;
        return OffsetFont.shortestPosChars((float) Math.ceil(exceed / 2) + rightMargin + right.advance());
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public String createImage(float advance, float leftMargin, float rightMargin) {
        if (advance <= 0) return "";
        advance = Math.max(minWidth, advance);
        StringBuilder sb = new StringBuilder();
        sb.append(left.character());
        sb.append(OffsetFont.NEG_1.character());
        int mid_amount = (int) Math.ceil((advance + leftMargin + rightMargin) / (middle.advance() - 1));
        if (mid_amount > 512) return "";
        for (int i = 0; i < mid_amount; i++) {
            sb.append(middle.character()).append(OffsetFont.NEG_1.character());
        }
        sb.append(right.character());
        float delta = (right.advance() + rightMargin - (left.advance() - 1) - leftMargin);
        if (delta != 0) {
            sb.append(OffsetFont.createOffsets(-delta));
        }
        return sb.toString();
    }

    /**
     * The builder implementation
     */
    public static class BuilderImpl implements Builder {
        private String id;
        private String displayName;
        private ConfiguredCharacter left;
        private ConfiguredCharacter middle;
        private ConfiguredCharacter right;
        private int minWidth;

        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        @Override
        public Builder minWidth(int minWidth) {
            this.minWidth = minWidth;
            return this;
        }

        @Override
        public Builder left(ConfiguredCharacter left) {
            this.left = left;
            return this;
        }

        @Override
        public Builder middle(ConfiguredCharacter middle) {
            this.middle = middle;
            return this;
        }

        @Override
        public Builder right(ConfiguredCharacter right) {
            this.right = right;
            return this;
        }

        @Override
        public Nameplate build() {
            return new NameplateImpl(id, displayName, minWidth, left, middle, right);
        }
    }
}
