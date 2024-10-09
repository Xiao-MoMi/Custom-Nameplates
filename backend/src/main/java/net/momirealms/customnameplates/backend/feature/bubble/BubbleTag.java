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

package net.momirealms.customnameplates.backend.feature.bubble;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.bubble.BubbleConfig;
import net.momirealms.customnameplates.api.feature.bubble.BubbleManager;
import net.momirealms.customnameplates.api.feature.tag.AbstractTag;
import net.momirealms.customnameplates.api.feature.tag.Tag;
import net.momirealms.customnameplates.api.feature.tag.TagRenderer;
import net.momirealms.customnameplates.api.network.Tracker;
import net.momirealms.customnameplates.api.util.Alignment;
import net.momirealms.customnameplates.api.util.SelfIncreaseEntityID;
import net.momirealms.customnameplates.api.util.Vector3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class BubbleTag extends AbstractTag {

    private final Object text;
    @Nullable
    private final Object background;
    private final BubbleManager manager;
    private final BubbleConfig bubbleConfig;
    private int ticker;
    private boolean canShow;
    private final String channel;
    private final HashMap<UUID, Boolean> cachedVisibility = new HashMap<>();

    protected final int subEntityID = SelfIncreaseEntityID.getAndIncrease();
    protected final UUID subEntityUUID = UUID.randomUUID();

    public BubbleTag(CNPlayer owner, TagRenderer renderer, String channel, BubbleConfig bubbleConfig, Object text, @Nullable Object background, BubbleManager bubbleManager) {
        super(owner, renderer);
        this.text = text;
        this.manager = bubbleManager;
        this.bubbleConfig = bubbleConfig;
        this.channel = channel;
        this.background = background;
    }

    @Override
    protected List<Object> spawnPacket(CNPlayer viewer) {
        Tracker tracker = owner.getTracker(viewer);
        Vector3 translation = translation(viewer);
        List<Object> packets = new ArrayList<>(CustomNameplates.getInstance().getPlatform().createTextDisplayPacket(
                entityID, uuid,
                owner.position().add(0, (1.8 + (affectedByCrouching() && tracker.isCrouching() && !owner.isFlying() ? -0.3 : 0) + renderer.hatOffset()) * (affectedByScaling() ? tracker.getScale() : 1), 0),
                0f, 0f, 0d,
                -1, 0, 0,
                text, bubbleConfig.backgroundColor(), (byte) -1, false, false, false,
                Alignment.CENTER, manager.viewRange(), 0.0f, 1.0f,
                new Vector3(0.001, 0.001, 0.001),
                affectedByScaling() ? translation.multiply(tracker.getScale()).add(0.01, 0, 0.01) : translation,
                bubbleConfig.lineWidth(),
                (affectedByCrouching() && tracker.isCrouching())
        ));
        if (background != null) {
            packets.addAll(CustomNameplates.getInstance().getPlatform().createTextDisplayPacket(
                    subEntityID, subEntityUUID,
                    owner.position().add(0,(1.8 + (affectedByCrouching() && tracker.isCrouching() && !owner.isFlying() ? -0.3 : 0) + renderer.hatOffset()) * (affectedByScaling() ? tracker.getScale() : 1),0),
                    0f, 0f, 0d,
                    -1, 0, 0,
                    background, 0, (byte) -1, false, false, false,
                    Alignment.CENTER, manager.viewRange(), 0.0f, 1.0f,
                    new Vector3(0.001, 0.001, 0.001),
                    affectedByScaling() ? translation.multiply(tracker.getScale()) : translation,
                    2048,
                    (affectedByCrouching() && tracker.isCrouching())
            ));
        }
        return packets;
    }

    public void setCanShow(boolean canShow) {
        this.canShow = canShow;
    }

    @Override
    public boolean canShow() {
        return canShow;
    }

    @Override
    public void darkTag(CNPlayer viewer, boolean dark) {
    }

    @Override
    public boolean canShow(CNPlayer viewer) {
        if (!viewer.isMet(owner, manager.viewBubbleRequirements())) {
            return false;
        }
        switch (manager.channelMode()) {
            case ALL -> {
                return true;
            }
            case JOINED -> {
                Boolean previous = cachedVisibility.get(viewer.uuid());
                if (previous != null) return previous;
                boolean can = CustomNameplates.getInstance().getChatManager().chatProvider().hasJoinedChannel(viewer, channel);
                cachedVisibility.put(viewer.uuid(), can);
                return can;
            }
            case CAN_JOIN -> {
                Boolean previous = cachedVisibility.get(viewer.uuid());
                if (previous != null) return previous;
                boolean can = CustomNameplates.getInstance().getChatManager().chatProvider().canJoinChannel(viewer, channel);
                cachedVisibility.put(viewer.uuid(), can);
                return can;
            }
        }
        return true;
    }

    @Override
    public void show(CNPlayer viewer) {
        if (!isShown()) return;
        viewers.add(viewer);
        resetViewerArray();
        owner.trackPassengers(viewer, entityID);
        if (background != null)
            owner.trackPassengers(viewer, subEntityID);
        CustomNameplates.getInstance().getPacketSender().sendPacket(viewer, spawnPacket(viewer));
        Tracker tracker = owner.getTracker(viewer);
        CustomNameplates.getInstance().getScheduler().asyncLater(() -> {
            Consumer<List<Object>> modifier0 = CustomNameplates.getInstance().getPlatform().createInterpolationDelayModifier(-1);
            Consumer<List<Object>> modifier1 = CustomNameplates.getInstance().getPlatform().createTransformationInterpolationDurationModifier(manager.appearDuration());
            Consumer<List<Object>> modifier2 = CustomNameplates.getInstance().getPlatform().createScaleModifier(affectedByScaling() ? bubbleConfig.scale().multiply(tracker.getScale()) : bubbleConfig.scale());
            Object packet1 = CustomNameplates.getInstance().getPlatform().updateTextDisplayPacket(entityID, List.of(modifier0, modifier1, modifier2));
            if (background != null) {
                Object packet2 = CustomNameplates.getInstance().getPlatform().updateTextDisplayPacket(subEntityID, List.of(modifier0, modifier1, modifier2));
                CustomNameplates.getInstance().getPacketSender().sendPacket(viewer, List.of(packet1, packet2));
            } else {
                CustomNameplates.getInstance().getPacketSender().sendPacket(viewer, packet1);
            }
        }, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public void tick() {
        if (!canShow) return;
        if (ticker >= manager.stayDuration()) {
            renderer.removeTag(this);
            return;
        }
        ticker++;
    }

    @Override
    public void onPlayerScaleUpdate(CNPlayer viewer, double scale) {
        Consumer<List<Object>> modifier1 = CustomNameplates.getInstance().getPlatform().createScaleModifier(scale(viewer).multiply(scale));
        Vector3 translation = translation(viewer);
        Consumer<List<Object>> modifier2 = CustomNameplates.getInstance().getPlatform().createTranslationModifier(translation.multiply(scale).add(0.01,0,0.01));
        Object packet1 = CustomNameplates.getInstance().getPlatform().updateTextDisplayPacket(entityID, List.of(modifier1, modifier2));
        if (background != null) {
            Consumer<List<Object>> modifier3 = CustomNameplates.getInstance().getPlatform().createTranslationModifier(translation.multiply(scale));
            Object packet2 = CustomNameplates.getInstance().getPlatform().updateTextDisplayPacket(subEntityID, List.of(modifier1, modifier3));
            CustomNameplates.getInstance().getPacketSender().sendPacket(viewer, List.of(packet1, packet2));
        } else {
            CustomNameplates.getInstance().getPacketSender().sendPacket(viewer, packet1);
        }
    }

    @Override
    public void updateTranslation(CNPlayer viewer) {
        Tracker tracker = owner.getTracker(viewer);
        if (tracker != null) {
            Vector3 translation = translation(viewer);
            Consumer<List<Object>> modifier1 = CustomNameplates.getInstance().getPlatform().createTranslationModifier(translation.multiply(tracker.getScale()).add(0.01,0,0.01));
            Object packet1 = CustomNameplates.getInstance().getPlatform().updateTextDisplayPacket(entityID, List.of(modifier1));
            if (background != null) {
                Consumer<List<Object>> modifier2 = CustomNameplates.getInstance().getPlatform().createTranslationModifier(translation.multiply(tracker.getScale()));
                Object packet2 = CustomNameplates.getInstance().getPlatform().updateTextDisplayPacket(subEntityID, List.of(modifier2));
                CustomNameplates.getInstance().getPacketSender().sendPacket(viewer, List.of(packet1, packet2));
            } else {
                CustomNameplates.getInstance().getPacketSender().sendPacket(viewer, packet1);
            }
        }
    }

    @Override
    public void hide() {
        if (!isShown()) return;

        CNPlayer[] viewers = viewerArray.clone();
        CustomNameplates.getInstance().getScheduler().asyncLater(() -> {
            Object removePacket = createRemovePacket();
            for (CNPlayer viewer : viewers) {
                CustomNameplates.getInstance().getPacketSender().sendPacket(viewer, removePacket);
            }
        }, manager.disappearDuration() * 50L, TimeUnit.MILLISECONDS);

        List<Object> disappearPacket = createDisappearPacket();
        for (CNPlayer viewer : viewers) {
            if (background != null) {
                owner.untrackPassengers(viewer, entityID, subEntityID);
            } else {
                owner.untrackPassengers(viewer, entityID);
            }
            CustomNameplates.getInstance().getPacketSender().sendPacket(viewer, disappearPacket);
        }

        this.cachedVisibility.clear();
        this.isShown = false;
        this.viewers.clear();
        resetViewerArray();
    }

    @Override
    public void hide(CNPlayer viewer) {
        if (!isShown()) return;
        boolean removed = viewers.remove(viewer);
        if (!removed) return;
        resetViewerArray();
        if (background != null) {
            owner.untrackPassengers(viewer, entityID, subEntityID);
        } else {
            owner.untrackPassengers(viewer, entityID);
        }
        CustomNameplates.getInstance().getPacketSender().sendPacket(viewer, createDisappearPacket());
    }

    private Object createRemovePacket() {
        Object packet;
        if (background != null) {
            packet = CustomNameplates.getInstance().getPlatform().removeEntityPacket(entityID, subEntityID);
        } else {
            packet = CustomNameplates.getInstance().getPlatform().removeEntityPacket(entityID);
        }
        return packet;
    }

    private List<Object> createDisappearPacket() {
        Consumer<List<Object>> modifier0 = CustomNameplates.getInstance().getPlatform().createInterpolationDelayModifier(-1);
        Consumer<List<Object>> modifier1 = CustomNameplates.getInstance().getPlatform().createTransformationInterpolationDurationModifier(manager.disappearDuration());
        Consumer<List<Object>> modifier2 = CustomNameplates.getInstance().getPlatform().createScaleModifier(new Vector3(0.001,0.001,0.001));
        if (background != null) {
            return List.of(CustomNameplates.getInstance().getPlatform().updateTextDisplayPacket(entityID, List.of(modifier0, modifier1, modifier2)), CustomNameplates.getInstance().getPlatform().updateTextDisplayPacket(subEntityID, List.of(modifier0, modifier1, modifier2)));
        } else {
            return List.of(CustomNameplates.getInstance().getPlatform().updateTextDisplayPacket(entityID, List.of(modifier0, modifier1, modifier2)));
        }
    }

    @Override
    public double getTextHeight(CNPlayer viewer) {
        return -Double.MAX_VALUE;
    }

    @Override
    public Vector3 scale(CNPlayer viewer) {
        return new Vector3(1,1,1);
    }

    @Override
    public Vector3 translation(CNPlayer viewer) {
        return new Vector3(0, manager.verticalOffset() + maxY(viewer), 0);
    }

    @Override
    public boolean relativeTranslation() {
        return true;
    }

    public double maxY(CNPlayer viewer) {
        double y = 0;
        for (Tag tag : renderer.tags()) {
            if (tag.isShown() && tag.isShown(viewer) && !tag.id().equals(id())) {
                double currentY = tag.translation(viewer).y() + tag.getTextHeight(viewer);
                if (currentY > y) {
                    y = currentY;
                }
            }
        }
        return y;
    }

    @Override
    public String id() {
        return "bubble";
    }

    @Override
    public boolean affectedByCrouching() {
        return false;
    }

    @Override
    public boolean affectedByScaling() {
        return true;
    }

    @Override
    public boolean affectedBySpectator() {
        return false;
    }
}
