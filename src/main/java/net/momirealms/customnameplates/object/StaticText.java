package net.momirealms.customnameplates.object;

public record StaticText(String text, int value, StaticState staticState) {

    public enum StaticState {

        LEFT,
        MIDDLE,
        RIGHT
    }
}