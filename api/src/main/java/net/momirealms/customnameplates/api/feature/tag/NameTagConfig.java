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

/**
 * NameTag Configuration
 */
public interface NameTagConfig {

    /**
     * Returns the unique ID of the name tag configuration.
     *
     * @return the name tag configuration ID
     */
    String id();

    /**
     * Returns the requirements for the name tag owner.
     *
     * @return an array of owner requirements
     */
    Requirement[] ownerRequirements();

    /**
     * Returns the requirements for the name tag viewer.
     *
     * @return an array of viewer requirements
     */
    Requirement[] viewerRequirements();

    /**
     * Returns the carousel texts displayed on the name tag.
     *
     * @return an array of carousel texts
     */
    CarouselText[] carouselTexts();

    /**
     * Returns the opacity level of the name tag.
     *
     * @return the opacity value as a byte
     */
    byte opacity();

    /**
     * Returns the background color of the name tag.
     *
     * @return the background color as an integer
     */
    int backgroundColor();

    /**
     * Checks if the name tag has a shadow effect.
     *
     * @return true if the name tag has a shadow, false otherwise
     */
    boolean hasShadow();

    /**
     * Checks if the name tag is see-through.
     *
     * @return true if the name tag is see-through, false otherwise
     */
    @Deprecated
    boolean isSeeThrough();

    /**
     * Checks if the default background color is used.
     *
     * @return true if the default background color is used, false otherwise
     */
    boolean useDefaultBackgroundColor();

    /**
     * Checks if the name tag is affected by the crouching state of the player.
     *
     * @return true if affected by crouching, false otherwise
     */
    boolean affectedByCrouching();

    /**
     * Checks if the name tag is affected by scaling.
     *
     * @return true if affected by scaling, false otherwise
     */
    boolean affectedByScaling();

    /**
     * Checks if the name tag is affected by spectator mode.
     *
     * @return true if affected by spectator mode, false otherwise
     */
    boolean affectedBySpectator();

    /**
     * Returns the alignment of the name tag.
     *
     * @return the alignment of the name tag
     */
    Alignment alignment();

    /**
     * Returns the view range of the name tag.
     *
     * @return the view range as a float
     */
    float viewRange();

    /**
     * Returns the shadow radius of the name tag.
     *
     * @return the shadow radius as a float
     */
    float shadowRadius();

    /**
     * Returns the shadow strength of the name tag.
     *
     * @return the shadow strength as a float
     */
    float shadowStrength();

    /**
     * Returns the scaling vector of the name tag.
     *
     * @return the scaling vector as a Vector3
     */
    Vector3 scale();

    /**
     * Returns the translation vector for the name tag.
     *
     * @return the translation vector as a Vector3
     */
    Vector3 translation();

    /**
     * Returns the maximum line width of the name tag.
     *
     * @return the line width as an integer
     */
    int lineWidth();

    /**
     * Creates a new builder for constructing a NameTagConfig.
     *
     * @return a new builder instance
     */
    static Builder builder() {
        return new NameTagConfigImpl.BuilderImpl();
    }

    /**
     * Builder for constructing NameTagConfig instances with customizable properties.
     */
    interface Builder {

        /**
         * Sets the unique ID for the NameTagConfig.
         *
         * @param id the name tag configuration ID
         * @return the builder instance
         */
        Builder id(String id);

        /**
         * Sets the maximum line width for the name tag.
         *
         * @param lineWidth the maximum line width
         * @return the builder instance
         */
        Builder lineWidth(int lineWidth);

        /**
         * Sets the requirements for the name tag owner.
         *
         * @param requirements an array of owner requirements
         * @return the builder instance
         */
        Builder ownerRequirement(Requirement[] requirements);

        /**
         * Sets the requirements for the name tag viewer.
         *
         * @param requirements an array of viewer requirements
         * @return the builder instance
         */
        Builder viewerRequirement(Requirement[] requirements);

        /**
         * Sets the carousel texts for the name tag.
         *
         * @param carouselTexts an array of carousel texts
         * @return the builder instance
         */
        Builder carouselText(CarouselText[] carouselTexts);

        /**
         * Sets the opacity level for the name tag.
         *
         * @param opacity the opacity value as a byte
         * @return the builder instance
         */
        Builder opacity(byte opacity);

        /**
         * Sets the background color for the name tag.
         *
         * @param backgroundColor the background color as an integer
         * @return the builder instance
         */
        Builder backgroundColor(int backgroundColor);

        /**
         * Sets whether the name tag has a shadow effect.
         *
         * @param hasShadow true if the name tag should have a shadow, false otherwise
         * @return the builder instance
         */
        Builder hasShadow(boolean hasShadow);

        /**
         * Sets whether the name tag is see-through.
         *
         * @param seeThrough true if the name tag is see-through, false otherwise
         * @return the builder instance
         */
        Builder seeThrough(boolean seeThrough);

        /**
         * Sets whether the name tag uses the default background color.
         *
         * @param useDefaultBackgroundColor true to use the default background color, false otherwise
         * @return the builder instance
         */
        Builder useDefaultBackgroundColor(boolean useDefaultBackgroundColor);

        /**
         * Sets the alignment of the name tag.
         *
         * @param alignment the alignment setting
         * @return the builder instance
         */
        Builder alignment(Alignment alignment);

        /**
         * Sets the view range for the name tag.
         *
         * @param viewRange the view range as a float
         * @return the builder instance
         */
        Builder viewRange(float viewRange);

        /**
         * Sets the shadow radius for the name tag.
         *
         * @param shadowRadius the shadow radius as a float
         * @return the builder instance
         */
        Builder shadowRadius(float shadowRadius);

        /**
         * Sets the shadow strength for the name tag.
         *
         * @param shadowStrength the shadow strength as a float
         * @return the builder instance
         */
        Builder shadowStrength(float shadowStrength);

        /**
         * Sets the scaling factor for the name tag.
         *
         * @param scale the scaling vector
         * @return the builder instance
         */
        Builder scale(Vector3 scale);

        /**
         * Sets the translation vector for the name tag.
         *
         * @param translation the translation vector
         * @return the builder instance
         */
        Builder translation(Vector3 translation);

        /**
         * Sets whether the name tag is affected by crouching.
         *
         * @param affectedByCrouching true if the name tag is affected by crouching, false otherwise
         * @return the builder instance
         */
        Builder affectedByCrouching(boolean affectedByCrouching);

        /**
         * Sets whether the name tag is affected by spectator mode.
         *
         * @param affectedBySpectator true if the name tag is affected by spectator mode, false otherwise
         * @return the builder instance
         */
        Builder affectedBySpectator(boolean affectedBySpectator);

        /**
         * Sets whether the name tag is affected by scaling.
         *
         * @param affectedByScale true if the name tag is affected by scaling, false otherwise
         * @return the builder instance
         */
        Builder affectedByScaling(boolean affectedByScale);

        /**
         * Builds and returns the configured NameTagConfig instance.
         *
         * @return the constructed NameTagConfig
         */
        NameTagConfig build();
    }
}

