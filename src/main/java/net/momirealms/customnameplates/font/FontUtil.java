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

import net.momirealms.customnameplates.ConfigManager;

import java.util.Objects;

public class FontUtil {

    /*
    获取每个字符的像素宽度
     */
    public static int getInfo(char c) {
        return Objects.requireNonNullElse(ConfigManager.fontWidth.get(c), 8);
    }

    /*
    计算一个字符串的总宽度
     */
    public static int getTotalWidth(String s) {
        int length = s.length();
        int n = 0;
        for (int i = 0; i < length; i++) {
            n += getInfo(s.charAt(i));
        }
        return n + length; //总长还需加上字符间距
    }
}
