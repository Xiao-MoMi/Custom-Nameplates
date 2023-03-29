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

package net.momirealms.customnameplates.object;

public class SimpleChar {

    private final int height;
    private final int ascent;
    private final char chars;
    private final String file;
    private final int width;

    public SimpleChar(int height, int ascent, int width, char chars, String path) {
        this.height = height;
        this.ascent = ascent;
        this.chars = chars;
        this.file = path;
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public int getAscent() {
        return ascent;
    }

    public char getChars() {
        return chars;
    }

    public String getFile() {
        return file;
    }

    public int getWidth() {
        return width;
    }
}
