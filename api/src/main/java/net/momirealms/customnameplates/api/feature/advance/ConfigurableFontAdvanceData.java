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

package net.momirealms.customnameplates.api.feature.advance;

import java.util.List;

/**
 * Represents configurable font advance data, including default advance values and per-character advances.
 */
public interface ConfigurableFontAdvanceData {

    /**
     * Returns the default advance (width) used when no specific value is defined for a character.
     *
     * @return the default advance width
     */
    float defaultAdvance();

    /**
     * Retrieves the advance (width) for a specific character code point.
     *
     * @param codePoint the code point of the character
     * @return the advance value for the character, or the default advance if not defined
     */
    float getAdvance(int codePoint);

    /**
     * Returns the unique identifier for this configurable font advance data.
     *
     * @return the font advance data ID
     */
    String id();

    /**
     * Creates a new builder for constructing ConfigurableFontAdvanceData.
     *
     * @return a new builder instance
     */
    static Builder builder() {
        return new ConfigurableFontAdvanceDataImpl.BuilderImpl();
    }

    /**
     * Builder for creating ConfigurableFontAdvanceData.
     */
    interface Builder {

        /**
         * Sets the unique ID for the configurable font advance data.
         *
         * @param id the ID of the font advance data
         * @return the builder instance
         */
        Builder id(String id);

        /**
         * Sets the default advance (width) for characters that do not have specific advances.
         *
         * @param width the default advance width
         * @return the builder instance
         */
        Builder defaultAdvance(float width);

        /**
         * Sets the advance (width) for a specific character code point.
         *
         * @param codePoint the code point of the character
         * @param width the advance width for the character
         * @return the builder instance
         */
        Builder advance(int codePoint, float width);

        /**
         * Sets the parent fonts used by this configurable font advance data.
         *
         * @param font a list of parent fonts
         * @return the builder instance
         */
        Builder parentFont(List<CharacterFontAdvanceData> font);

        /**
         * Builds and returns the ConfigurableFontAdvanceData instance.
         *
         * @return the constructed ConfigurableFontAdvanceData
         */
        ConfigurableFontAdvanceData build();
    }
}
