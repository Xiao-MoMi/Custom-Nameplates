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
import net.momirealms.customnameplates.api.feature.pack.CharacterArranger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ConfiguredCharacter {

    private final char character;
    private final File imageFile;

    private final int height;
    private final int ascent;

    private final float advance;

    public static ConfiguredCharacter create(File imageFile, int ascent, int height) {
        return new ConfiguredCharacter(CharacterArranger.getAndIncrease(), imageFile, ascent, height);
    }

    public static ConfiguredCharacter create(File imageFile, int ascent, int height, float advance) {
        return new ConfiguredCharacter(CharacterArranger.getAndIncrease(), imageFile, ascent, height, advance);
    }

    public ConfiguredCharacter(char character, File imageFile, int ascent, int height, float advance) {
        this.character = character;
        this.imageFile = imageFile;
        this.ascent = ascent;
        this.height = height;
        this.advance = advance;
    }

    public ConfiguredCharacter(char character, File imageFile, int ascent, int height) {
        this.character = character;
        this.imageFile = imageFile;
        this.ascent = ascent;
        this.height = height;
        if (this.ascent > this.height) {
            CustomNameplates.getInstance().getPluginLogger().severe(String.format("Found an issue in the image config. Ascent(%s) is higher than height(%s). " + imageFile.getAbsolutePath(), ascent, height));
        }
        try {
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            int imageHeight = bufferedImage.getHeight();
            int imageWidth = bufferedImage.getWidth();

            float scale = (float) height / (float) imageHeight;
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
            advance = (int) (0.5 + (double) ((i+1f) * scale)) + 1f;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public char character() {
        return character;
    }

    public int ascent() {
        return ascent;
    }

    public float advance() {
        return advance;
    }

    public int height() {
        return height;
    }

    public File imageFile() {
        return imageFile;
    }
}
