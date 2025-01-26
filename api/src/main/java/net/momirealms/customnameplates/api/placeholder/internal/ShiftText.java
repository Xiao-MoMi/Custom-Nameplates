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

package net.momirealms.customnameplates.api.placeholder.internal;

import net.momirealms.customnameplates.api.feature.PreParsedDynamicText;
import org.jetbrains.annotations.ApiStatus;

/**
 * Shift Text
 */
@ApiStatus.Internal
public class ShiftText {

    /**
     * The font associated with the text.
     */
    private final String font;

    /**
     * The pre-parsed dynamic text, allowing for the use of placeholders and dynamic content.
     */
    private final PreParsedDynamicText text;

    /**
     * Constructs a ShiftText instance with the specified font and text.
     * The text is parsed dynamically for placeholders and other content.
     *
     * @param font the font to be associated with the text
     * @param text the dynamic text that may contain placeholders to be parsed
     */
    public ShiftText(String font, String text) {
        this.font = font;
        this.text = new PreParsedDynamicText(text);
    }

    /**
     * Retrieves the font associated with this ShiftText.
     *
     * @return the font as a String
     */
    public String getFont() {
        return font;
    }

    /**
     * Retrieves the pre-parsed dynamic text associated with this ShiftText.
     *
     * @return the PreParsedDynamicText instance
     */
    public PreParsedDynamicText getText() {
        return text;
    }
}
