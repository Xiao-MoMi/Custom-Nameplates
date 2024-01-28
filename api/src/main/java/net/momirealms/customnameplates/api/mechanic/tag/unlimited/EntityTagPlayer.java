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

package net.momirealms.customnameplates.api.mechanic.tag.unlimited;

import net.momirealms.customnameplates.api.mechanic.tag.NameplatePlayer;

import java.util.Vector;

public interface EntityTagPlayer extends NameplatePlayer {

    void addTag(DynamicTextEntity tag);

    void addTag(StaticTextEntity tag);

    DynamicTextEntity addTag(DynamicTextTagSetting setting);

    StaticTextEntity addTag(StaticTextTagSetting setting);

    void removeTag(DynamicTextEntity tag);

    void removeTag(StaticTextEntity tag);

    Vector<DynamicTextEntity> getDynamicTags();

    Vector<StaticTextEntity> getStaticTags();

    void setHatOffset(double hatOffset);

    double getHatOffset();

    void destroy();
}
