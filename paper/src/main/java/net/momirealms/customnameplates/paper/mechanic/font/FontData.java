package net.momirealms.customnameplates.paper.mechanic.font;

import net.momirealms.customnameplates.paper.setting.CNConfig;

import java.util.HashMap;

public class FontData {

    private final HashMap<Character, Short> widthData;

    public FontData() {
        this.widthData = new HashMap<>();
    }

    public void registerCharWidth(char c, short width) {
        widthData.put(c, width);
    }

    public short getWidth(char c) {
        return widthData.getOrDefault(c, CNConfig.defaultCharWidth);
    }
}
