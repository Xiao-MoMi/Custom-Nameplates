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

package net.momirealms.customnameplates.api.feature.tag;

import java.util.function.Predicate;

public interface TagRenderer {

    double hatOffset();

    void hatOffset(double hatOffset);

    void onTick();

    void destroy();

    void addTag(Tag tag);

    Tag[] tags();

    int removeTagIf(Predicate<Tag> predicate);

    int tagIndex(Tag tag);

    void addTag(Tag tag, int index);

    void removeTag(Tag tag);
}
