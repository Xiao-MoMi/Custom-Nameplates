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

package net.momirealms.customnameplates.objects.font;

import net.momirealms.customnameplates.manager.WidthManager;

import java.util.Objects;

public class FontUtil {

    public static int getInfo(char c) {
        return Objects.requireNonNullElse(WidthManager.fontWidth.get(c), 8);
    }

    public static int getTotalWidth(String s) {
        int length = s.length();
        int n = 0;
        for (int i = 0; i < length; i++) {
            n += getInfo(s.charAt(i));
        }
        return n + length;
    }

    public static String getOffset(int offset) {
        if (offset >= 0) {
            return FontOffset.getShortestPosChars(offset);
        }
        else {
            return FontOffset.getShortestNegChars(-offset);
        }
    }
}
