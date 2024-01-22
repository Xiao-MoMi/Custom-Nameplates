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

package net.momirealms.customnameplates.api.manager;

import net.momirealms.customnameplates.api.mechanic.character.ConfiguredChar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ImageManager {

    /**
     * Get an image by key
     *
     * @param key key
     * @return image
     */
    @Nullable
    ConfiguredChar getImage(@NotNull String key);

    Collection<ConfiguredChar> getImages();

    /**
     * Register am image into the plugin
     * This will fail if there already exists one with the same key
     *
     * @param key key
     * @param configuredChar image
     * @return success or not
     */
    boolean registerImage(@NotNull String key, @NotNull ConfiguredChar configuredChar);

    /**
     * Unregister an image by key
     * This will fail if the key doesn't exist
     *
     * @param key key
     * @return success or not
     */
    boolean unregisterImage(@NotNull String key);
}
