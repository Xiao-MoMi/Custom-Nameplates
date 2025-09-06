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

import net.momirealms.customnameplates.api.CustomNameplates;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Represents a configured character with associated image, dimensions, and advance (spacing).
 */
public class ConfiguredCharacter {

    private final char character;
    private final File imageFile;
    private final int height;
    private final int ascent;
    private final float advance;

    /**
     * Creates a new ConfiguredCharacter instance using an automatically assigned character.
     *
     * @param imageFile the image file associated with this character
     * @param ascent    the ascent value (distance from the baseline to the top of the character)
     * @param height    the height of the character
     * @return a new ConfiguredCharacter instance
     */
    public static ConfiguredCharacter create(File imageFile, int ascent, int height) {
        return new ConfiguredCharacter(CharacterArranger.getAndIncrease(), imageFile, ascent, height);
    }

    /**
     * Creates a new ConfiguredCharacter instance using an automatically assigned character and a specified advance value.
     *
     * @param imageFile the image file associated with this character
     * @param ascent    the ascent value (distance from the baseline to the top of the character)
     * @param height    the height of the character
     * @param advance   the advance (spacing) for this character
     * @return a new ConfiguredCharacter instance
     */
    public static ConfiguredCharacter create(File imageFile, int ascent, int height, float advance) {
        return new ConfiguredCharacter(CharacterArranger.getAndIncrease(), imageFile, ascent, height, advance);
    }

    /**
     * Constructs a new ConfiguredCharacter instance with specified character, image, ascent, height, and advance values.
     *
     * @param character the character to associate
     * @param imageFile the image file
     * @param ascent    the ascent value
     * @param height    the height of the character
     * @param advance   the advance (spacing) for the character
     */
    public ConfiguredCharacter(char character, File imageFile, int ascent, int height, float advance) {
        this.character = character;
        this.imageFile = imageFile;
        this.ascent = ascent;
        this.height = height;
        this.advance = advance;
    }

    /**
     * Constructs a new ConfiguredCharacter instance with automatic advance calculation.
     *
     * @param character the character to associate
     * @param imageFile the image file
     * @param ascent    the ascent value
     * @param height    the height of the character
     */
    public ConfiguredCharacter(char character, File imageFile, int ascent, int height) {
        this.character = character;
        this.imageFile = imageFile;
        this.ascent = ascent;
        this.height = height;

        if (this.ascent > this.height) {
            CustomNameplates.getInstance().getPluginLogger().severe(String.format(
                    "Found an issue in the image config. Ascent(%s) is higher than height(%s). %s", ascent, height, imageFile.getAbsolutePath()));
        }

        try {
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            int imageHeight = bufferedImage.getHeight();
            int imageWidth = bufferedImage.getWidth();

            float scale = (float) height / imageHeight;
            int i;
            outer:
            for (i = imageWidth - 1; i >= 0; --i) {
                for (int k = 0; k < imageHeight; ++k) {
                    int rgb = bufferedImage.getRGB(i, k);
                    int alpha = (rgb >> 24) & 0xff;
                    if (alpha != 0) {
                        break outer;
                    }
                }
            }
            advance = (float) Math.round((i + 1) * scale) + 1f;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + imageFile.toPath(), e);
        }
    }

    /**
     * Returns the character associated with this ConfiguredCharacter.
     *
     * @return the character
     */
    public char character() {
        return character;
    }

    /**
     * Returns the ascent value of this character.
     *
     * @return the ascent value
     */
    public int ascent() {
        return ascent;
    }

    /**
     * Returns the advance (spacing) of this character.
     *
     * @return the advance value
     */
    public float advance() {
        return advance;
    }

    /**
     * Returns the height of this character.
     *
     * @return the character height
     */
    public int height() {
        return height;
    }

    /**
     * Returns the image file associated with this character.
     *
     * @return the image file
     */
    public File imageFile() {
        return imageFile;
    }
}
