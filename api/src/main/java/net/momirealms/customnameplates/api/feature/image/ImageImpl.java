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
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Implementation of Image
 */
public class ImageImpl implements Image {
    private final String id;
    private final boolean removeShadow;
    private final ConfiguredCharacter character;
    private final Animation animation;

    private ImageImpl(String id, boolean removeShadow, Animation animation, ConfiguredCharacter character) {
        this.id = id;
        this.removeShadow = removeShadow;
        this.character = character;
        this.animation = animation;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public boolean removeShadow() {
        return removeShadow;
    }

    @Override
    public @Nullable Animation animation() {
        return animation;
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

    /**
     * The builder Implementation
     */
    public static class BuilderImpl implements Builder {
        private String id;
        private boolean removeShadow;
        private ConfiguredCharacter character;
        private Animation animation;

        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public Builder removeShadow(boolean removeShadow) {
            this.removeShadow = removeShadow;
            return this;
        }

        @Override
        public Builder animation(@Nullable Animation animation) {
            this.animation = animation;
            return this;
        }

        @Override
        public Builder character(ConfiguredCharacter character) {
            this.character = character;
            return this;
        }

        @Override
        public Image build() {
            return new ImageImpl(id, removeShadow, animation, character);
        }
    }
}
