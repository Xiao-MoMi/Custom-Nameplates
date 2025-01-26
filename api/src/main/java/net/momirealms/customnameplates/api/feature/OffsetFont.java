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

package net.momirealms.customnameplates.api.feature;

/**
 * Enum representing different font offsets used for handling character spacing and height.
 * The offsets are categorized into positive and negative values, each corresponding to
 * a specific advance (horizontal movement) and height (vertical movement).
 */
public enum OffsetFont {
    /**
     * Represents a font with a character value of '\uf800', horizontal advance of -1, and vertical height of -3.
     */
    NEG_1('\uf800', -1, -3),

    /**
     * Represents a font with a character value of '\uf801', horizontal advance of -2, and vertical height of -4.
     */
    NEG_2('\uf801', -2, -4),

    /**
     * Represents a font with a character value of '\uf802', horizontal advance of -3, and vertical height of -5.
     */
    NEG_3('\uf802', -3, -5),

    /**
     * Represents a font with a character value of '\uf803', horizontal advance of -4, and vertical height of -6.
     */
    NEG_4('\uf803', -4, -6),

    /**
     * Represents a font with a character value of '\uf804', horizontal advance of -5, and vertical height of -7.
     */
    NEG_5('\uf804', -5, -7),

    /**
     * Represents a font with a character value of '\uf805', horizontal advance of -6, and vertical height of -8.
     */
    NEG_6('\uf805', -6, -8),

    /**
     * Represents a font with a character value of '\uf806', horizontal advance of -7, and vertical height of -9.
     */
    NEG_7('\uf806', -7, -9),

    /**
     * Represents a font with a character value of '\uf807', horizontal advance of -8, and vertical height of -10.
     */
    NEG_8('\uf807', -8, -10),

    /**
     * Represents a font with a character value of '\uf808', horizontal advance of -9, and vertical height of -11.
     */
    NEG_9('\uf808', -9, -11),

    /**
     * Represents a font with a character value of '\uf809', horizontal advance of -10, and vertical height of -12.
     */
    NEG_10('\uf809', -10, -12),

    /**
     * Represents a font with a character value of '\uf80a', horizontal advance of -11, and vertical height of -13.
     */
    NEG_11('\uf80a', -11, -13),

    /**
     * Represents a font with a character value of '\uf80b', horizontal advance of -12, and vertical height of -14.
     */
    NEG_12('\uf80b', -12, -14),

    /**
     * Represents a font with a character value of '\uf80c', horizontal advance of -13, and vertical height of -15.
     */
    NEG_13('\uf80c', -13, -15),

    /**
     * Represents a font with a character value of '\uf80d', horizontal advance of -14, and vertical height of -16.
     */
    NEG_14('\uf80d', -14, -16),

    /**
     * Represents a font with a character value of '\uf80e', horizontal advance of -15, and vertical height of -17.
     */
    NEG_15('\uf80e', -15, -17),

    /**
     * Represents a font with a character value of '\uf80f', horizontal advance of -16, and vertical height of -18.
     */
    NEG_16('\uf80f', -16, -18),

    /**
     * Represents a font with a character value of '\uf810', horizontal advance of -24, and vertical height of -26.
     */
    NEG_24('\uf810', -24, -26),

    /**
     * Represents a font with a character value of '\uf811', horizontal advance of -32, and vertical height of -34.
     */
    NEG_32('\uf811', -32, -34),

    /**
     * Represents a font with a character value of '\uf812', horizontal advance of -48, and vertical height of -50.
     */
    NEG_48('\uf812', -48, -50),

    /**
     * Represents a font with a character value of '\uf813', horizontal advance of -64, and vertical height of -66.
     */
    NEG_64('\uf813', -64, -66),

    /**
     * Represents a font with a character value of '\uf814', horizontal advance of -128, and vertical height of -130.
     */
    NEG_128('\uf814', -128, -130),

    /**
     * Represents a font with a character value of '\uf815', horizontal advance of -256, and vertical height of -258.
     */
    NEG_256('\uf815', -256, -258),

