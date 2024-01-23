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

    @Override
    public String toString() {
        return "FontData{" +
                "widthData=" + widthData +
                ", defaultWidth=" + defaultWidth +
                '}';
    }
}
