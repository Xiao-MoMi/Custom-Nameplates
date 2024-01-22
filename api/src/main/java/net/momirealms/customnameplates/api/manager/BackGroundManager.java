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

import net.momirealms.customnameplates.api.mechanic.background.BackGround;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface BackGroundManager {

    /**
     * Get a background's config by key
     *
     * @param key key
     * @return background
     */
    @Nullable
    BackGround getBackGround(@NotNull String key);

    Collection<BackGround> getBackGrounds();

    /**
     * Register a background into the plugin
     * This will fail if there already exists one with the same key
     *
     * @param key key
     * @param backGround background
     * @return success or not
     */
    boolean registerBackGround(@NotNull String key, @NotNull BackGround backGround);

    /**
     * Unregister a background by key
     * This will fail if the key doesn't exist
     *
     * @param key key
     * @return success or not
     */
    boolean unregisterBackGround(@NotNull String key);
}
