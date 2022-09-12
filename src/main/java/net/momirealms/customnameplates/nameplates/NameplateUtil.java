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

package net.momirealms.customnameplates.nameplates;

import net.momirealms.customnameplates.font.*;
import org.bukkit.ChatColor;

public class NameplateUtil {

    public static String makeCustomNameplate(String prefix, String name, String suffix, NameplateInstance nameplate) {
        int totalWidth = FontUtil.getTotalWidth(ChatColor.stripColor(prefix + name + suffix));
        boolean isEven = totalWidth % 2 == 0;
        char left = nameplate.getChar().getLeft();
        char middle = nameplate.getChar().getMiddle();
        char right = nameplate.getChar().getRight();
        char neg_1 = FontOffset.NEG_1.getCharacter();
        int left_offset = totalWidth + 16 + 1;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(FontOffset.getShortestNegChars(isEven ? left_offset : left_offset + 1));
        stringBuilder.append(left).append(neg_1);
        int mid_amount = (totalWidth + 1) / 16;
        for (int i = 0; i < (mid_amount == 0 ? 1 : mid_amount); i++) stringBuilder.append(middle).append(neg_1);
        stringBuilder.append(FontOffset.getShortestNegChars(16 - ((totalWidth + 1) % 16 + (isEven ? 0 : 1))));
        stringBuilder.append(middle).append(neg_1);
        stringBuilder.append(right).append(neg_1);
        stringBuilder.append(FontOffset.getShortestNegChars(left_offset - 1));
        return stringBuilder.toString();
    }

    public static String getSuffixChar(String name) {
        int totalWidth = FontUtil.getTotalWidth(ChatColor.stripColor(name));
        return FontOffset.getShortestNegChars(totalWidth + totalWidth % 2 + 1);
    }
}
