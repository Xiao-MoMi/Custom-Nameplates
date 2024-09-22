package net.momirealms.customnameplates.api.tracker;

import net.momirealms.customnameplates.api.CNPlayer;

import java.util.Collection;

public interface TrackedEntityView<P> {

    Collection<CNPlayer<P>> nearbyPlayers();

    void addPlayer(CNPlayer<P> player);

    void removePlayer(CNPlayer<P> player);
}
