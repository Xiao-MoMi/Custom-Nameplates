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

package net.momirealms.customnameplates.api.feature.advance;

import net.kyori.adventure.key.Key;
import net.momirealms.customnameplates.common.plugin.feature.Reloadable;
import net.momirealms.customnameplates.common.util.Tuple;

import java.util.List;

public interface AdvanceManager extends Reloadable {

    void loadTemplates();

    ConfigurableFontAdvanceData getCustomFontData(String id);

    CharacterFontAdvanceData getCharacterFontData(String id);

    float getLineAdvance(String text);

    float getCharAdvance(char[] chars, Key font, boolean bold);

    int getLines(String text, int width);

    List<Tuple<String, Key, Boolean>> miniMessageToIterable(String text);
}
