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

package net.momirealms.customnameplates.api.mechanic.font;

public enum OffsetFont {

    NEG_1('\uf801', -1, -3),
    NEG_2('\uf802', -2, -4),
    NEG_3('\uf803', -3, -5),
    NEG_4('\uf804', -4, -6),
    NEG_5('\uf805', -5, -7),
    NEG_6('\uf806', -6, -8),
    NEG_7('\uf807', -7, -9),
    NEG_8('\uf808', -8, -10),
    NEG_16('\uf809', -16, -18),
    NEG_32('\uf80a', -32, -34),
    NEG_64('\uf80b', -64, -66),
    NEG_128('\uf80c', -128, -130),
    POS_1('\uf811', 1, -1),
    POS_2('\uf812', 2, 1),
    POS_3('\uf813', 3, 2),
    POS_4('\uf814', 4, 3),
    POS_5('\uf815', 5, 4),
    POS_6('\uf816', 6, 5),
    POS_7('\uf817', 7, 6),
    POS_8('\uf818', 8, 7),
    POS_16('\uf819', 16, 15),
    POS_32('\uf81a', 32, 31),
    POS_64('\uf81b', 64, 63),
    POS_128('\uf81c', 128, 127);

    private final char character;
    private final int space;
    private final int height;

    OffsetFont(char character, int space, int height) {
        this.character = character;
        this.space = space;
        this.height = height;
    }

    public char getCharacter() {
        return this.character;
    }

    public int getSpace() {
        return this.space;
    }

    public int getHeight() {
        return this.height;
    }

    public static String getOffsetChars(int offset) {
        if (offset >= 0) {
            return getShortestPosChars(offset);
        } else {
            return getShortestNegChars(-offset);
        }
    }

    public static String getShortestNegChars(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        while (n >= 128) {
            stringBuilder.append(OffsetFont.NEG_128.getCharacter());
            n -= 128;
        }
        if (n - 64 >= 0) {
            stringBuilder.append(OffsetFont.NEG_64.getCharacter());
            n -= 64;
        }
        if (n - 32 >= 0) {
            stringBuilder.append(OffsetFont.NEG_32.getCharacter());
            n -= 32;
        }
        if (n - 16 >= 0) {
            stringBuilder.append(OffsetFont.NEG_16.getCharacter());
            n -= 16;
        }
        if (n - 8 >= 0) {
            stringBuilder.append(OffsetFont.NEG_8.getCharacter());
            n -= 8;
        }
        if (n - 7 >= 0) {
            stringBuilder.append(OffsetFont.NEG_7.getCharacter());
            n -= 7;
        }
        if (n - 6 >= 0) {
            stringBuilder.append(OffsetFont.NEG_6.getCharacter());
            n -= 6;
        }
        if (n - 5 >= 0) {
            stringBuilder.append(OffsetFont.NEG_5.getCharacter());
            n -= 5;
        }
        if (n - 4 >= 0) {
            stringBuilder.append(OffsetFont.NEG_4.getCharacter());
            n -= 4;
        }
        if (n - 3 >= 0) {
            stringBuilder.append(OffsetFont.NEG_3.getCharacter());
            n -= 3;
        }
        if (n - 2 >= 0) {
            stringBuilder.append(OffsetFont.NEG_2.getCharacter());
            n -= 2;
        }
        if (n - 1 >= 0) {
            stringBuilder.append(OffsetFont.NEG_1.getCharacter());
        }
        return stringBuilder.toString();
    }

    public static String getShortestPosChars(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        while (n >= 128) {
            stringBuilder.append(OffsetFont.POS_128.getCharacter());
            n -= 128;
        }
        if (n - 64 >= 0) {
            stringBuilder.append(OffsetFont.POS_64.getCharacter());
            n -= 64;
        }
        if (n - 32 >= 0) {
            stringBuilder.append(OffsetFont.POS_32.getCharacter());
            n -= 32;
        }
        if (n - 16 >= 0) {
            stringBuilder.append(OffsetFont.POS_16.getCharacter());
            n -= 16;
        }
        if (n - 8 >= 0) {
            stringBuilder.append(OffsetFont.POS_8.getCharacter());
            n -= 8;
        }
        if (n - 7 >= 0) {
            stringBuilder.append(OffsetFont.POS_7.getCharacter());
            n -= 7;
        }
        if (n - 6 >= 0) {
            stringBuilder.append(OffsetFont.POS_6.getCharacter());
            n -= 6;
        }
        if (n - 5 >= 0) {
            stringBuilder.append(OffsetFont.POS_5.getCharacter());
            n -= 5;
        }
        if (n - 4 >= 0) {
            stringBuilder.append(OffsetFont.POS_4.getCharacter());
            n -= 4;
        }
        if (n - 3 >= 0) {
            stringBuilder.append(OffsetFont.POS_3.getCharacter());
            n -= 3;
        }
        if (n - 2 >= 0) {
            stringBuilder.append(OffsetFont.POS_2.getCharacter());
            n -= 2;
        }
        if (n - 1 >= 0) {
            stringBuilder.append(OffsetFont.POS_1.getCharacter());
        }
        return stringBuilder.toString();
    }
}
