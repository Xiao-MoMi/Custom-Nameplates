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

package net.momirealms.customnameplates.api.feature.bubble;

import net.momirealms.customnameplates.api.util.Vector3;

public interface BubbleConfig {

    String id();

    int backgroundColor();

    int lineWidth();

    int maxLines();

    Bubble[] bubbles();

    String textPrefix();

    String textSuffix();

    String displayName();

    Vector3 scale();

    static Builder builder() {
        return new BubbleConfigImpl.BuilderImpl();
    }

    interface Builder {

        Builder id(String id);

        Builder displayName(String displayName);

        Builder backgroundColor(int backgroundColor);

        Builder lineWidth(int lineWidth);

        Builder maxLines(int maxLines);

        Builder bubbles(Bubble[] bubbles);

        Builder textPrefix(String textPrefix);

        Builder textSuffix(String textSuffix);

        Builder scale(Vector3 scale);

        BubbleConfig build();
    }
}
