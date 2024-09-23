package net.momirealms.customnameplates.api.feature.bossbar;

import java.util.UUID;

public interface BossBar {

    UUID uuid();

    float progress();

    Color color();

    Overlay overlay();

    enum Overlay {
        PROGRESS,
        NOTCHED_6,
        NOTCHED_10,
        NOTCHED_12,
        NOTCHED_20
    }

    enum Color {
        BLUE,
        GREEN,
        PINK,
        PURPLE,
        RED,
        WHITE,
        YELLOW
    }
}
