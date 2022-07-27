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

import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.font.FontCache;
import net.momirealms.customnameplates.font.FontNegative;
import net.momirealms.customnameplates.font.FontWidth;
import net.momirealms.customnameplates.font.FontWidthThin;
import org.bukkit.ChatColor;

public class NameplateUtil {

    private final FontCache fontcache;

    public NameplateUtil(FontCache font) {
        this.fontcache = font;
    }

    public String makeCustomNameplate(String prefix, String name, String suffix) {
        int totalWidth;
        if (ConfigManager.MainConfig.thin_font){
            totalWidth = FontWidthThin.getTotalWidth(ChatColor.stripColor(prefix + name + suffix));
        }else {
            totalWidth = FontWidth.getTotalWidth(ChatColor.stripColor(prefix + name + suffix));
        }
        boolean isEven = totalWidth % 2 == 0;
        char left = this.fontcache.getChar().getLeft();
        char middle = this.fontcache.getChar().getMiddle();
        char right = this.fontcache.getChar().getRight();
        char neg_1 = FontNegative.NEG_1.getCharacter();
        int left_offset = totalWidth + 16 + 1;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(FontNegative.getShortestNegChars(isEven ? left_offset : left_offset + 1));
        stringBuilder.append(left).append(neg_1);
        int mid_amount = (totalWidth + 1) / 16;
        for (int i = 0; i < (mid_amount == 0 ? 1 : mid_amount); i++) {
            stringBuilder.append(middle).append(neg_1);
        }
        stringBuilder.append(FontNegative.getShortestNegChars(16 - ((totalWidth + 1) % 16 + (isEven ? 0 : 1))));
        stringBuilder.append(middle).append(neg_1);
        stringBuilder.append(right).append(neg_1);
        stringBuilder.append(FontNegative.getShortestNegChars(isEven ? left_offset : left_offset + 1));
        return stringBuilder.toString();
    }

    public String getSuffixLength(String name) {
        int totalWidth;
        if (ConfigManager.MainConfig.thin_font){
            totalWidth = FontWidthThin.getTotalWidth(ChatColor.stripColor(name));
        }else {
            totalWidth = FontWidth.getTotalWidth(ChatColor.stripColor(name));
        }
        return FontNegative.getShortestNegChars(totalWidth + totalWidth % 2 + 1);
    }

    public ChatColor getColor() {
        return this.fontcache.getConfig().getColor();
    }
}
