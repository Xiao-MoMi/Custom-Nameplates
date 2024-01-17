package net.momirealms.customnameplates.paper.mechanic.bossbar;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum BarColor {
    PINK,
    BLUE,
    RED,
    GREEN,
    YELLOW,
    PURPLE,
    WHITE;

    public static BarColor getColor(@NotNull String name) {
        try {
            return BarColor.valueOf(name.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Color: " + name + " doesn't exist");
        }
    }
}
