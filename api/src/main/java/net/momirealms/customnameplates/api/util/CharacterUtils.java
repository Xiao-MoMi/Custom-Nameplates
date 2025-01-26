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

package net.momirealms.customnameplates.api.util;

import java.util.ArrayList;

/**
 * Utility class for handling character and Unicode conversions.
 * This class provides methods to convert between Unicode escape sequences, character arrays,
 * and code points, as well as converting characters to their Unicode representations.
 */
public class CharacterUtils {

    private CharacterUtils() {}

    /**
     * Converts a Unicode string (e.g., "\\u0041\\u0042") into an array of characters.
     *
     * @param unicodeString a string containing Unicode escape sequences (e.g., "\\u0041\\u0042")
     * @return a character array corresponding to the Unicode string
     */
    public static char[] unicodeToChars(String unicodeString) {
        String processedString = unicodeString.replace("\\u", "");
        int length = processedString.length() / 4;
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            int codePoint = Integer.parseInt(processedString.substring(i * 4, i * 4 + 4), 16);
            if (Character.isSupplementaryCodePoint(codePoint)) {
                chars[i] = Character.highSurrogate(codePoint);
                chars[++i] = Character.lowSurrogate(codePoint);
            } else {
                chars[i] = (char) codePoint;
            }
        }
        return chars;
    }

    /**
     * Converts a character array to a single Unicode code point. If the array contains two characters
     * (a high surrogate and a low surrogate), it is combined into a single supplementary code point.
     *
     * @param chars the character array to convert
     * @return the Unicode code point corresponding to the character(s)
     * @throws IllegalArgumentException if the character array contains more than 2 characters
     */
    public static int toCodePoint(char[] chars) {
        if (chars.length == 1) {
            return chars[0];
        } else if (chars.length == 2) {
            return Character.toCodePoint(chars[0], chars[1]);
        } else {
            throw new IllegalArgumentException("The given chars array contains more than 2 characters");
        }
    }

    /**
     * Converts a character array to an array of Unicode code points. If the array contains surrogate pairs,
     * they are combined into a single supplementary code point.
     *
     * @param chars the character array to convert
     * @return an array of Unicode code points corresponding to the characters
     * @throws IllegalArgumentException if there is an illegal surrogate pair
     */
    public static int[] toCodePoints(char[] chars) {
        ArrayList<Integer> codePoints = new ArrayList<>();
        for (int i = 0; i < chars.length; i++) {
            int codePoint;
            char c1 = chars[i];
            if (Character.isHighSurrogate(c1)) {
                if (i + 1 < chars.length && Character.isLowSurrogate(chars[i + 1])) {
                    char c2 = chars[++i];
                    codePoint = Character.toCodePoint(c1, c2);
                } else {
                    throw new IllegalArgumentException("Illegal surrogate pair: High surrogate without matching low surrogate at index " + i);
                }
            } else {
                codePoint = c1;
            }
            codePoints.add(codePoint);
        }
        return codePoints.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Converts a single character to its Unicode escape sequence (e.g., '\\u0041' for 'A').
     *
     * @param c the character to convert
     * @return a string representing the Unicode escape sequence for the character
     */
    public static String char2Unicode(char c) {
        StringBuilder stringBuilder_1 = new StringBuilder("\\u");
        StringBuilder stringBuilder_2 = new StringBuilder(Integer.toHexString(c));
        stringBuilder_2.reverse();
        for (int n = 4 - stringBuilder_2.length(), i = 0; i < n; i++) stringBuilder_2.append('0');
        for (int j = 0; j < 4; j++) stringBuilder_1.append(stringBuilder_2.charAt(3 - j));
        return stringBuilder_1.toString();
    }

    /**
     * Converts an array of characters to their Unicode escape sequences.
     *
     * @param c the character array to convert
     * @return a string representing the Unicode escape sequences for all characters in the array
     */
    public static String char2Unicode(char[] c) {
        StringBuilder builder = new StringBuilder();
        for (char value : c) {
            builder.append(char2Unicode(value));
        }
        return builder.toString();
    }
}
