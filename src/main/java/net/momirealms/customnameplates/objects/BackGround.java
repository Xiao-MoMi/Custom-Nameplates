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

package net.momirealms.customnameplates.objects;

import net.momirealms.customnameplates.font.FontOffset;
import net.momirealms.customnameplates.resource.ResourceManager;

import java.util.HashMap;

public record BackGround(String key, String start, String offset_1,
                         String offset_2, String offset_4, String offset_8,
                         String offset_16, String offset_32, String offset_64,
                         String offset_128, String end, int offset_y, int start_width, int end_width, int size) {

    public String getBackGround(int n) {
        String offset = FontOffset.getShortestNegChars(n + end_width + 2);
        n = n + start_width + end_width + 2;
        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, Character> chars = ResourceManager.BACKGROUNDS.get(key);
        stringBuilder.append(chars.get(start));
        while (n >= 128) {
            stringBuilder.append(FontOffset.NEG_1.getCharacter());
            stringBuilder.append(chars.get(offset_128));
            n -= 128;
        }
        if (n - 64 >= 0) {
            stringBuilder.append(FontOffset.NEG_1.getCharacter());
            stringBuilder.append(chars.get(offset_64));
            n -= 64;
        }
        if (n - 32 >= 0) {
            stringBuilder.append(FontOffset.NEG_1.getCharacter());
            stringBuilder.append(chars.get(offset_32));
            n -= 32;
        }
        if (n - 16 >= 0) {
            stringBuilder.append(FontOffset.NEG_1.getCharacter());
            stringBuilder.append(chars.get(offset_16));
            n -= 16;
        }
        if (n - 8 >= 0) {
            stringBuilder.append(FontOffset.NEG_1.getCharacter());
            stringBuilder.append(chars.get(offset_8));
            n -= 8;
        }
        if (n - 4 >= 0) {
            stringBuilder.append(FontOffset.NEG_1.getCharacter());
            stringBuilder.append(chars.get(offset_4));
            n -= 4;
        }
        if (n - 2 >= 0) {
            stringBuilder.append(FontOffset.NEG_1.getCharacter());
            stringBuilder.append(chars.get(offset_2));
            n -= 2;
        }
        if (n - 1 >= 0) {
            stringBuilder.append(FontOffset.NEG_1.getCharacter());
            stringBuilder.append(chars.get(offset_1));
        }
        stringBuilder.append(FontOffset.NEG_1.getCharacter());
        stringBuilder.append(chars.get(end)).append(offset);
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

    public String getKey() {
        return key;
    }

    public int getSize() {
        return size;
    }
}
