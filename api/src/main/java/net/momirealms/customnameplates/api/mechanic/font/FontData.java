package net.momirealms.customnameplates.api.mechanic.font;

import java.util.HashMap;

public class FontData {

    private final HashMap<Character, Integer> widthData;
    private final int defaultWidth;

    public FontData(int defaultWidth) {
        this.widthData = new HashMap<>();
        this.defaultWidth = defaultWidth;
    }

    public void registerCharWidth(char c, int width) {
        widthData.put(c, width);
    }

    public int getWidth(char c) {
        return widthData.getOrDefault(c, defaultWidth);
    }

    public HashMap<Character, Integer> getWidthData() {
        return widthData;
    }

    public void overrideWith(FontData fontData) {
        if (fontData == null) return;
        widthData.putAll(fontData.getWidthData());
    }
}
