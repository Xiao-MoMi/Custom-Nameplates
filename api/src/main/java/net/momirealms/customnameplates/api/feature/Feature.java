package net.momirealms.customnameplates.api.feature;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.placeholder.Placeholder;

import java.util.Set;

public interface Feature {

    String name();

    Set<Placeholder> activePlaceholders();

    Set<Placeholder> allPlaceholders();

    void notifyPlaceholderUpdates(CNPlayer p1, boolean force);
}
