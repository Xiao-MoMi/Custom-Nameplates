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

package net.momirealms.customnameplates.api.feature.bubble;

import net.momirealms.customnameplates.api.feature.ConfiguredCharacter;
import net.momirealms.customnameplates.api.feature.OffsetFont;

import java.util.Objects;

/**
 * Implementation of the Bubble interface
 */
public class BubbleImpl implements Bubble {
    private final String id;
    private final ConfiguredCharacter left;
    private final ConfiguredCharacter right;
    private final ConfiguredCharacter middle;
    private final ConfiguredCharacter tail;

    /**
     * Constructs a new BubbleImpl.
     *
     * @param id the unique ID for the bubble
     * @param left the character representing the left part of the bubble
     * @param right the character representing the right part of the bubble
     * @param middle the character representing the middle part of the bubble
     * @param tail the character representing the tail part of the bubble
     */
    public BubbleImpl(String id, ConfiguredCharacter left, ConfiguredCharacter right, ConfiguredCharacter middle, ConfiguredCharacter tail) {
        this.id = id;
        this.left = left;
        this.right = right;
        this.middle = middle;
        this.tail = tail;
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
    public ConfiguredCharacter right() {
        return right;
    }

    @Override
    public ConfiguredCharacter middle() {
        return middle;
    }

    @Override
    public ConfiguredCharacter tail() {
        return tail;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        BubbleImpl bubble = (BubbleImpl) object;
        return Objects.equals(id, bubble.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public String createImagePrefix(float advance, float leftMargin, float rightMargin) {
        if (advance <= 0) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(left.character());
        sb.append(OffsetFont.NEG_1.character());
        int mid_amount = (int) Math.ceil((advance + leftMargin + rightMargin - (tail.advance() - 1)) / (middle.advance() - 1));
        float actualMiddleWidth = 0;
        if (mid_amount > 512) return "";
        if (mid_amount <= 0) {
            sb.append(tail.character()).append(OffsetFont.NEG_1.character());
            actualMiddleWidth = tail.advance() - 1;
        } else {
            actualMiddleWidth += (middle.advance() - 1) * mid_amount;
            actualMiddleWidth += (tail.advance() - 1);
            int tailPos = mid_amount / 2;
            for (int i = 0; i < mid_amount; i++) {
                sb.append(middle.character()).append(OffsetFont.NEG_1.character());
                if (i == tailPos) {
                    sb.append(tail.character()).append(OffsetFont.NEG_1.character());
                }
            }
        }
        float exceed = actualMiddleWidth - advance;
        sb.append(right.character());
        float delta = (right.advance() + rightMargin + advance + exceed / 2);
        if (delta != 0) {
            sb.append(OffsetFont.createOffsets(-delta));
        }
        return sb.toString();
    }

    @Override
    public String createImageSuffix(float advance, float leftMargin, float rightMargin) {
        if (advance <= 0) return "";
        int mid_amount = (int) Math.ceil((advance + leftMargin + rightMargin - (tail.advance() - 1)) / (middle.advance() - 1));
        float actualMiddleWidth = 0;
        actualMiddleWidth += (tail.advance() - 1);
        actualMiddleWidth += (middle.advance() - 1) * mid_amount;
        float exceed = actualMiddleWidth - advance;
        float delta = (right.advance() + rightMargin + exceed / 2);
        return OffsetFont.shortestPosChars(delta);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public String createImage(float advance, float leftMargin, float rightMargin) {
        if (advance <= 0) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(left.character());
        sb.append(OffsetFont.NEG_1.character());
        int mid_amount = (int) Math.ceil((advance + leftMargin + rightMargin - (tail.advance() - 1)) / (middle.advance() - 1));
        if (mid_amount > 512) return "";
        if (mid_amount <= 0) {
            sb.append(tail.character()).append(OffsetFont.NEG_1.character());
        } else {
            int tailPos = mid_amount / 2;
            for (int i = 0; i < mid_amount; i++) {
                sb.append(middle.character()).append(OffsetFont.NEG_1.character());
                if (i == tailPos) {
                    sb.append(tail.character()).append(OffsetFont.NEG_1.character());
                }
            }
        }
        sb.append(right.character());
        float delta = (right.advance() + rightMargin - (left.advance() - 1) - leftMargin);
        if (delta != 0) {
            sb.append(OffsetFont.createOffsets(-delta));
        }
        return sb.toString();
    }

    /**
     * Builder implementation for creating BubbleImpl instances.
     */
    public static class BuilderImpl implements Builder {
        private String id;
        private ConfiguredCharacter left;
        private ConfiguredCharacter right;
        private ConfiguredCharacter middle;
        private ConfiguredCharacter tail;

        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public Builder left(ConfiguredCharacter left) {
            this.left = left;
            return this;
        }

        @Override
        public Builder right(ConfiguredCharacter right) {
            this.right = right;
            return this;
        }

        @Override
        public Builder middle(ConfiguredCharacter middle) {
            this.middle = middle;
            return this;
        }

        @Override
        public Builder tail(ConfiguredCharacter tail) {
            this.tail = tail;
            return this;
        }

        @Override
        public Bubble build() {
            return new BubbleImpl(id, left, right, middle, tail);
        }
    }
}
