package net.momirealms.customnameplates.api.feature;

import net.momirealms.customnameplates.api.CNPlayer;

public interface RelationalFeature extends Feature {

    void notifyPlaceholderUpdates(CNPlayer p1, CNPlayer p2, boolean force);
}
