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

import net.momirealms.customnameplates.api.feature.CarouselText;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.api.util.Alignment;
import net.momirealms.customnameplates.api.util.Vector3;

public interface NameTagConfig {

    String id();

    Requirement[] ownerRequirements();

    Requirement[] viewerRequirements();

    CarouselText[] carouselTexts();

    byte opacity();

    int backgroundColor();

    boolean hasShadow();

    boolean isSeeThrough();

    boolean useDefaultBackgroundColor();

    boolean affectedByCrouching();

    boolean affectedByScaling();

    Alignment alignment();

    float viewRange();

    float shadowRadius();

    float shadowStrength();

    Vector3 scale();

    Vector3 translation();

    int lineWidth();

    static Builder builder() {
        return new NameTagConfigImpl.BuilderImpl();
    }

    interface Builder {

        Builder id(String id);

        Builder lineWidth(int lineWidth);

        Builder ownerRequirement(Requirement[] requirements);

        Builder viewerRequirement(Requirement[] requirements);

        Builder carouselText(CarouselText[] carouselTexts);

        Builder opacity(byte opacity);

        Builder backgroundColor(int backgroundColor);

        Builder hasShadow(boolean hasShadow);

        Builder seeThrough(boolean seeThrough);

        Builder useDefaultBackgroundColor(boolean useDefaultBackgroundColor);

        Builder alignment(Alignment alignment);

        Builder viewRange(float viewRange);

        Builder shadowRadius(float shadowRadius);

        Builder shadowStrength(float shadowStrength);

        Builder scale(Vector3 scale);

        Builder translation(Vector3 translation);

        Builder affectedByCrouching(boolean affectedByCrouching);

        Builder affectedByScaling(boolean affectedByScale);

        NameTagConfig build();
    }
}
