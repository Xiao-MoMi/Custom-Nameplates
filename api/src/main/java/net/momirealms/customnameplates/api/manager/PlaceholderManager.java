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

import net.momirealms.customnameplates.api.mechanic.placeholder.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface PlaceholderManager {

    /**
     * Detect all the placeholders
     *
     * @param text text
     * @return placeholder
     */
    @NotNull
    List<String> detectPlaceholders(String text);

    /**
     * Get a static text instance
     *
     * @param key key
     * @return static text
     */
    @Nullable
    StaticText getStaticText(String key);

    /**
     * Get all the static texts
     *
     * @return static texts
     */
    Collection<StaticText> getStaticTexts();

    /**
     * Get a switch text instance
     *
     * @param key key
     * @return switch text
     */
    @Nullable
    SwitchText getSwitchText(String key);

    /**
     * Get all the switch texts
     *
     * @return switch texts
     */
    Collection<SwitchText> getSwitchTexts();

    /**
     * Get a descent text instance
     *
     * @param key key
     * @return descent text
     */
    @Nullable
    DescentText getDescentText(String key);

    /**
     * Get all the descent texts
     *
     * @return descent texts
     */
    Collection<DescentText> getDescentTexts();

    /**
     * Get a conditional text
     *
     * @param key key
     * @return conditional text
     */
    @Nullable
    ConditionalText getConditionalText(String key);

    /**
     * Get all the conditional texts
     *
     * @return conditional texts
     */
    Collection<ConditionalText> getConditionalTexts();

    /**
     * Get a nameplate text
     *
     * @param key key
     * @return nameplate text
     */
    @Nullable
    NameplateText getNameplateText(String key);

    /**
     * Get all the nameplate texts
     *
     * @return nameplate texts
     */
    Collection<NameplateText> getNameplateTexts();

    /**
     * Get a background text
     *
     * @param key key
     * @return background text
     */
    @Nullable
    BackGroundText getBackGroundText(String key);

    /**
     * Get all the background texts
     *
     * @return background texts
     */
    Collection<BackGroundText> getBackGroundTexts();

    /**
     * Get a vanilla hud
     *
     * @param key key
     * @return vanilla hud
     */
    VanillaHud getVanillaHud(String key);

    /**
     * Get all the vanilla huds
     *
     * @return vanilla huds
     */
    Collection<VanillaHud> getVanillaHuds();
}
