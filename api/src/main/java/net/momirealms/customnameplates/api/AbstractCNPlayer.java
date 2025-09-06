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

import io.netty.channel.Channel;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.momirealms.customnameplates.api.feature.Feature;
import net.momirealms.customnameplates.api.feature.TimeStampData;
import net.momirealms.customnameplates.api.feature.tag.TeamView;
import net.momirealms.customnameplates.api.network.Tracker;
import net.momirealms.customnameplates.api.placeholder.Placeholder;
import net.momirealms.customnameplates.api.placeholder.PlayerPlaceholder;
import net.momirealms.customnameplates.api.placeholder.RelationalPlaceholder;
import net.momirealms.customnameplates.api.placeholder.SharedPlaceholder;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.api.storage.data.PlayerData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Player instance adapted by CustomNameplates
 */
public abstract class AbstractCNPlayer<P> implements CNPlayer {
    /**
     * The CustomNameplates plugin
     */
    protected final CustomNameplates plugin;
    /**
     * Player netty channel
     */
    protected final Channel channel;
    /**
     * Platform player instance
     */
    protected P player;
    protected UUID uuid;
    protected int entityId;
    protected String name;

    private volatile boolean isLoaded = false;
    private volatile boolean tempPreviewing = false;
    private volatile boolean toggleablePreviewing = false;

    private String currentNameplate;
    private String nameplateData;
    private String currentBubble;
    private String bubbleData;

    private final TeamView teamView = new TeamView();

    // these maps might be visited by other threads through PlaceholderAPI
    private final Map<Integer, TimeStampData<String>> cachedValues = new ConcurrentHashMap<>(128);
    private final Map<Integer, WeakHashMap<CNPlayer, TimeStampData<String>>> cachedRelationalValues = new ConcurrentHashMap<>(128);
    private final Map<Integer, TimeStampData<Boolean>> cachedRequirements = new ConcurrentHashMap<>(32);
    private final Map<Integer, WeakHashMap<CNPlayer, TimeStampData<Boolean>>> cachedRelationalRequirements = new ConcurrentHashMap<>(32);

    private final Set<Feature> activeFeatures = new CopyOnWriteArraySet<>();
    private final Map<Placeholder, Set<Feature>> placeholder2Features = new ConcurrentHashMap<>();
    private final Map<Feature, Set<Placeholder>> feature2Placeholders = new ConcurrentHashMap<>();

    private final Map<CNPlayer, Tracker> trackers = new WeakHashMap<>();
    private final ReadWriteLock trackerLock = new ReentrantReadWriteLock();
    private final Vector<String> otherActionBarFeatures = new Vector<>();

    /**
     * Creates a player instance
     *
     * @param plugin CustomNameplates plugin
     * @param channel netty channel
     */
    protected AbstractCNPlayer(CustomNameplates plugin, Channel channel) {
        this.plugin = plugin;
        this.channel = channel;
    }

    @Override
    public void acquireActionBar(String id) {
        this.otherActionBarFeatures.add(id);
    }

    @Override
    public void releaseActionBar(String id) {
        this.otherActionBarFeatures.remove(id);
    }

    @Override
    public boolean shouldCNTakeOverActionBar() {
        return otherActionBarFeatures.isEmpty();
    }

    @Override
    public List<Placeholder> activePlaceholdersToRefresh() {
        Placeholder[] activePlaceholders = activePlaceholders();
        List<Placeholder> placeholderWithChildren = new ObjectArrayList<>();
        for (Placeholder placeholder : activePlaceholders) {
            childrenFirstList(placeholder, placeholderWithChildren);
        }
        return placeholderWithChildren.stream().distinct().toList();
    }

