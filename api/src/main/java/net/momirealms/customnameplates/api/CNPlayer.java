package net.momirealms.customnameplates.api;

import net.momirealms.customnameplates.api.feature.Feature;
import net.momirealms.customnameplates.api.placeholder.Placeholder;
import net.momirealms.customnameplates.api.placeholder.PlaceholderUpdateTask;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.api.util.Vector3;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface CNPlayer {

    String name();

    UUID uuid();

    int entityID();

    Vector3 position();

    Set<Feature> getUsedFeatures(Placeholder placeholder);

    Object player();

    boolean isLoaded();

    @Nullable
    PlaceholderUpdateTask getRefreshValueTask();

    Placeholder[] activePlaceholders();

    boolean isOnline();

    String getValue(String id);

    String getRelationalValue(String id, CNPlayer another);

    void addFeature(Feature feature);

    void removeFeature(Feature feature);

    String setValue(String id, String value);

    String setRelationalValue(String id, CNPlayer another, String value);

    boolean isMet(Requirement[] requirements);

    boolean isMet(CNPlayer another, Requirement[] requirements);

    void forceUpdate(Set<Placeholder> placeholders);

    void addPlayerToTracker(CNPlayer another);

    void removePlayerFromTracker(CNPlayer another);

    Collection<CNPlayer> nearbyPlayers();

    void trackPassengers(CNPlayer another, int... passengers);

    void untrackPassengers(CNPlayer another, int... passengers);

    Set<Integer> getTrackedPassengers(CNPlayer another);

    Set<Integer> passengers();

    boolean isCrouching();
}