    /**
     * Represents a font with a character value of '\uf830', horizontal advance of 1, and vertical height of -1.
     */
    POS_1('\uf830', 1, -1),

    /**
     * Represents a font with a character value of '\uf831', horizontal advance of 2, and vertical height of 1.
     */
    POS_2('\uf831', 2, 1),

    /**
     * Represents a font with a character value of '\uf832', horizontal advance of 3, and vertical height of 2.
     */
    POS_3('\uf832', 3, 2),

    /**
     * Represents a font with a character value of '\uf833', horizontal advance of 4, and vertical height of 3.
     */
    POS_4('\uf833', 4, 3),

    /**
     * Represents a font with a character value of '\uf834', horizontal advance of 5, and vertical height of 4.
     */
    POS_5('\uf834', 5, 4),

    /**
     * Represents a font with a character value of '\uf835', horizontal advance of 6, and vertical height of 5.
     */
    POS_6('\uf835', 6, 5),

    /**
     * Represents a font with a character value of '\uf836', horizontal advance of 7, and vertical height of 6.
     */
    POS_7('\uf836', 7, 6),

    /**
     * Represents a font with a character value of '\uf837', horizontal advance of 8, and vertical height of 7.
     */
    POS_8('\uf837', 8, 7),

    /**
     * Represents a font with a character value of '\uf838', horizontal advance of 9, and vertical height of 8.
     */
    POS_9('\uf838', 9, 8),

    /**
     * Represents a font with a character value of '\uf839', horizontal advance of 10, and vertical height of 9.
     */
    POS_10('\uf839', 10, 9),

    /**
     * Represents a font with a character value of '\uf83a', horizontal advance of 11, and vertical height of 10.
     */
    POS_11('\uf83a', 11, 10),

    /**
     * Represents a font with a character value of '\uf83b', horizontal advance of 12, and vertical height of 11.
     */
    POS_12('\uf83b', 12, 11),

    /**
     * Represents a font with a character value of '\uf83c', horizontal advance of 13, and vertical height of 12.
     */
    POS_13('\uf83c', 13, 12),

    /**
     * Represents a font with a character value of '\uf83d', horizontal advance of 14, and vertical height of 13.
     */
    POS_14('\uf83d', 14, 13),

    /**
     * Represents a font with a character value of '\uf83e', horizontal advance of 15, and vertical height of 14.
     */
    POS_15('\uf83e', 15, 14),

    /**
     * Represents a font with a character value of '\uf83f', horizontal advance of 16, and vertical height of 15.
     */
    POS_16('\uf83f', 16, 15),

    /**
     * Represents a font with a character value of '\uf840', horizontal advance of 24, and vertical height of 23.
     */
    POS_24('\uf840', 24, 23),

    /**
     * Represents a font with a character value of '\uf841', horizontal advance of 32, and vertical height of 31.
     */
    POS_32('\uf841', 32, 31),

    /**
     * Represents a font with a character value of '\uf842', horizontal advance of 48, and vertical height of 47.
     */
    POS_48('\uf842', 48, 47),

    /**
     * Represents a font with a character value of '\uf843', horizontal advance of 64, and vertical height of 63.
     */
    POS_64('\uf843', 64, 63),

    /**
     * Represents a font with a character value of '\uf844', horizontal advance of 128, and vertical height of 127.
     */
    POS_128('\uf844', 128, 127),

    /**
     * Represents a font with a character value of '\uf845', horizontal advance of 256, and vertical height of 255.
     */
    POS_256('\uf845', 256, 255);

    private static final OffsetFont[] negFastLookup = new OffsetFont[17];
    private static final OffsetFont[] posFastLookup = new OffsetFont[17];

    static {
        for (OffsetFont f : OffsetFont.values()) {
            if (f.advance > 0 && f.advance <= 16) {
                posFastLookup[f.advance] = f;
            } else if (f.advance < 0 && f.advance >= -16) {
                negFastLookup[-f.advance] = f;
            }
        }
    }

