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

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Interface representing character font advance data.
 */
public interface CharacterFontAdvanceData {

    /**
     * Returns the size of the character font advance data, typically the number of characters.
     *
     * @return the size of the data
     */
    int size();

    /**
     * Retrieves the advance (width) for a specific character code point.
     *
     * @param codePoint the code point of the character
     * @return the advance value for the character, or null if not found
     */
    Float getAdvance(int codePoint);

    /**
     * Returns the map containing the character code points and their corresponding advance values.
     *
     * @return a map of character code points and their advances
     */
    Map<Integer, Float> data();

    /**
     * Returns the unique identifier for this font advance data.
     *
     * @return the font advance data ID
     */
    String id();

    /**
     * Generates a list of font provider configurations based on the provided properties.
     *
     * @param properties a map of properties for font provider configuration
     * @return a list of JSON objects representing the font providers
     */
    List<JsonObject> fontProvider(Map<String, Object> properties);

    /**
     * Creates a new builder for constructing CharacterFontAdvanceData.
     *
     * @return a new builder instance
     */
    static Builder builder() {
        return new CharacterFontAdvanceDataImpl.BuilderImpl();
    }

    /**
     * Builder for creating CharacterFontAdvanceData.
     */
    interface Builder {

        /**
         * Sets the advance data for the characters.
         *
         * @param data a map of character code points and their advance values
         * @return the builder instance
         */
        Builder advance(Map<Integer, Float> data);

        /**
         * Sets the unique ID for the font advance data.
         *
         * @param id the ID of the font advance data
         * @return the builder instance
         */
        Builder id(String id);

        /**
         * Sets a function to provide the font provider configuration.
         *
         * @param function a function that takes properties and returns a list of JSON objects representing font providers
         * @return the builder instance
         */
        Builder fontProviderFunction(Function<Map<String, Object>, List<JsonObject>> function);

        /**
         * Builds and returns the CharacterFontAdvanceData instance.
         *
         * @return the constructed CharacterFontAdvanceData
         */
        CharacterFontAdvanceData build();
    }
}
