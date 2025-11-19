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

package net.momirealms.customnameplates.backend.feature.tag;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.Feature;
import net.momirealms.customnameplates.api.feature.tag.NameTagConfig;
import net.momirealms.customnameplates.api.feature.tag.Tag;
import net.momirealms.customnameplates.api.feature.tag.TagRenderer;
import net.momirealms.customnameplates.api.feature.tag.UnlimitedTagManager;
import net.momirealms.customnameplates.api.helper.VersionHelper;
import net.momirealms.customnameplates.api.network.Tracker;

import java.util.*;
import java.util.function.Predicate;

public class TagRendererImpl implements TagRenderer {
    private final CNPlayer owner;
    private final UnlimitedTagManager manager;
    private final Vector<Tag> tagVector;
    private Tag[] tagArray;
    private Tag[] rTagsArray;
    private double hatOffset;
    private boolean valid = true;
    private Set<Integer> cachedPassengers = Set.of();

    public TagRendererImpl(UnlimitedTagManager manager, CNPlayer owner) {
        this.owner = owner;
        this.manager = manager;
        List<NameTag> tagList = new ArrayList<>();
        for (NameTagConfig config : manager.nameTagConfigs()) {
            NameTag sender = new NameTag(owner, config, this);
            tagList.add(sender);
            this.owner.addFeature(sender);
        }
        this.tagVector = new Vector<>(tagList);
        this.resetTagArray();
        this.rTagsArray = new Tag[0];
    }

    @Override
    public double hatOffset() {
        return hatOffset;
    }

