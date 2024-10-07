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

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.Feature;
import net.momirealms.customnameplates.api.feature.tag.NameTagConfig;
import net.momirealms.customnameplates.api.feature.tag.Tag;
import net.momirealms.customnameplates.api.feature.tag.TagRenderer;
import net.momirealms.customnameplates.api.feature.tag.UnlimitedTagManager;
import net.momirealms.customnameplates.api.network.Tracker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class TagRendererImpl implements TagRenderer {

    private final CNPlayer owner;
    private final UnlimitedTagManager manager;
    private Tag[] tags;
    private double hatOffset;

    public TagRendererImpl(UnlimitedTagManager manager, CNPlayer owner) {
        this.owner = owner;
        this.manager = manager;
        List<NameTag> senderList = new ArrayList<>();
        for (NameTagConfig config : manager.nameTagConfigs()) {
            NameTag sender = new NameTag(owner, config, this);
            senderList.add(sender);
            this.owner.addFeature(sender);
        }
        this.tags = senderList.toArray(new Tag[0]);
    }

    @Override
    public double hatOffset() {
        return hatOffset;
    }

    @Override
    public void hatOffset(double hatOffset) {
        if (hatOffset != this.hatOffset) {
            this.hatOffset = hatOffset;
            for (Tag tag : tags) {
                tag.updateTranslation();
            }
        }
    }

    @Override
    public void onTick() {
        HashSet<CNPlayer> playersToUpdatePassengers = new HashSet<>();
        for (Tag display : tags) {
            boolean canShow = display.canShow();
            if (canShow) {
                if (display.isShown()) {
                    for (CNPlayer nearby : owner.nearbyPlayers()) {
                        if (display.isShown(nearby)) {
                            if (!display.canShow(nearby)) {
                                display.hide(nearby);
                            }
                        } else {
                            if (display.canShow(nearby)) {
                                display.show(nearby);
                                playersToUpdatePassengers.add(nearby);
                            }
                        }
                    }
                    display.tick();
                } else {
                    display.init();
                    display.tick();
                    display.show();
                    for (CNPlayer nearby : owner.nearbyPlayers()) {
                        if (display.canShow(nearby) && !display.isShown(nearby)) {
                            display.show(nearby);
                            playersToUpdatePassengers.add(nearby);
                        }
                    }
                }
            } else {
                if (display.isShown()) {
                    display.hide();
                }
            }
        }

        // Update passengers
        Set<Integer> realPassengers = owner.passengers();
        for (CNPlayer nearby : playersToUpdatePassengers) {
            updatePassengers(nearby, realPassengers);
        }
    }

    @Override
    public void destroy() {
        for (Tag tag : this.tags) {
            tag.hide();
            if (tag instanceof Feature feature) {
                this.owner.removeFeature(feature);
            }
        }
    }

    public void handlePlayerRemove(CNPlayer another) {
        for (Tag display : this.tags) {
            if (display.isShown()) {
                if (display.isShown(another)) {
                    display.hide(another);
                }
            }
        }
    }

    @Override
    public void addTag(Tag tag) {
        Tag[] newTags = new Tag[this.tags.length + 1];
        System.arraycopy(this.tags, 0, newTags, 0, this.tags.length);
        newTags[this.tags.length] = tag;
        this.tags = newTags;
        if (tag instanceof Feature feature) {
            this.owner.addFeature(feature);
        }
    }

    @Override
    public Tag[] tags() {
        return this.tags;
    }

    @Override
    public int removeTagIf(Predicate<Tag> predicate) {
        Set<Integer> removedIndexes = new HashSet<>();
        for (int i = 0; i < this.tags.length; i++) {
            if (predicate.test(this.tags[i])) {
                removedIndexes.add(i);
                this.tags[i].hide();
                if (this.tags[i] instanceof Feature feature) {
                    this.owner.removeFeature(feature);
                }
            }
        }
        if (removedIndexes.isEmpty()) {
            return 0;
        }
        Tag[] newTags = new Tag[this.tags.length - removedIndexes.size()];
        int newIndex = 0;
        for (int i = 0; i < this.tags.length; i++) {
            if (!removedIndexes.contains(i)) {
                newTags[newIndex++] = this.tags[i];
            }
        }
        this.tags = newTags;
        return removedIndexes.size();
    }

    @Override
    public int tagIndex(Tag tag) {
        for (int i = 0; i < this.tags.length; i++) {
            if (this.tags[i].equals(tag)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void addTag(Tag tag, int index) {
        if (index < 0 || index > this.tags.length) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        Tag[] newTags = new Tag[this.tags.length + 1];
        System.arraycopy(this.tags, 0, newTags, 0, index);
        newTags[index] = tag;
        System.arraycopy(this.tags, index, newTags, index + 1, this.tags.length - index);
        this.tags = newTags;
        tag.show();
        if (tag instanceof Feature feature) {
            this.owner.addFeature(feature);
        }
    }

    @Override
    public void removeTag(Tag tag) {
        int i = 0;
        boolean has = false;
        for (Tag display : this.tags) {
            if (display == tag) {
                has = true;
                break;
            }
            i++;
        }
        if (has) {
            Tag[] newTags = new Tag[this.tags.length - 1];
            System.arraycopy(this.tags, 0, newTags, 0, i);
            System.arraycopy(this.tags, i + 1, newTags, i, (this.tags.length - i) - 1);
            this.tags = newTags;
            tag.hide();
            if (tag instanceof Feature feature) {
                this.owner.removeFeature(feature);
            }
        }
    }

    public void handlePlayerAdd(CNPlayer another) {
        boolean updatePassengers = false;
        for (Tag display : this.tags) {
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
            Set<Integer> realPassengers = owner.passengers();
            updatePassengers(another, realPassengers);
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
        CustomNameplates.getInstance().getPacketSender().sendPacket(another, packet);
    }

    public void handleEntityDataChange(CNPlayer another, boolean isCrouching) {
        Tracker tracker = owner.getTracker(another);
        // should never be null
        if (tracker == null) return;
        tracker.setCrouching(isCrouching);
        for (Tag display : this.tags) {
            if (display.affectedByCrouching()) {
                if (display.isShown()) {
                    if (display.isShown(another)) {
                        display.onOpacityChange(another, isCrouching || tracker.isSpectator());
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
        for (Tag display : this.tags) {
            if (display.affectedByScaling()) {
                if (display.isShown()) {
                    if (display.isShown(another)) {
                        display.onPlayerScaleUpdate(another, scale);
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
        for (Tag display : this.tags) {
            if (display.isShown()) {
                if (display.isShown(another)) {
                    display.onOpacityChange(another, isSpectator || tracker.isCrouching());
                }
            }
        }
    }
}
