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

package net.momirealms.customnameplates.api.feature;

public interface AdaptiveImage {

    /**
     * Creates the prefix for the image with the specified advance and margins.
     *
     * @param advance the advance for the image
     * @param leftMargin the left margin for the image
     * @param rightMargin the right margin for the image
     * @return the generated image prefix
     */
    String createImagePrefix(float advance, float leftMargin, float rightMargin);

    /**
     * Creates the suffix for the image with the specified advance and margins.
     *
     * @param advance the advance for the image
     * @param leftMargin the left margin for the image
     * @param rightMargin the right margin for the image
     * @return the generated image suffix
     */
    String createImageSuffix(float advance, float leftMargin, float rightMargin);

    /**
     * Creates the full image string with the specified advance and margins.
     *
     * @param advance the advance for the image
     * @param leftMargin the left margin for the image
     * @param rightMargin the right margin for the image
     * @return the generated image string
     */
    String createImage(float advance, float leftMargin, float rightMargin);
}
