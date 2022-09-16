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

public record BackGround(SimpleChar start, SimpleChar offset_1,
                         SimpleChar offset_2, SimpleChar offset_4,
                         SimpleChar offset_8, SimpleChar offset_16,
                         SimpleChar offset_32, SimpleChar offset_64,
                         SimpleChar offset_128, SimpleChar end,
                         int start_width, int end_width) {

    public String getBackGround(int n) {

        String offset = FontOffset.getShortestNegChars(n + end_width + 2);
        n = n + start_width + end_width + 2;
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(start.getChars());
        while (n >= 128) {
            stringBuilder.append(FontOffset.NEG_1.getCharacter());
            stringBuilder.append(offset_128.getChars());
            n -= 128;
        }
        if (n - 64 >= 0) {
            stringBuilder.append(FontOffset.NEG_1.getCharacter());
            stringBuilder.append(offset_64.getChars());
            n -= 64;
        }
        if (n - 32 >= 0) {
            stringBuilder.append(FontOffset.NEG_1.getCharacter());
            stringBuilder.append(offset_32.getChars());
            n -= 32;
        }
        if (n - 16 >= 0) {
            stringBuilder.append(FontOffset.NEG_1.getCharacter());
            stringBuilder.append(offset_16.getChars());
            n -= 16;
        }
        if (n - 8 >= 0) {
            stringBuilder.append(FontOffset.NEG_1.getCharacter());
            stringBuilder.append(offset_8.getChars());
            n -= 8;
        }
        if (n - 4 >= 0) {
            stringBuilder.append(FontOffset.NEG_1.getCharacter());
            stringBuilder.append(offset_4.getChars());
            n -= 4;
        }
        if (n - 2 >= 0) {
            stringBuilder.append(FontOffset.NEG_1.getCharacter());
            stringBuilder.append(offset_2.getChars());
            n -= 2;
        }
        if (n - 1 >= 0) {
            stringBuilder.append(FontOffset.NEG_1.getCharacter());
            stringBuilder.append(offset_1.getChars());
        }
        stringBuilder.append(FontOffset.NEG_1.getCharacter());
        stringBuilder.append(end.getChars()).append(offset);
        return stringBuilder.toString();
    }
}
