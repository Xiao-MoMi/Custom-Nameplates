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

import net.kyori.adventure.key.Key;
import net.momirealms.customnameplates.common.plugin.feature.Reloadable;
import net.momirealms.customnameplates.common.util.Tuple;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Interface that defines methods for managing and calculating text advance data,
 */
public interface AdvanceManager extends Reloadable {

    /**
     * Retrieves custom font advance data by its ID.
     *
     * @param id the ID of the custom font
     * @return the custom font advance data
     */
    @Nullable
    ConfigurableFontAdvanceData customFontDataById(String id);

    /**
     * Retrieves character font advance data by its ID.
     *
     * @param id the ID of the character font
     * @return the character font advance data
     */
    @Nullable
    CharacterFontAdvanceData templateFontDataById(String id);

    /**
     * Calculates the total advance (width) for a line of text.
     *
     * @param text the text to calculate the advance for
     * @return the total line advance
     */
    float getLineAdvance(String text);

    /**
     * Calculates the advance (width) for a series of characters with a specific font and style.
     *
     * @param chars the array of characters
     * @param font the font key
     * @param bold whether the text is bold
     * @return the total character advance
     */
    float getCharAdvance(char[] chars, Key font, boolean bold);

    /**
     * Calculates the number of lines needed to fit the text within a specified width.
     *
     * @param text the text to calculate the number of lines for
     * @param width the maximum width of each line
     * @return the number of lines required
     */
    int getLines(String text, int width);

    /**
     * Converts a MiniMessage-formatted string into an iterable list of tuples.
     * Each tuple contains text, its corresponding font key, and whether it is bold.
     *
     * @param text the MiniMessage-formatted string
     * @return a list of tuples representing the iterable text parts
     */
    @ApiStatus.Internal
    List<Tuple<String, Key, Boolean>> miniMessageToIterable(String text);
}
