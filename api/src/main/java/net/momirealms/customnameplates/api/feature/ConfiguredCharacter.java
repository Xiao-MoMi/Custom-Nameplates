package net.momirealms.customnameplates.api.feature;

import net.momirealms.customnameplates.api.CustomNameplates;

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
            for (i = imageWidth - 1; i >= 0; --i) {
                for (int k = 0; k < imageHeight; ++k) {
                    int rgb = bufferedImage.getRGB(i, k);
                    int alpha = (rgb >> 24) & 0xff;
                    if (alpha != 0) {
                        break;
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
