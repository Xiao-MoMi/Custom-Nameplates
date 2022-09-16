package net.momirealms.customnameplates.objects;

public class SimpleChar {

    public static SimpleChar none = new SimpleChar(16, 12, 16,'默', "none.png");

    private final int height;
    private final int ascent;
    private final char chars;
    private final String file;
    private final int width;

    public SimpleChar(int height, int ascent, int width, char chars, String path) {
        this.height = height;
        this.ascent = ascent;
        this.chars = chars;
        this.file = path;
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public int getAscent() {
        return ascent;
    }

    public char getChars() {
        return chars;
    }

    public String getFile() {
        return file;
    }

    public int getWidth() {
        return width;
    }
}