    private String updatePlayerPlaceholder(PlayerPlaceholder placeholder) {
        TimeStampData<String> value = getRawPlayerValue(placeholder);
        if (value == null) {
            value = new TimeStampData<>(placeholder.request(this), MainTask.getTicks(), true);
            setPlayerValue(placeholder, value);
            return value.data();
        }
        if (value.ticks() != MainTask.getTicks()) {
            String newValue = placeholder.request(this);
            value.updateTicks(!value.data().equals(newValue));
            value.data(newValue);
        }
        return value.data();
    }

    private void updateRelationalPlaceholder(RelationalPlaceholder placeholder, Collection<CNPlayer> others) {
        for (CNPlayer another : others) {
            updateRelationalPlaceholder(placeholder, another);
        }
    }

    private String updateRelationalPlaceholder(RelationalPlaceholder placeholder, CNPlayer another) {
        TimeStampData<String> value = getRawRelationalValue(placeholder, another);
        if (value == null) {
            value = new TimeStampData<>(placeholder.request(this, another), MainTask.getTicks(), true);
            setRelationalValue(placeholder, another, value);
            return value.data();
        }
        if (value.ticks() != MainTask.getTicks()) {
            String newValue = placeholder.request(this, another);
            value.updateTicks(!value.data().equals(newValue));
            value.data(newValue);
        }
        return value.data();
    }

    private String updateSharedPlaceholder(SharedPlaceholder placeholder) {
        TimeStampData<String> value = getRawSharedValue(placeholder);
        if (value == null) {
            String latest;
            if (MainTask.hasRequested(placeholder.countId())) {
                latest = placeholder.getLatestValue();
            } else {
                latest = placeholder.request();
            }
            value = new TimeStampData<>(latest, MainTask.getTicks(), true);
            setSharedValue(placeholder, value);
            return value.data();
        }
        if (value.ticks() != MainTask.getTicks()) {
            String latest;
            if (MainTask.hasRequested(placeholder.countId())) {
                latest = placeholder.getLatestValue();
            } else {
                latest = placeholder.request();
            }
            value.updateTicks(!value.data().equals(latest));
            value.data(latest);
        }
        return value.data();
    }

    @Override
    public void forceUpdatePlaceholders(Set<Placeholder> placeholders, Collection<CNPlayer> others) {
        if (placeholders.isEmpty()) return;
        List<Placeholder> placeholderWithChildren = new ObjectArrayList<>();
        for (Placeholder placeholder : placeholders) {
            childrenFirstList(placeholder, placeholderWithChildren);
        }
        placeholderWithChildren = placeholderWithChildren.stream().distinct().toList();
        for (Placeholder placeholder : placeholderWithChildren) {
             if (placeholder instanceof PlayerPlaceholder playerPlaceholder) {
                 updatePlayerPlaceholder(playerPlaceholder);
             } else if (placeholder instanceof RelationalPlaceholder relationalPlaceholder) {
                 updateRelationalPlaceholder(relationalPlaceholder, others);
             } else if (placeholder instanceof SharedPlaceholder sharedPlaceholder) {
                 updateSharedPlaceholder(sharedPlaceholder);
             }
        }
    }

    private void childrenFirstList(Placeholder placeholder, List<Placeholder> list) {
        if (placeholder.children().isEmpty()) {
            list.add(placeholder);
        } else {
            for (Placeholder child : placeholder.children()) {
                childrenFirstList(child, list);
            }
            list.add(placeholder);
        }
    }

    private void parentLastList(Placeholder placeholder, List<Placeholder> list) {
        if (placeholder.parents().isEmpty()) {
            list.add(placeholder);
        } else {
            list.add(placeholder);
            for (Placeholder parent : placeholder.parents()) {
                parentLastList(parent, list);
            }
        }
    }

    /**
     * Reload the player instance, clear caches
     */
    public void reload() {
        cachedValues.clear();
        cachedRelationalValues.clear();
        cachedRequirements.clear();
        cachedRelationalRequirements.clear();
        activeFeatures.clear();
        placeholder2Features.clear();
        feature2Placeholders.clear();
    }

    /**
     * Sets the platform player instance on join
     *
     * @param player player
     */
    public void setPlayer(P player) {
        this.player = player;
    }

