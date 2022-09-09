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

package net.momirealms.customnameplates.font;

public enum FontOffset {

    /*
    实际向左移的距离是height（尺寸）-2
    */
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
    POS_2('\uf812', 2, 0),
    POS_3('\uf813', 3, 1),
    POS_4('\uf814', 4, 2),
    POS_5('\uf815', 5, 3),
    POS_6('\uf816', 6, 4),
    POS_7('\uf817', 7, 5),
    POS_8('\uf818', 8, 6),
    POS_16('\uf819', 16, 14),
    POS_32('\uf81a', 32, 30),
    POS_64('\uf81b', 64, 62),
    POS_128('\uf81c', 128, 126);

    private final char character;
    private final int space;
    private final int height;

    FontOffset(char character, int space, int height) {
        this.character = character;
        this.space = space;
        this.height = height;
    }

    /*
    获取最短的负空格字符
    */
    public static String getShortestNegChars(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        while (n >= 128) {
            stringBuilder.append(FontOffset.NEG_128.getCharacter());
            n -= 128;
        }
        if (n - 64 >= 0) {
            stringBuilder.append(FontOffset.NEG_64.getCharacter());
            n -= 64;
        }
        if (n - 32 >= 0) {
            stringBuilder.append(FontOffset.NEG_32.getCharacter());
            n -= 32;
        }
        if (n - 16 >= 0) {
            stringBuilder.append(FontOffset.NEG_16.getCharacter());
            n -= 16;
        }
        if (n - 8 >= 0) {
            stringBuilder.append(FontOffset.NEG_8.getCharacter());
            n -= 8;
        }
        if (n - 7 >= 0) {
            stringBuilder.append(FontOffset.NEG_7.getCharacter());
            n -= 7;
        }
        if (n - 6 >= 0) {
            stringBuilder.append(FontOffset.NEG_6.getCharacter());
            n -= 6;
        }
        if (n - 5 >= 0) {
            stringBuilder.append(FontOffset.NEG_5.getCharacter());
            n -= 5;
        }
        if (n - 4 >= 0) {
            stringBuilder.append(FontOffset.NEG_4.getCharacter());
            n -= 4;
        }
        if (n - 3 >= 0) {
            stringBuilder.append(FontOffset.NEG_3.getCharacter());
            n -= 3;
        }
        if (n - 2 >= 0) {
            stringBuilder.append(FontOffset.NEG_2.getCharacter());
            n -= 2;
        }
        if (n - 1 >= 0) {
            stringBuilder.append(FontOffset.NEG_1.getCharacter());
        }
        return stringBuilder.toString();
    }

    /*
    获取最短的正空格字符
    */
    public static String getShortestPosChars(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        while (n >= 128) {
            stringBuilder.append(FontOffset.POS_128.getCharacter());
            n -= 128;
        }
        if (n - 64 >= 0) {
            stringBuilder.append(FontOffset.POS_64.getCharacter());
            n -= 64;
        }
        if (n - 32 >= 0) {
            stringBuilder.append(FontOffset.POS_32.getCharacter());
            n -= 32;
        }
        if (n - 16 >= 0) {
            stringBuilder.append(FontOffset.POS_16.getCharacter());
            n -= 16;
        }
        if (n - 8 >= 0) {
            stringBuilder.append(FontOffset.POS_8.getCharacter());
            n -= 8;
        }
        if (n - 7 >= 0) {
            stringBuilder.append(FontOffset.POS_7.getCharacter());
            n -= 7;
        }
        if (n - 6 >= 0) {
            stringBuilder.append(FontOffset.POS_6.getCharacter());
            n -= 6;
        }
        if (n - 5 >= 0) {
            stringBuilder.append(FontOffset.POS_5.getCharacter());
            n -= 5;
        }
        if (n - 4 >= 0) {
            stringBuilder.append(FontOffset.POS_4.getCharacter());
            n -= 4;
        }
        if (n - 3 >= 0) {
            stringBuilder.append(FontOffset.POS_3.getCharacter());
            n -= 3;
        }
        if (n - 2 >= 0) {
            stringBuilder.append(FontOffset.POS_2.getCharacter());
            n -= 2;
        }
        if (n - 1 >= 0) {
            stringBuilder.append(FontOffset.POS_1.getCharacter());
        }
        return stringBuilder.toString();
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
}