    @Override
    public void hatOffset(double hatOffset) {
        if (hatOffset != this.hatOffset) {
            this.hatOffset = hatOffset;
            for (Tag tag : tagArray) {
                tag.updateTranslation();
            }
        }
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public synchronized void onTick() {
        if (!isValid()) return;

        Set<CNPlayer> playersToUpdatePassengers = new ObjectOpenHashSet<>();
        Set<CNPlayer> tagTranslationUpdates = new ObjectOpenHashSet<>();

        Collection<CNPlayer> nearbyPlayers = owner.nearbyPlayers();
        for (Tag tag : tagArray) {
            boolean canShow = tag.canShow();
            if (canShow) {
                if (tag.isShown()) {
                    for (CNPlayer nearby : nearbyPlayers) {
                        if (tag.isShown(nearby)) {
                            if (!tag.canShow(nearby)) {
                                tag.hide(nearby);
                                if (!tag.relativeTranslation())
                                    tagTranslationUpdates.add(nearby);
                            }
                        } else {
                            if (tag.canShow(nearby)) {
                                tag.show(nearby);
                                playersToUpdatePassengers.add(nearby);
                                if (!tag.relativeTranslation())
                                    tagTranslationUpdates.add(nearby);
                            }
                        }
                    }
                    tag.tick();
                } else {
                    tag.init();
                    tag.tick();
                    tag.show();
                    for (CNPlayer nearby : nearbyPlayers) {
                        if (tag.canShow(nearby) && !tag.isShown(nearby)) {
                            tag.show(nearby);
                            playersToUpdatePassengers.add(nearby);
                            if (!tag.relativeTranslation())
                                tagTranslationUpdates.add(nearby);
                        }
                    }
                }
            } else {
                if (tag.isShown()) {
                    tag.hide();
                    if (!tag.relativeTranslation())
                        tagTranslationUpdates.addAll(nearbyPlayers);
                }
            }
        }

        // Update passengers
        this.cachedPassengers = owner.passengers();
        for (CNPlayer nearby : playersToUpdatePassengers) {
            updatePassengers(nearby, this.cachedPassengers);
        }

        // Update relative translation tags
        for (Tag tag : rTagsArray) {
            for (CNPlayer nearby : tagTranslationUpdates) {
                if (tag.isShown(nearby)) {
                    tag.updateTranslation(nearby);
                }
            }
        }
    }

    @Override
    public void destroy() {
        this.valid = false;
        for (Tag tag : this.tagArray) {
            tag.hide();
            if (tag instanceof Feature feature) {
                this.owner.removeFeature(feature);
            }
        }
    }

    public void handlePlayerRemove(CNPlayer another) {
        for (Tag display : this.tagArray) {
            if (display.isShown()) {
                if (display.isShown(another)) {
                    display.hide(another);
                }
            }
        }
    }

    private void resetRTagArray() {
        List<Tag> rTags = new ArrayList<>();
        for (Tag tag : this.tagArray) {
            if (tag.relativeTranslation()) rTags.add(tag);
        }
        this.rTagsArray = rTags.toArray(new Tag[0]);
    }

    private void resetTagArray() {
        this.tagArray = tagVector.toArray(new Tag[0]);
    }

    @Override
    public void addTag(Tag tag) {
        if (tagVector.contains(tag)) {
            return;
        }
        tagVector.add(tag);
        resetTagArray();
        if (tag instanceof Feature feature) {
            this.owner.addFeature(feature);
        }
        if (tag.relativeTranslation()) {
            resetRTagArray();
        }
    }

    @Override
    public void addTag(Tag tag, int index) {
        if (index < 0 || index > this.tagArray.length) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        tagVector.add(index, tag);
        resetTagArray();
        if (tag instanceof Feature feature) {
            this.owner.addFeature(feature);
        }
        if (tag.relativeTranslation()) {
            resetRTagArray();
        }
    }

    @Override
    public Tag[] tags() {
        return this.tagArray;
    }

    @Override
    public int removeTagIf(Predicate<Tag> predicate) {
        List<Integer> removedIndexes = new ArrayList<>();
        boolean hasRTag = false;
        for (int i = 0; i < this.tagArray.length; i++) {
            Tag tag = this.tagArray[i];
            if (predicate.test(tag)) {
                removedIndexes.add(i);
                tag.hide();
                if (tag instanceof Feature feature) {
                    this.owner.removeFeature(feature);
                }
                if (tag.relativeTranslation()) {
                    hasRTag = true;
                }
            }
        }
        if (removedIndexes.isEmpty()) {
            return 0;
        }
        Collections.reverse(removedIndexes);
        for (int index : removedIndexes) {
            this.tagVector.remove(index);
        }
        resetTagArray();
        if (hasRTag) {
            resetRTagArray();
        }
        return removedIndexes.size();
    }

    @Override
    public int tagIndex(Tag tag) {
        for (int i = 0; i < this.tagArray.length; i++) {
            if (this.tagArray[i] == tag) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void removeTag(Tag tag) {
        int i = 0;
        boolean has = false;
        for (Tag display : this.tagArray) {
            if (display == tag) {
                has = true;
                break;
            }
            i++;
        }
        if (has) {
            this.tagVector.remove(tag);
            resetTagArray();
            tag.hide();
            if (tag instanceof Feature feature) {
                this.owner.removeFeature(feature);
            }
            if (tag.relativeTranslation()) {
                resetRTagArray();
            }
        }
    }

    public void handlePlayerAdd(CNPlayer another) {
        boolean updatePassengers = false;
        for (Tag display : this.tagArray) {
            if (display.isShown()) {
                if (!display.isShown(another)) {
                    if (display.canShow(another)) {
                        display.show(another);
                        updatePassengers = true;
                    }
                }
            }
        }
        if (updatePassengers) {
            updatePassengers(another, this.cachedPassengers);
        }
    }

    private void updatePassengers(CNPlayer another, Set<Integer> realPassengers) {
        Set<Integer> fakePassengers = owner.getTrackedPassengerIds(another);
        fakePassengers.addAll(realPassengers);
        int[] passengers = new int[fakePassengers.size()];
        int index = 0;
        for (int passenger : fakePassengers) {
            passengers[index++] = passenger;
        }
        Object packet = CustomNameplates.getInstance().getPlatform().setPassengersPacket(owner.entityID(), passengers);
        if (VersionHelper.isPaperOrItsForks()) {
            CustomNameplates.getInstance().getPacketSender().sendPacket(another, packet);
        } else {
            CustomNameplates.getInstance().getScheduler().sync().runLater(() -> {
                CustomNameplates.getInstance().getPacketSender().sendPacket(another, packet);
            }, 0, null);
        }
    }

    public void handleEntityDataChange(CNPlayer another, boolean isCrouching) {
        Tracker tracker = owner.getTracker(another);
        // should never be null
        if (tracker == null) return;
        tracker.setCrouching(isCrouching);
        for (Tag display : this.tagArray) {
            if (display.affectedByCrouching()) {
                if (display.isShown()) {
                    if (display.isShown(another)) {
                        display.darkTag(another, isCrouching || (display.affectedBySpectator() && tracker.isSpectator()));
                    }
                }
            }
        }
    }

    public void handleGameModeChange(CNPlayer another, boolean isSpectator) {
        Tracker tracker = owner.getTracker(another);
        // can be null
        if (tracker == null) return;
        tracker.setSpectator(isSpectator);
        for (Tag display : this.tagArray) {
            if (display.affectedBySpectator()) {
                if (display.isShown()) {
                    if (display.isShown(another)) {
                        display.darkTag(another, isSpectator || (display.affectedByCrouching() && tracker.isCrouching()));
                    }
                }
            }
        }
    }

    public void handleAttributeChange(CNPlayer another, double scale) {
        Tracker tracker = owner.getTracker(another);
        // should never be null
        if (tracker == null) return;
        tracker.setScale(scale);
        for (Tag display : this.tagArray) {
            if (display.affectedByScaling()) {
                if (display.isShown()) {
                    if (display.isShown(another)) {
                        display.onPlayerScaleUpdate(another, scale);
                    }
                }
            }
        }
    }
}
