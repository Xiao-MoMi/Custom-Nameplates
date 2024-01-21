package net.momirealms.customnameplates.common.team;

import java.util.Locale;

public enum TeamColor {

    NONE,
    BLACK,
    DARK_BLUE,
    DARK_GREEN,
    DARK_AQUA,
    DARK_RED,
    DARK_PURPLE,
    GOLD,
    GRAY,
    DARK_GRAY,
    BLUE,
    GREEN,
    AQUA,
    RED,
    LIGHT_PURPLE,
    YELLOW,
    WHITE,
    CUSTOM;

    public TeamColor getById(String id) throws IllegalArgumentException {
        return valueOf(id.toUpperCase(Locale.ENGLISH));
    }
}
