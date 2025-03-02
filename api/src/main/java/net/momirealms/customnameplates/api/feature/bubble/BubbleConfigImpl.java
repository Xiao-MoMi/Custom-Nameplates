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

import net.momirealms.customnameplates.api.feature.PreParsedDynamicText;
import net.momirealms.customnameplates.api.util.Vector3;

import static java.util.Objects.requireNonNull;

/**
 * Implementation of the BubbleConfig interface
 */
public class BubbleConfigImpl implements BubbleConfig {
    private final String id;
    private final int backgroundColor;
    private final int lineWidth;
    private final int maxLines;
    private final Bubble[] bubbles;
    private final PreParsedDynamicText textPrefix;
    private final PreParsedDynamicText textSuffix;
    private final String displayName;
    private final Vector3 scale;

    /**
     * Constructs a new BubbleConfigImpl.
     *
     * @param id            the unique ID for the bubble configuration
     * @param displayName   the name to be displayed for the bubble configuration
     * @param backgroundColor the background color for the bubble (in integer format)
     * @param lineWidth     the line width for the bubble display
     * @param maxLines      the maximum number of lines that can be displayed in the bubble
     * @param bubbles       an array of Bubble objects representing the actual bubbles
     * @param textPrefix    a prefix that will be added before the text in the bubble
     * @param textSuffix    a suffix that will be added after the text in the bubble
     * @param scale         the scale of the bubble display (as a Vector3)
     */
    public BubbleConfigImpl(String id, String displayName, int backgroundColor, int lineWidth, int maxLines, Bubble[] bubbles, String textPrefix, String textSuffix, Vector3 scale) {
        this.backgroundColor = backgroundColor;
        this.lineWidth = lineWidth;
        this.maxLines = maxLines;
        this.id = id;
        this.bubbles = requireNonNull(bubbles);
        this.textPrefix = new PreParsedDynamicText(requireNonNull(textPrefix), true);
        this.textSuffix = new PreParsedDynamicText(requireNonNull(textSuffix), true);
        this.displayName = requireNonNull(displayName);
        this.scale = requireNonNull(scale);
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public int backgroundColor() {
        return backgroundColor;
    }

    @Override
    public int lineWidth() {
        return lineWidth;
    }

    @Override
    public int maxLines() {
        return maxLines;
    }

    @Override
    public Bubble[] bubbles() {
        return bubbles;
    }

    @Override
    public PreParsedDynamicText textPrefix() {
        return textPrefix;
    }

    @Override
    public PreParsedDynamicText textSuffix() {
        return textSuffix;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public Vector3 scale() {
        return scale;
    }

    /**
     * Builder implementation for creating BubbleConfigImpl instances.
     */
    public static class BuilderImpl implements Builder {
        private int backgroundColor;
        private int lineWidth;
        private int maxLines;
        private Bubble[] bubbles;
        private String textPrefix;
        private String textSuffix;
        private String id;
        private String displayName;
        private Vector3 scale;

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
        public Builder backgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        @Override
        public Builder lineWidth(int lineWidth) {
            this.lineWidth = lineWidth;
            return this;
        }

        @Override
        public Builder maxLines(int maxLines) {
            this.maxLines = maxLines;
            return this;
        }

        @Override
        public Builder bubbles(Bubble[] bubbles) {
            this.bubbles = bubbles;
            return this;
        }

        @Override
        public Builder textPrefix(String textPrefix) {
            this.textPrefix = textPrefix;
            return this;
        }

        @Override
        public Builder textSuffix(String textSuffix) {
            this.textSuffix = textSuffix;
            return this;
        }

        @Override
        public Builder scale(Vector3 scale) {
            this.scale = scale;
            return this;
        }

        @Override
        public BubbleConfig build() {
            return new BubbleConfigImpl(id, displayName, backgroundColor, lineWidth, maxLines, bubbles, textPrefix, textSuffix, scale);
        }
    }
}
