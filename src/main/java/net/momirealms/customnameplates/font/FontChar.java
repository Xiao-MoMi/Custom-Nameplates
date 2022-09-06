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

package net.momirealms.customnameplates.font;

public record FontChar(char left, char middle, char right) {

    public char getLeft() {
        return this.left;
    }

    public char getMiddle() {
        return this.middle;
    }

    public char getRight() {
        return this.right;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof FontChar fontInfo)) return false;
        return this.getLeft() == fontInfo.getLeft() && this.getMiddle() == fontInfo.getMiddle() && this.getRight() == fontInfo.getRight();
    }

    @Override
    public int hashCode() {
        return ((59 + this.getLeft()) * 59 + this.getMiddle()) * 59 + this.getRight();
    }

    @Override
    public String toString() {
        return "FontChar{" +
                "left=" + left +
                ", middle=" + middle +
                ", right=" + right +
                '}';
    }
}