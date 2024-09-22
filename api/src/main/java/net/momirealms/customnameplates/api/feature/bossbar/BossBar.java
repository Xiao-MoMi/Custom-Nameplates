package net.momirealms.customnameplates.api.feature.bossbar;

import net.momirealms.customnameplates.api.CNPlayer;

import java.util.List;
import java.util.UUID;

public interface BossBar<P> {

    UUID uuid();

    String name();

    float progress();

    void progress(float progress);

    Color color();

    void color(Color color);

    Overlay overlay();

    void overlay(Overlay overlay);

    List<CNPlayer<P>> viewers();

    void addViewer(CNPlayer<P> viewer);

    void removeViewer(CNPlayer<P> viewer);

    boolean hasViewer(CNPlayer<P> viewer);

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
