package net.momirealms.customnameplates.api.util;

public class FontUtils {

    private static String namespace;
    private static String font;

    /**
     * Surround the text with ascent font
     *
     * @param text text
     * @param ascent ascent
     * @return ascent font text
     */
    public static String surroundAscentFont(String text, int ascent) {
        return getAscentFontTag(ascent) + text + getFontTagCloser();
    }

    /**
     * Surround the text with ascent unicode
     *
     * @param text text
     * @param ascent ascent
     * @return ascent font text
     */
    public static String surroundAscentUnicodeFont(String text, int ascent) {
        return getAscentUnicodeFontTag(ascent) + text + getFontTagCloser();
    }

    /**
     * Surround the text with custom nameplates font
     *
     * @param text text
     * @return font text
     */
    public static String surroundNameplateFont(String text) {
        return getMiniMessageFontTag() + text + getFontTagCloser();
    }

    private static String getAscentFontTag(int ascent) {
        return "<font:" + namespace + ":ascent_" + ascent + ">";
    }

    private static String getAscentUnicodeFontTag(int ascent) {
        return "<font:" + namespace + ":ascent_unicode_" + ascent + ">";
    }

    private static String getMiniMessageFontTag() {
        return "<font:" + namespace + ":" + font + ">";
    }

    private static String getFontTagCloser() {
        return "</font>";
    }

    /**
     * Get a text's width
     *
     * @param text text
     * @return width
     */
    public static int getTextWidth(String text) {
        return 0;
    }

    /**
     * Set namespace and font
     *
     * @param n namespace
     * @param f font
     */
    public static void setNameSpaceAndFont(String n, String f) {
        namespace = n;
        font = f;
    }
}
