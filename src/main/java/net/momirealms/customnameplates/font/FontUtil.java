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
        return n + length - 1; //总长还需加上字符间距
    }
}
