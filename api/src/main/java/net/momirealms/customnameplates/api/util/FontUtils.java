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

package net.momirealms.customnameplates.api.util;

import net.momirealms.customnameplates.api.CustomNameplatesPlugin;

public class FontUtils {

    private static String namespace;
    private static String font;

    /**
     * Surround the text with ascent font
     *
     * @param text text
     * @param ascent ascent
     * @return ascent font text
     */
    public static String surroundAscentFont(String text, int ascent) {
        return getAscentFontTag(ascent) + text + getFontTagCloser();
    }

    /**
     * Surround the text with custom nameplates font
     *
     * @param text text
     * @return font text
     */
    public static String surroundNameplateFont(String text) {
        return getMiniMessageFontTag() + text + getFontTagCloser();
    }

    private static String getAscentFontTag(int ascent) {
        return "<font:" + namespace + ":ascent_" + ascent + ">";
    }

    private static String getMiniMessageFontTag() {
        return "<font:" + namespace + ":" + font + ">";
    }

    private static String getFontTagCloser() {
        return "</font>";
    }

    /**
     * Get a text's width
     *
     * @param text text
     * @return width
     */
    public static int getTextWidth(String text) {
        return CustomNameplatesPlugin.get().getWidthManager().getTextWidth(text);
    }

    /**
     * Set namespace and font
     *
     * @param n namespace
     * @param f font
     */
    public static void setNameSpaceAndFont(String n, String f) {
        namespace = n;
        font = f;
    }
}
