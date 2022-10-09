package net.momirealms.customnameplates.objects;

public record StaticText(String text, int value) {

    public String getText() {
        return text;
    }

    public int getStaticValue() {
        return value;
    }
}