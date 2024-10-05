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

import java.util.HashMap;

public enum OffsetFont {

    NEG_1('\uf800', -1, -3),
    NEG_2('\uf801', -2, -4),
    NEG_3('\uf802', -3, -5),
    NEG_4('\uf803', -4, -6),
    NEG_5('\uf804', -5, -7),
    NEG_6('\uf805', -6, -8),
    NEG_7('\uf806', -7, -9),
    NEG_8('\uf807', -8, -10),
    NEG_9('\uf808', -9, -11),
    NEG_10('\uf809', -10, -12),
    NEG_11('\uf80a', -11, -13),
    NEG_12('\uf80b', -12, -14),
    NEG_13('\uf80c', -13, -15),
    NEG_14('\uf80d', -14, -16),
    NEG_15('\uf80e', -15, -17),
    NEG_16('\uf80f', -16, -18),
    NEG_24('\uf810', -24, -26),
    NEG_32('\uf811', -32, -34),
    NEG_48('\uf812', -48, -50),
    NEG_64('\uf813', -64, -66),
    NEG_128('\uf814', -128, -130),
    NEG_256('\uf815', -256, -258),
    POS_1('\uf830', 1, -1),
    POS_2('\uf831', 2, 1),
    POS_3('\uf832', 3, 2),
    POS_4('\uf833', 4, 3),
    POS_5('\uf834', 5, 4),
    POS_6('\uf835', 6, 5),
    POS_7('\uf836', 7, 6),
    POS_8('\uf837', 8, 7),
    POS_9('\uf838', 9, 8),
    POS_10('\uf839', 10, 9),
    POS_11('\uf83a', 11, 10),
    POS_12('\uf83b', 12, 11),
    POS_13('\uf83c', 13, 12),
    POS_14('\uf83d', 14, 13),
    POS_15('\uf83e', 15, 14),
    POS_16('\uf83f', 16, 15),
    POS_24('\uf840', 24, 23),
    POS_32('\uf841', 32, 31),
    POS_48('\uf842', 48, 47),
    POS_64('\uf843', 64, 63),
    POS_128('\uf844', 128, 127),
    POS_256('\uf845', 256, 255);

    private static final HashMap<Integer, OffsetFont> negFastLookup = new HashMap<>();
    private static final HashMap<Integer, OffsetFont> posFastLookup = new HashMap<>();

    static {
        for (OffsetFont f : OffsetFont.values()) {
            if (f.advance > 0) {
                posFastLookup.put(f.advance, f);
            } else {
                negFastLookup.put(-f.advance, f);
            }
        }
    }

    private final char character;
    private final int advance;
    private final int height;

    OffsetFont(char character, int advance, int height) {
        this.character = character;
        this.advance = advance;
        this.height = height;
    }

    public char character() {
        return this.character;
    }

    public float advance() {
        return this.advance;
    }

    public int height() {
        return this.height;
    }

    public static String createOffsets(float offset) {
        if (offset >= 0) {
            return shortestPosChars(offset);
        } else {
            return shortestNegChars(-offset);
        }
    }

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
        stringBuilder.append(negFastLookup.get(n).character());
        return stringBuilder.toString();
    }

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
        stringBuilder.append(posFastLookup.get(n).character());
        return stringBuilder.toString();
    }
}
