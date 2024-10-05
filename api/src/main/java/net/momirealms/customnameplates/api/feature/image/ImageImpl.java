/*
 *  Copyright (C) <2024> <XiaoMoMi>
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

package net.momirealms.customnameplates.api.feature.image;

import net.momirealms.customnameplates.api.feature.ConfiguredCharacter;

import java.util.Objects;

public class ImageImpl implements Image {

    private final String id;
    private final boolean hasShadow;
    private final int opacity;
    private final ConfiguredCharacter character;

    public ImageImpl(String id, boolean hasShadow, int opacity, ConfiguredCharacter character) {
        this.id = id;
        this.hasShadow = hasShadow;
        this.opacity = opacity;
        this.character = character;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public boolean hasShadow() {
        return hasShadow;
    }

    @Override
    public int opacity() {
        return opacity;
    }

    @Override
    public ConfiguredCharacter character() {
        return character;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ImageImpl image = (ImageImpl) object;
        return Objects.equals(id, image.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static class BuilderImpl implements Builder {

        private String id;
        private boolean hasShadow;
        private int opacity;
        private ConfiguredCharacter character;

        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public Builder hasShadow(boolean has) {
            this.hasShadow = has;
            return this;
        }

        @Override
        public Builder opacity(int opacity) {
            this.opacity = opacity;
            return this;
        }

        @Override
        public Builder character(ConfiguredCharacter character) {
            this.character = character;
            return this;
        }

        @Override
        public Image build() {
            return new ImageImpl(id, hasShadow, opacity, character);
        }
    }
}
