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

package net.momirealms.customnameplates.background;

import net.momirealms.customnameplates.font.FontNegative;
import net.momirealms.customnameplates.resource.ResourceManager;

public class BackGround {

    private final String start;
    private final String offset_1;
    private final String offset_2;
    private final String offset_4;
    private final String offset_8;
    private final String offset_16;
    private final String offset_32;
    private final String offset_64;
    private final String offset_128;
    private final String end;
    private final int offset_y;
    private final int offset_x;

    public BackGround(String start, String offset_1, String offset_2,
                      String offset_4, String offset_8, String offset_16,
                      String offset_32, String offset_64, String offset_128, String end, int offset_y, int offset_x){
        this.start = start; this.offset_1 = offset_1; this.offset_2 = offset_2;
        this.end = end; this.offset_4 = offset_4; this.offset_8 = offset_8;
        this.offset_16 = offset_16; this.offset_32 = offset_32; this.offset_64 = offset_64;
        this.offset_128 = offset_128;
        this.offset_y = offset_y;
        this.offset_x = offset_x;
    }

    public String getBackGround(int n) {
        n += offset_x;
        String offset = FontNegative.getShortestNegChars(n);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ResourceManager.bgCaches.get(start));
        if (n > 128) {
            stringBuilder.append(FontNegative.NEG_1.getCharacter());
            stringBuilder.append(ResourceManager.bgCaches.get(offset_128));
            n -= 128;
        }
        if (n - 64 > 0) {
            stringBuilder.append(FontNegative.NEG_1.getCharacter());
            stringBuilder.append(ResourceManager.bgCaches.get(offset_64));
            n -= 64;
        }
        if (n - 32 > 0) {
            stringBuilder.append(FontNegative.NEG_1.getCharacter());
            stringBuilder.append(ResourceManager.bgCaches.get(offset_32));
            n -= 32;
        }
        if (n - 16 > 0) {
            stringBuilder.append(FontNegative.NEG_1.getCharacter());
            stringBuilder.append(ResourceManager.bgCaches.get(offset_16));
            n -= 16;
        }
        if (n - 8 > 0) {
            stringBuilder.append(FontNegative.NEG_1.getCharacter());
            stringBuilder.append(ResourceManager.bgCaches.get(offset_8));
            n -= 8;
        }
        if (n - 4 > 0) {
            stringBuilder.append(FontNegative.NEG_1.getCharacter());
            stringBuilder.append(ResourceManager.bgCaches.get(offset_4));
            n -= 4;
        }
        if (n - 2 > 0) {
            stringBuilder.append(FontNegative.NEG_1.getCharacter());
            stringBuilder.append(ResourceManager.bgCaches.get(offset_2));
            n -= 2;
        }
        if (n - 1 > 0) {
            stringBuilder.append(FontNegative.NEG_1.getCharacter());
            stringBuilder.append(ResourceManager.bgCaches.get(offset_1));
        }
        stringBuilder.append(FontNegative.NEG_1.getCharacter());
        stringBuilder.append(ResourceManager.bgCaches.get(end)).append(offset);
        return stringBuilder.toString();
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public int getOffset_y() {
        return offset_y;
    }

    public String getOffset_1() {
        return offset_1;
    }

    public String getOffset_2() {
        return offset_2;
    }

    public String getOffset_4() {
        return offset_4;
    }

    public String getOffset_8() {
        return offset_8;
    }

    public String getOffset_16() {
        return offset_16;
    }

    public String getOffset_32() {
        return offset_32;
    }

    public String getOffset_64() {
        return offset_64;
    }

    public String getOffset_128() {
        return offset_128;
    }
}
