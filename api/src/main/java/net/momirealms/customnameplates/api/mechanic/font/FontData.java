package net.momirealms.customnameplates.api.mechanic.font;

import java.util.HashMap;

public class FontData {

    private final HashMap<Integer, Integer> widthData;
    private final int defaultWidth;

    public FontData(int defaultWidth) {
        this.widthData = new HashMap<>();
        this.defaultWidth = defaultWidth;
    }

    public void registerCharWidth(int codePoint, int width) {
        widthData.put(codePoint, width);
    }

    public int getWidth(int codePoint) {
        return widthData.getOrDefault(codePoint, defaultWidth);
    }

    public HashMap<Integer, Integer> getWidthData() {
        return widthData;
    }

    public void overrideWith(FontData fontData) {
        if (fontData == null) return;
        widthData.putAll(fontData.getWidthData());
    }
}
