package net.momirealms.customnameplates.api.util;

public enum Alignment {

    CENTER(0),
    LEFT(1),
    RIGHT(2);

    final int id;

    Alignment(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
