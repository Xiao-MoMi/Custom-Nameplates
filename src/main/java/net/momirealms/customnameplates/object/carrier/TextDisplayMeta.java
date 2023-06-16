package net.momirealms.customnameplates.object.carrier;

public record TextDisplayMeta(boolean hasShadow, boolean isSeeThrough, boolean useDefaultBackground,
                              int backgroundColor, byte opacity) {

    public static TextDisplayMeta defaultValue = new TextDisplayMeta(false, false, true, 0, (byte) -1);
}
