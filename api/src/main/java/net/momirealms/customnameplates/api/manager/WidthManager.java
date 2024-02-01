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

import net.momirealms.customnameplates.api.mechanic.font.FontData;
import net.momirealms.customnameplates.common.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface WidthManager {

    /**
     * Register a font data into the plugin
     * If there already exists one, it would fail
     *
     * @param key key
     * @param fontData data
     * @return success or not
     */
    boolean registerFontData(@NotNull Key key, @NotNull FontData fontData);

    /**
     * Unregister a font data from map
     *
     * @param key key
     * @return success or not
     */
    boolean unregisterFontData(@NotNull Key key);

    /**
     * Get font data by key
     *
     * @param key key
     * @return font data
     */
    @Nullable
    FontData getFontData(@NotNull Key key);

    /**
     * Get text's width (MiniMessage format text)
     *
     * @param textWithTags text
     * @return width
     */
    int getTextWidth(@NotNull String textWithTags);
}
