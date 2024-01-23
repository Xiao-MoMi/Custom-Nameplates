package net.momirealms.customnameplates.paper.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtils {

    public static void removeImageShadow(File file) {
        try {
            BufferedImage inputImage = ImageIO.read(file);

            int width = inputImage.getWidth();
            int height = inputImage.getHeight();
            BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            int[] pixels = new int[width * height];
            inputImage.getRGB(0, 0, width, height, pixels, 0, width);
            for (int i = 0; i < pixels.length; i++) {
                int alpha = (pixels[i] >> 24) & 0xFF;
                int red = (pixels[i] >> 16) & 0xFF;
                int green = (pixels[i] >> 8) & 0xFF;
                int blue = pixels[i] & 0xFF;
                if (alpha > 0) {
                    alpha = 254;
                }
                int newPixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                pixels[i] = newPixel;
            }
            outputImage.setRGB(0, 0, width, height, pixels, 0, width);
            ImageIO.write(outputImage, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setAnimatedImage(File file, int frames) {
        try {
            BufferedImage inputImage = ImageIO.read(file);
            int width = inputImage.getWidth();
            int height = inputImage.getHeight();
            int frameHeight = height / frames;
            BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            int newPixel = (1 << 24) | (10 << 16) | (width << 8) | frameHeight;
            for (int i = 0; i < frames; i++) {
                outputImage.setRGB(0, frameHeight * i, newPixel);
            }
            ImageIO.write(outputImage, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
