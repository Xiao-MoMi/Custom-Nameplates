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

package net.momirealms.customnameplates.nameplates;

import net.momirealms.customnameplates.font.FontChar;

import java.util.Objects;

public record NameplateInstance(String name, FontChar fontChar, NameplateConfig config) {

    public static NameplateInstance EMPTY = new NameplateInstance("none", new FontChar('小', '默', '米'), NameplateConfig.EMPTY);

    public String getName() {
        return this.name;
    }

    public FontChar getChar() {
        return this.fontChar;
    }

    public NameplateConfig getConfig() {
        return this.config;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof NameplateInstance nameplateInstance)) return false;
        return Objects.equals(nameplateInstance.getName(), this.getName());
    }

    @Override
    public int hashCode() {
        int n = 1;
        String name = this.getName();
        int n2 = n * 59 + ((name == null) ? 43 : name.hashCode());
        FontChar fontChar = this.getChar();
        int n3 = n2 * 59 + ((fontChar == null) ? 43 : fontChar.hashCode());
        NameplateConfig config = this.getConfig();
        return n3 * 59 + ((config == null) ? 43 : config.hashCode());
    }

    @Override
    public String toString() {
        return "FontCache{" +
                "name='" + name + '\'' +
                ", fontChar=" + fontChar +
                ", config=" + config +
                '}';
    }
}
