/*
 *  Copyright (C) <2024> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customnameplates.api;

import net.momirealms.customnameplates.api.feature.Feature;
import net.momirealms.customnameplates.api.feature.TickStampData;
import net.momirealms.customnameplates.api.network.Tracker;
import net.momirealms.customnameplates.api.placeholder.Placeholder;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.api.util.Vector3;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CNPlayer {

    String name();

    UUID uuid();

    int entityID();

    Vector3 position();

    String world();

    void forceUpdate(Set<Placeholder> placeholders, Set<CNPlayer> another);

    Set<Feature> getUsedFeatures(Placeholder placeholder);

    Object player();

    double scale();

    boolean isCrouching();

    boolean isLoaded();

    boolean isPreviewing();

    boolean hasPermission(String permission);

    long playerTime();

    List<Placeholder> getRefreshValueTask();

    Placeholder[] activePlaceholders();

    boolean isOnline();

    String getData(Placeholder placeholder);

    TickStampData<String> getValue(Placeholder placeholder);

    String getRelationalData(Placeholder placeholder, CNPlayer another);

    TickStampData<String> getRelationalValue(Placeholder placeholder, CNPlayer another);

    void setValue(Placeholder placeholder, TickStampData<String> value);

    boolean setValue(Placeholder placeholder, String value);

    void setRelationalValue(Placeholder placeholder, CNPlayer another, TickStampData<String> value);

    boolean setRelationalValue(Placeholder placeholder, CNPlayer another, String value);

    void addFeature(Feature feature);

    void removeFeature(Feature feature);

    boolean isMet(Requirement[] requirements);

    boolean isMet(CNPlayer another, Requirement[] requirements);

    Tracker addPlayerToTracker(CNPlayer another);

    void removePlayerFromTracker(CNPlayer another);

    Set<CNPlayer> nearbyPlayers();

    void trackPassengers(CNPlayer another, int... passengers);

    void untrackPassengers(CNPlayer another, int... passengers);

    Set<Integer> getTrackedPassengerIds(CNPlayer another);

    Set<Integer> passengers();

    boolean isFlying();

    Tracker getTracker(CNPlayer another);

    String equippedBubble();

    void equippedBubble(String equippedBubble);

    String equippedNameplate();

    void equippedNameplate(String equippedNameplate);

    int remainingAir();
}