    private final char character;
    private final int advance;
    private final int height;

    /**
     * Constructor to initialize the OffsetFont with a specific character, advance, and height.
     *
     * @param character the character associated with the offset
     * @param advance   the horizontal movement (positive or negative)
     * @param height    the vertical movement (positive or negative)
     */
    OffsetFont(char character, int advance, int height) {
        this.character = character;
        this.advance = advance;
        this.height = height;
    }

    /**
     * Gets the character associated with this offset font.
     *
     * @return the character of this offset font
     */
    public char character() {
        return this.character;
    }

    /**
     * Gets the advance value for this offset font.
     *
     * @return the advance (horizontal movement) for this offset font
     */
    public float advance() {
        return this.advance;
    }

    /**
     * Gets the height value for this offset font.
     *
     * @return the height (vertical movement) for this offset font
     */
    public int height() {
        return this.height;
    }

    /**
     * Creates a string of offset characters based on the given offset value.
     * The result depends on whether the offset is positive or negative.
     *
     * @param offset the offset value
     * @return a string of offset characters
     */
    public static String createOffsets(float offset) {
        if (offset >= 0) {
            return shortestPosChars(offset);
        } else {
            return shortestNegChars(-offset);
        }
    }

    /**
     * Generates a string of the shortest possible negative offset characters
     * that sum up to the given advance value.
     *
     * @param advance the negative advance value
     * @return the shortest string of negative offset characters
     */
    public static String shortestNegChars(float advance) {
        int n = (int) Math.ceil(advance);
        StringBuilder stringBuilder = new StringBuilder();
        while (n >= 256) {
            stringBuilder.append(OffsetFont.NEG_256.character());
            n -= 256;
        }
        if (n - 128 >= 0) {
            stringBuilder.append(OffsetFont.NEG_128.character());
            n -= 128;
        }
        if (n - 64 >= 0) {
            stringBuilder.append(OffsetFont.NEG_64.character());
            n -= 64;
        }
        if (n - 48 >= 0) {
            stringBuilder.append(OffsetFont.NEG_48.character());
            n -= 48;
        }
        if (n - 32 >= 0) {
            stringBuilder.append(OffsetFont.NEG_32.character());
            n -= 32;
        }
        if (n - 24 >= 0) {
            stringBuilder.append(OffsetFont.NEG_24.character());
            n -= 24;
        }
        if (n - 16 >= 0) {
            stringBuilder.append(OffsetFont.NEG_16.character());
            n -= 16;
        }
        if (n == 0) return stringBuilder.toString();
        stringBuilder.append(negFastLookup[n].character());
        return stringBuilder.toString();
    }

    /**
     * Generates a string of the shortest possible positive offset characters
     * that sum up to the given advance value.
     *
     * @param advance the positive advance value
     * @return the shortest string of positive offset characters
     */
    public static String shortestPosChars(float advance) {
        int n = (int) Math.ceil(advance);
        StringBuilder stringBuilder = new StringBuilder();
        while (n >= 256) {
            stringBuilder.append(OffsetFont.POS_256.character());
            n -= 256;
        }
        if (n - 128 >= 0) {
            stringBuilder.append(OffsetFont.POS_128.character());
            n -= 128;
        }
        if (n - 64 >= 0) {
            stringBuilder.append(OffsetFont.POS_64.character());
            n -= 64;
        }
        if (n - 48 >= 0) {
            stringBuilder.append(OffsetFont.POS_48.character());
            n -= 48;
        }
        if (n - 32 >= 0) {
            stringBuilder.append(OffsetFont.POS_32.character());
            n -= 32;
        }
        if (n - 24 >= 0) {
            stringBuilder.append(OffsetFont.POS_24.character());
            n -= 24;
        }
        if (n - 16 >= 0) {
            stringBuilder.append(OffsetFont.POS_16.character());
            n -= 16;
        }
        if (n == 0) return stringBuilder.toString();
        stringBuilder.append(posFastLookup[n].character());
        return stringBuilder.toString();
    }
}
