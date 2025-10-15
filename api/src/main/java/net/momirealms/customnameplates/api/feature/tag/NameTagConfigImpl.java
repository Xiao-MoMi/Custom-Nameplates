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
import net.momirealms.customnameplates.common.util.Tristate;

/**
 * Implementation of NameTagConfig
 */
public class NameTagConfigImpl implements NameTagConfig {
    private final String id;
    private final Requirement[] ownerRequirements;
    private final Requirement[] viewerRequirements;
    private final CarouselText[] carouselTexts;
    private final int lineWidth;
    private final byte opacity;
    private final int backgroundColor;
    private final boolean hasShadow;
    private final Tristate isSeeThrough;
    private final boolean useDefaultBackgroundColor;
    private final Alignment alignment;
    private final float viewRange;
    private final float shadowRadius;
    private final float shadowStrength;
    private final Vector3 scale;
    private final Vector3 translation;
    private final boolean affectedByCrouching;
    private final boolean affectedByScale;
    private final boolean affectedBySpectator;

    private NameTagConfigImpl(String id, Requirement[] ownerRequirements, Requirement[] viewerRequirements, CarouselText[] carouselTexts, int lineWidth, byte opacity, int backgroundColor, boolean hasShadow, Tristate isSeeThrough, boolean useDefaultBackgroundColor, Alignment alignment, float viewRange, float shadowRadius, float shadowStrength, Vector3 scale, Vector3 translation, boolean affectedByCrouching, boolean affectedByScale, boolean affectedBySpectator) {
        this.id = id;
        this.ownerRequirements = ownerRequirements;
        this.viewerRequirements = viewerRequirements;
        this.carouselTexts = carouselTexts;
        this.opacity = opacity;
        this.backgroundColor = backgroundColor;
        this.hasShadow = hasShadow;
        this.isSeeThrough = isSeeThrough;
        this.useDefaultBackgroundColor = useDefaultBackgroundColor;
        this.alignment = alignment;
        this.viewRange = viewRange;
        this.shadowRadius = shadowRadius;
        this.shadowStrength = shadowStrength;
        this.scale = scale;
        this.translation = translation;
        this.lineWidth = lineWidth;
        this.affectedByCrouching = affectedByCrouching;
        this.affectedByScale = affectedByScale;
        this.affectedBySpectator = affectedBySpectator;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Requirement[] ownerRequirements() {
        return ownerRequirements;
    }

    @Override
    public Requirement[] viewerRequirements() {
        return viewerRequirements;
    }

    @Override
    public CarouselText[] carouselTexts() {
        return carouselTexts;
    }

    @Override
    public byte opacity() {
        return opacity;
    }

    @Override
    public int backgroundColor() {
        return backgroundColor;
    }

    @Override
    public boolean hasShadow() {
        return hasShadow;
    }

    @Override
    public Tristate isSeeThrough() {
        return isSeeThrough;
    }

    @Override
    public boolean useDefaultBackgroundColor() {
        return useDefaultBackgroundColor;
    }

    @Override
    public boolean affectedByCrouching() {
        return affectedByCrouching;
    }

    @Override
    public boolean affectedByScaling() {
        return affectedByScale;
    }

    @Override
    public boolean affectedBySpectator() {
        return affectedBySpectator;
    }

    @Override
    public Alignment alignment() {
        return alignment;
    }

    @Override
    public float viewRange() {
        return viewRange;
    }

    @Override
    public float shadowRadius() {
        return shadowRadius;
    }

    @Override
    public float shadowStrength() {
        return shadowStrength;
    }

    @Override
    public Vector3 scale() {
        return scale;
    }

    @Override
    public Vector3 translation() {
        return translation;
    }

    @Override
    public int lineWidth() {
        return lineWidth;
    }

    /**
     * The builder implementation
     */
    public static class BuilderImpl implements Builder {
        private String id;
        private Requirement[] ownerRequirements;
        private Requirement[] viewerRequirements;
        private CarouselText[] carouselTexts;
        private int lineWidth;
        private byte opacity;
        private int backgroundColor;
        private boolean hasShadow;
        private Tristate isSeeThrough;
        private boolean useDefaultBackgroundColor;
        private Alignment alignment;
        private float viewRange;
        private float shadowRadius;
        private float shadowStrength;
        private Vector3 scale;
        private Vector3 translation;
        private boolean affectedByCrouching;
        private boolean affectedByScale;
        private boolean affectedBySpectator;

        @Override
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        @Override
        public Builder lineWidth(int lineWidth) {
            this.lineWidth = lineWidth;
            return this;
        }

        @Override
        public Builder ownerRequirement(Requirement[] requirements) {
            this.ownerRequirements = requirements;
            return this;
        }

        @Override
        public Builder viewerRequirement(Requirement[] requirements) {
            this.viewerRequirements = requirements;
            return this;
        }

        @Override
        public Builder carouselText(CarouselText[] carouselTexts) {
            this.carouselTexts = carouselTexts;
            return this;
        }

        @Override
        public Builder opacity(byte opacity) {
            this.opacity = opacity;
            return this;
        }

        @Override
        public Builder backgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        @Override
        public Builder hasShadow(boolean hasShadow) {
            this.hasShadow = hasShadow;
            return this;
        }

        @Override
        public Builder seeThrough(Tristate seeThrough) {
            this.isSeeThrough = seeThrough;
            return this;
        }

        @Override
        public Builder useDefaultBackgroundColor(boolean useDefaultBackgroundColor) {
            this.useDefaultBackgroundColor = useDefaultBackgroundColor;
            return this;
        }

        @Override
        public Builder alignment(Alignment alignment) {
            this.alignment = alignment;
            return this;
        }

        @Override
        public Builder viewRange(float viewRange) {
            this.viewRange = viewRange;
            return this;
        }

        @Override
        public Builder shadowRadius(float shadowRadius) {
            this.shadowRadius = shadowRadius;
            return this;
        }

        @Override
        public Builder shadowStrength(float shadowStrength) {
            this.shadowStrength = shadowStrength;
            return this;
        }

        @Override
        public Builder scale(Vector3 scale) {
            this.scale = scale;
            return this;
        }

        @Override
        public Builder translation(Vector3 translation) {
            this.translation = translation;
            return this;
        }

        @Override
        public Builder affectedByCrouching(boolean affectedByCrouching) {
            this.affectedByCrouching = affectedByCrouching;
            return this;
        }

        @Override
        public Builder affectedBySpectator(boolean affectedBySpectator) {
            this.affectedBySpectator = affectedBySpectator;
            return this;
        }

        @Override
        public Builder affectedByScaling(boolean affectedByScale) {
            this.affectedByScale = affectedByScale;
            return this;
        }

        @Override
        public NameTagConfig build() {
            return new NameTagConfigImpl(id, ownerRequirements, viewerRequirements, carouselTexts, lineWidth, opacity, backgroundColor, hasShadow, isSeeThrough, useDefaultBackgroundColor, alignment, viewRange, shadowRadius, shadowStrength, scale, translation, affectedByCrouching, affectedByScale, affectedBySpectator);
        }
    }
}
