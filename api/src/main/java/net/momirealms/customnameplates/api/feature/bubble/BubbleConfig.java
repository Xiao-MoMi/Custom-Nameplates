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

/**
 * Bubble configuration
 */
public interface BubbleConfig {

    /**
     * Returns the unique ID of the Bubble configuration.
     *
     * @return the configuration ID
     */
    String id();

    /**
     * Returns the background color of the Bubble text.
     *
     * @return the background color (ARGB) as an integer
     */
    int backgroundColor();

    /**
     * Returns the line width for the Bubble text.
     *
     * @return the line width
     */
    int lineWidth();

    /**
     * Returns the maximum number of lines for the Bubble text.
     *
     * @return the maximum number of lines
     */
    int maxLines();

    /**
     * Returns the bubbles used in this configuration.
     *
     * @return an array of Bubble instances
     */
    Bubble[] bubbles();

    /**
     * Returns the text prefix to be applied before the Bubble content.
     *
     * @return the text prefix
     */
    PreParsedDynamicText textPrefix();

    /**
     * Returns the text suffix to be applied after the Bubble content.
     *
     * @return the text suffix
     */
    PreParsedDynamicText textSuffix();

    /**
     * Returns the display name of the Bubble.
     *
     * @return the display name
     */
    String displayName();

    /**
     * Returns the scale vector for the Bubble.
     *
     * @return the scale as a Vector3
     */
    Vector3 scale();

    /**
     * Creates a new builder for constructing a BubbleConfig.
     *
     * @return a new builder instance
     */
    static Builder builder() {
        return new BubbleConfigImpl.BuilderImpl();
    }

    /**
     * Builder for constructing BubbleConfig instances.
     */
    interface Builder {

        /**
         * Sets the unique ID for the BubbleConfig.
         *
         * @param id the configuration ID
         * @return the builder instance
         */
        Builder id(String id);

        /**
         * Sets the display name for the BubbleConfig.
         *
         * @param displayName the display name
         * @return the builder instance
         */
        Builder displayName(String displayName);

        /**
         * Sets the background color for the BubbleConfig.
         *
         * @param backgroundColor the background color
         * @return the builder instance
         */
        Builder backgroundColor(int backgroundColor);

        /**
         * Sets the line width for the Bubble text.
         *
         * @param lineWidth the line width
         * @return the builder instance
         */
        Builder lineWidth(int lineWidth);

        /**
         * Sets the maximum number of lines for the Bubble text.
         *
         * @param maxLines the maximum number of lines
         * @return the builder instance
         */
        Builder maxLines(int maxLines);

        /**
         * Sets the bubbles used in this configuration.
         *
         * @param bubbles an array of Bubble instances
         * @return the builder instance
         */
        Builder bubbles(Bubble[] bubbles);

        /**
         * Sets the text prefix for the BubbleConfig.
         *
         * @param textPrefix the text prefix
         * @return the builder instance
         */
        Builder textPrefix(String textPrefix);

        /**
         * Sets the text suffix for the BubbleConfig.
         *
         * @param textSuffix the text suffix
         * @return the builder instance
         */
        Builder textSuffix(String textSuffix);

        /**
         * Sets the scale for the BubbleConfig.
         *
         * @param scale the scale as a Vector3
         * @return the builder instance
         */
        Builder scale(Vector3 scale);

        /**
         * Builds and returns the configured BubbleConfig instance.
         *
         * @return the constructed BubbleConfig
         */
        BubbleConfig build();
    }
}
