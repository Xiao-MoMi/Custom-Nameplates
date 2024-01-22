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
import org.jetbrains.annotations.Nullable;

public interface WidthManager {

    boolean registerFontData(Key key, FontData fontData);

    boolean unregisterFontData(Key key);

    @Nullable FontData getFontData(Key key);

    int getTextWidth(String textWithTags);
}