    @Override
    public Channel channel() {
        return channel;
    }

    @Override
    public Set<Feature> activeFeatures(Placeholder placeholder) {
        return placeholder2Features.getOrDefault(placeholder, Collections.emptySet());
    }

    @Override
    public Object player() {
        return player;
    }

    /**
     * Sets if the player data is loaded
     *
     * @param loaded loaded or not
     */
    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    /**
     * Sets if the player is temporarily previewing the nameplate
     *
     * @param previewing is previewing or not
     */
    public void setTempPreviewing(boolean previewing) {
        this.tempPreviewing = previewing;
    }

    @Override
    public boolean isTempPreviewing() {
        return tempPreviewing;
    }

    /**
     * Sets if the player is using toggle command to preview his nameplate
     *
     * @param previewing is previewing or not
     */
    public void setToggleablePreviewing(boolean previewing) {
        this.toggleablePreviewing = previewing;
    }

    @Override
    public boolean isToggleablePreviewing() {
        return toggleablePreviewing;
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public void addFeature(Feature feature) {
        activeFeatures.add(feature);
        Set<Placeholder> allPlaceholders = feature.allPlaceholders();
        feature2Placeholders.put(feature, allPlaceholders);
        for (Placeholder placeholder : allPlaceholders) {
            Set<Feature> featureSet = placeholder2Features.computeIfAbsent(placeholder, k -> new ObjectOpenHashSet<>());
            featureSet.add(feature);
        }
    }

    @Override
    public void removeFeature(Feature feature) {
        activeFeatures.remove(feature);
        Set<Placeholder> placeholders = feature2Placeholders.remove(feature);
        if (placeholders != null) {
            for (Placeholder placeholder : placeholders) {
                Set<Feature> featureSet = placeholder2Features.get(placeholder);
                featureSet.remove(feature);
                if (featureSet.isEmpty()) placeholder2Features.remove(placeholder);
            }
        }
    }

    @Override
    public void setPlayerValue(PlayerPlaceholder placeholder, TimeStampData<String> value) {
        cachedValues.put(placeholder.countId(), value);
    }

    @Override
    public void setSharedValue(SharedPlaceholder placeholder, TimeStampData<String> value) {
        cachedValues.put(placeholder.countId(), value);
    }

    @Override
    public void setRelationalValue(RelationalPlaceholder placeholder, CNPlayer another, TimeStampData<String> value) {
        WeakHashMap<CNPlayer, TimeStampData<String>> map = cachedRelationalValues.computeIfAbsent(placeholder.countId(), k -> new WeakHashMap<>());
        map.put(another, value);
    }

//    @Override
//    public boolean setPlayerValue(PlayerPlaceholder placeholder, String value) {
//        TimeStampData<String> previous = cachedValues.get(placeholder.countId());
//        int currentTicks = MainTask.getTicks();
//        boolean changed = false;
//        if (previous != null) {
//            if (previous.ticks() == currentTicks) {
//                return false;
//            }
//            String data = previous.data();
//            if (!data.equals(value)) {
//                changed = true;
//                previous.data(value);
//                previous.updateTicks(true);
//            }
//        } else {
//            changed= true;
//            previous = new TimeStampData<>(value, currentTicks, true);
//            cachedValues.put(placeholder.countId(), previous);
//        }
//        return changed;
//    }

//    @Override
//    public boolean setRelationalValue(RelationalPlaceholder placeholder, CNPlayer another, String value) {
//        WeakHashMap<CNPlayer, TimeStampData<String>> map = cachedRelationalValues.computeIfAbsent(placeholder.countId(), k -> new WeakHashMap<>());
//        TimeStampData<String> previous = map.get(another);
//        int currentTicks = MainTask.getTicks();
//        boolean changed = false;
//        if (previous != null) {
//            if (previous.ticks() == currentTicks) {
//                return false;
//            }
//            String data = previous.data();
//            if (!data.equals(value)) {
//                changed = true;
//                previous.data(value);
//                previous.updateTicks(true);
//            }
//        } else {
//            changed= true;
//            previous = new TimeStampData<>(value, currentTicks, true);
//            map.put(another, previous);
//        }
//        return changed;
//    }

    @Override
    public @NotNull String getCachedSharedValue(SharedPlaceholder placeholder) {
        return updateSharedPlaceholder(placeholder);
    }

    @Override
    public @NotNull String getCachedPlayerValue(PlayerPlaceholder placeholder) {
        return updatePlayerPlaceholder(placeholder);
    }

    @Override
    public @NotNull String getCachedRelationalValue(RelationalPlaceholder placeholder, CNPlayer another) {
        return updateRelationalPlaceholder(placeholder, another);
    }

    @Nullable
    @Override
    public TimeStampData<String> getRawPlayerValue(PlayerPlaceholder placeholder) {
        return cachedValues.get(placeholder.countId());
    }

    @Nullable
    @Override
    public TimeStampData<String> getRawSharedValue(SharedPlaceholder placeholder) {
        return cachedValues.get(placeholder.countId());
    }

    @Nullable
    @Override
    public TimeStampData<String> getRawRelationalValue(RelationalPlaceholder placeholder, CNPlayer another) {
        WeakHashMap<CNPlayer, TimeStampData<String>> map = cachedRelationalValues.get(placeholder.countId());
        if (map == null) {
            return null;
        }
        return map.get(another);
    }

    @Override
    public Placeholder[] activePlaceholders() {
        Set<Placeholder> placeholders = new ObjectOpenHashSet<>();
        for (Feature feature : activeFeatures) {
            placeholders.addAll(feature.activePlaceholders());
        }
        return placeholders.toArray(new Placeholder[0]);
    }

    @Override
    public boolean isMet(Requirement[] requirements) {
        int currentTicks = MainTask.getTicks();
        for (Requirement requirement : requirements) {
            TimeStampData<Boolean> data = cachedRequirements.get(requirement.countId());
            if (data != null) {
                if (data.ticks() + requirement.refreshInterval() > currentTicks) {
                    if (!data.data()) {
                        return false;
                    }
                } else {
                    boolean satisfied = requirement.isSatisfied(this, this);
                    data.updateTicks(false);
                    data.data(satisfied);
                    if (!satisfied) {
                        return false;
                    }
                }
            } else {
                boolean satisfied = requirement.isSatisfied(this, this);
                data = new TimeStampData<>(satisfied, currentTicks, true);
                cachedRequirements.put(requirement.countId(), data);
                if (!satisfied) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean isMet(CNPlayer another, Requirement[] requirements) {
        int currentTicks = MainTask.getTicks();
        for (Requirement requirement : requirements) {
            WeakHashMap<CNPlayer, TimeStampData<Boolean>> innerMap = cachedRelationalRequirements.computeIfAbsent(requirement.countId(), k -> new WeakHashMap<>());
            TimeStampData<Boolean> data = innerMap.get(another);
            if (data != null) {
                if (data.ticks() + requirement.refreshInterval() > currentTicks) {
                    if (!data.data()) {
                        return false;
                    }
                } else {
                    boolean satisfied = requirement.isSatisfied(this, another);
                    data.updateTicks(false);
                    data.data(satisfied);
                    if (!satisfied) {
                        return false;
                    }
                }
            } else {
                boolean satisfied = requirement.isSatisfied(this, another);
                data = new TimeStampData<>(satisfied, currentTicks, true);
                innerMap.put(another, data);
                if (!satisfied) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Tracker addPlayerToTracker(CNPlayer another) {
        trackerLock.writeLock().lock();
        try {
            Tracker tracker = trackers.get(another);
            if (tracker != null) {
                return tracker;
            }
            tracker = new Tracker(another);
            trackers.put(another, tracker);
            return tracker;
        } finally {
            trackerLock.writeLock().unlock();
        }
    }

    @Override
    public void removePlayerFromTracker(CNPlayer another) {
        trackerLock.writeLock().lock();
        try {
            trackers.remove(another);
        } finally {
            trackerLock.writeLock().unlock();
        }
    }

    @Override
    public Collection<CNPlayer> nearbyPlayers() {
        trackerLock.readLock().lock();
        try {
            // Create a snapshot of keys to avoid concurrent modification
            return new ObjectArrayList<>(trackers.keySet());
        } finally {
            trackerLock.readLock().unlock();
        }
    }

    @Override
    public void trackPassengers(CNPlayer another, int... passengers) {
        trackerLock.writeLock().lock();
        try {
            Tracker tracker = trackers.get(another);
            if (tracker != null) {
                for (int passenger : passengers) {
                    tracker.addPassengerID(passenger);
                }
            }
        } finally {
            trackerLock.writeLock().unlock();
        }
    }

    @Override
    public void untrackPassengers(CNPlayer another, int... passengers) {
        trackerLock.writeLock().lock();
        try {
            Tracker tracker = trackers.get(another);
            if (tracker != null) {
                for (int passenger : passengers) {
                    tracker.removePassengerID(passenger);
                }
            }
        } finally {
            trackerLock.writeLock().unlock();
        }
    }

    @Override
    public Set<Integer> getTrackedPassengerIds(CNPlayer another) {
        trackerLock.readLock().lock();
        try {
            Tracker tracker = trackers.get(another);
            return tracker != null ? tracker.getPassengerIDs() : new ObjectOpenHashSet<>();
        } finally {
            trackerLock.readLock().unlock();
        }
    }

    @Override
    public Tracker getTracker(CNPlayer another) {
        trackerLock.readLock().lock();
        try {
            return trackers.get(another);
        } finally {
            trackerLock.readLock().unlock();
        }
    }

    @Override
    public String currentBubble() {
        if (currentNameplate == null) return "none";
        return currentBubble;
    }

    @Override
    public boolean setCurrentBubble(String bubble) {
        if (!isLoaded()) return false;
        this.currentBubble = bubble;
        return true;
    }

    @Override
    public String bubbleData() {
        if (bubbleData == null) return "none";
        return bubbleData;
    }

    @Override
    public boolean setBubbleData(String bubble) {
        if (!isLoaded()) return false;
        this.currentBubble = bubble;
        this.bubbleData = bubble;
        return true;
    }

    @Override
    public String currentNameplate() {
        if (currentNameplate == null) return "none";
        return currentNameplate;
    }

    @Override
    public boolean setCurrentNameplate(String nameplate) {
        if (!isLoaded()) return false;
        if (!nameplate.equals(this.currentNameplate)) {
            this.currentNameplate = nameplate;
        }
        return true;
    }

    @Override
    public String nameplateData() {
        return nameplateData;
    }

    @Override
    public boolean setNameplateData(String nameplate) {
        if (!isLoaded()) return false;
        this.nameplateData = nameplate;
        this.currentNameplate = nameplate;
        return true;
    }

    @Override
    public void save() {
        plugin.getStorageManager().dataSource().updatePlayerData(PlayerData.builder()
                .uuid(uuid())
                .nameplate(nameplateData())
                .bubble(bubbleData())
                .previewTags(isToggleablePreviewing())
                .build(), plugin.getScheduler().async());
    }

    @Override
    public TeamView teamView() {
        return teamView;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        AbstractCNPlayer that = (AbstractCNPlayer) object;
        return entityID() == that.entityID() && uuid().equals(that.uuid());
    }

    @Override
    public int hashCode() {
        return entityID();
    }

    @Override
    public String name() {
        return Optional.ofNullable(this.name).orElse("");
    }

    @Override
    public UUID uuid() {
        return this.uuid;
    }

    @Override
    public int entityID() {
        return this.entityId;
    }
}
