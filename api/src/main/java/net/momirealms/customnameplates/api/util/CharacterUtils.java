package net.momirealms.customnameplates.api.util;

public class CharacterUtils {

    public static char[] unicodeToChars(String unicodeString) {
        String processedString = unicodeString.replace("\\u", "");
        int length = processedString.length() / 4;
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            int codePoint = Integer.parseInt(processedString.substring(i * 4, i * 4 + 4), 16);
            if (Character.isSupplementaryCodePoint(codePoint)) {
                chars[i] = Character.highSurrogate(codePoint);
                chars[++i] = Character.lowSurrogate(codePoint);
            } else {
                chars[i] = (char) codePoint;
            }
        }
        return chars;
    }

    public static String native2ascii(char c) {
        StringBuilder stringBuilder_1 = new StringBuilder("\\u");
        StringBuilder stringBuilder_2 = new StringBuilder(Integer.toHexString(c));
        stringBuilder_2.reverse();
        for (int n = 4 - stringBuilder_2.length(), i = 0; i < n; i++) stringBuilder_2.append('0');
        for (int j = 0; j < 4; j++) stringBuilder_1.append(stringBuilder_2.charAt(3 - j));
        return stringBuilder_1.toString();
    }
}
