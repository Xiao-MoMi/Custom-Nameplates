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

package net.momirealms.customnameplates.api.mechanic.character;

import net.momirealms.customnameplates.api.util.LogUtils;

public class ConfiguredChar {

    private char character;
    private String pngFile;
    private int height;
    private int width;
    private int ascent;

    private ConfiguredChar() {

    }

    public ConfiguredChar(char character, String pngFile, int height, int width, int ascent) {
        this.character = character;
        this.pngFile = pngFile;
        this.height = height;
        this.width = width;
        this.ascent = ascent;
    }

    public char getCharacter() {
        return character;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getPngFile() {
        return pngFile;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getAscent() {
        return ascent;
    }

    public String getFile() {
        return pngFile + ".png";
    }

    public static class Builder {

        private final ConfiguredChar configuredChar;

        public static Builder of() {
            return new Builder();
        }

        public Builder() {
            this.configuredChar = new ConfiguredChar();
        }

        public Builder character(char character) {
            configuredChar.character = character;
            return this;
        }

        public Builder png(String png) {
            configuredChar.pngFile = png;
            return this;
        }

        public Builder height(int height) {
            configuredChar.height = height;
            return this;
        }

        public Builder ascent(int ascent) {
            configuredChar.ascent = ascent;
            if (ascent >= configuredChar.height) {
                LogUtils.warn("Invalid config for " + configuredChar.pngFile);
                LogUtils.warn("Ascent " + ascent + " should be no higher than Height " + configuredChar.height);
            }
            return this;
        }

        public Builder descent(int descent) {
            if (descent < 0) {
                LogUtils.warn("Invalid config for " + configuredChar.pngFile);
                LogUtils.warn("Descent " + descent + " should be no lower than 0");
            }
            configuredChar.ascent = configuredChar.height - descent;
            return this;
        }

        public Builder width(int width) {
            configuredChar.width = width;
            return this;
        }

        public ConfiguredChar build() {
            return configuredChar;
        }
    }
}
