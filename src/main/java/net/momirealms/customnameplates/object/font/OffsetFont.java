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

package net.momirealms.customnameplates.object.font;

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
}
