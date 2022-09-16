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

import net.momirealms.customnameplates.font.FontOffset;
import net.momirealms.customnameplates.font.FontUtil;
import net.momirealms.customnameplates.helper.Log;
import org.bukkit.ChatColor;

public class NameplateUtil {

    public static String makeCustomNameplate(String prefix, String name, String suffix, NameplateConfig nameplate) {
        int totalWidth = FontUtil.getTotalWidth(ChatColor.stripColor(prefix + name + suffix));
        boolean isEven = totalWidth % 2 == 0;
        char left = nameplate.left().getChars();
        char middle = nameplate.middle().getChars();
        char right = nameplate.right().getChars();
        char neg_1 = FontOffset.NEG_1.getCharacter();
        int offset_2 = nameplate.right().getWidth() - nameplate.middle().getWidth();
        int left_offset = totalWidth + (nameplate.left().getWidth() + nameplate.right().getWidth())/2 + 1;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(FontOffset.getShortestNegChars(isEven ? left_offset : left_offset + 1 ));
        stringBuilder.append(left).append(neg_1);
        int mid_amount = (totalWidth + 1 + offset_2) / (nameplate.middle().getWidth());
        if (mid_amount == 0) {
            stringBuilder.append(middle).append(neg_1);
        }
        else {
            for (int i = 0; i < mid_amount; i++) {
                stringBuilder.append(middle).append(neg_1);
            }
        }
        stringBuilder.append(FontOffset.getShortestNegChars(nameplate.right().getWidth() - ((totalWidth + 1 + offset_2) % nameplate.middle().getWidth() + (isEven ? 0 : -1))));
//        stringBuilder.append(FontOffset.getShortestNegChars(nameplate.right().getWidth() - ((totalWidth + 1) % nameplate.middle().getWidth() + (isEven ? 0 : -1))));
        stringBuilder.append(middle).append(neg_1);
        stringBuilder.append(right).append(neg_1);
        stringBuilder.append(FontOffset.getShortestNegChars(left_offset - 1));
        return stringBuilder.toString();
    }

    public static String getSuffixChar(String name) {
        int totalWidth = FontUtil.getTotalWidth(ChatColor.stripColor(name));
        return FontOffset.getShortestNegChars(totalWidth + totalWidth % 2 + 1);
    }

    public static String makeCustomBubble(String prefix, String name, String suffix, BubbleConfig bubble) {
        int totalWidth = FontUtil.getTotalWidth(ChatColor.stripColor(prefix + name + suffix));
        boolean isEven = totalWidth % 2 == 0;
        char left = bubble.left().getChars();
        char middle = bubble.middle().getChars();
        char right = bubble.right().getChars();
        char tail = bubble.tail().getChars();
        char neg_1 = FontOffset.NEG_1.getCharacter();
        int offset = bubble.middle().getWidth() - bubble.tail().getWidth();
        int left_offset = totalWidth + bubble.left().getWidth() + 1;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(FontOffset.getShortestNegChars(isEven ? left_offset - offset : left_offset + 1 - offset));
        stringBuilder.append(left).append(neg_1);
        int mid_amount = (totalWidth + 1 - bubble.tail().getWidth()) / (bubble.middle().getWidth());
        if (mid_amount == 0) {
            stringBuilder.append(tail).append(neg_1);
        }
        else {
            for (int i = 0; i <= mid_amount; i++) {
                if (i == mid_amount/2) {
                    stringBuilder.append(tail).append(neg_1);
                }
                else {
                    stringBuilder.append(middle).append(neg_1);
                }
            }
        }
        stringBuilder.append(FontOffset.getShortestNegChars(bubble.right().getWidth() - ((totalWidth + 1 + offset) % bubble.middle().getWidth() + (isEven ? 0 : -1))));
        stringBuilder.append(middle).append(neg_1);
        stringBuilder.append(right).append(neg_1);
        stringBuilder.append(FontOffset.getShortestNegChars(left_offset - 1));
        return stringBuilder.toString();
    }
}
