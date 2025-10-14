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

package net.momirealms.customnameplates.api.feature.tag;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.network.Tracker;
import net.momirealms.customnameplates.api.util.SelfIncreaseEntityID;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.function.Consumer;

/**
 * Represents an abstract tag for a {@link CNPlayer} that can be shown to specific viewers.
 * This class handles the rendering of the tag, showing and hiding it for specific players,
 * and managing tag attributes such as opacity and scale.
 */
public abstract class AbstractTag implements Tag {
    /**
     * The owner of this tag
     */
    protected CNPlayer owner;
    /**
     * The {@link TagRenderer} responsible for rendering this tag.
     */
    protected TagRenderer renderer;
    /**
     * The unique UUID assigned to this tag.
     */
    protected final UUID uuid = UUID.randomUUID();
    /**
     * The unique entity ID assigned to this tag, used to track visibility and actions.
     */
    protected final int entityID = SelfIncreaseEntityID.getAndIncrease();
    /**
     * A flag indicating whether the tag is currently visible to viewers.
     */
    protected boolean isShown = false;
    /**
     * A list of players who are currently viewing this tag.
     */
    protected final Vector<CNPlayer> viewers = new Vector<>();
    /**
     * An array representation of the viewers, used for optimization.
     */
    protected CNPlayer[] viewerArray = new CNPlayer[0];

    /**
     * Constructs a new {@link AbstractTag} for the given {@link CNPlayer} owner and
     * {@link TagRenderer}.
     *
     * @param owner the {@link CNPlayer} who owns this tag
     * @param renderer the {@link TagRenderer} used to render this tag
     */
    protected AbstractTag(CNPlayer owner, TagRenderer renderer) {
        this.owner = owner;
        this.renderer = renderer;
    }

    /**
     * Spawns the tag for the given viewer by sending a packet to the viewer.
     *
     * @param viewer the {@link CNPlayer} who will see the tag
     * @return a list of objects representing the spawn packet for the tag
     */
    protected abstract List<Object> spawnPacket(CNPlayer viewer);

    @Override
    public void tick() {
    }

    @Override
    public void init() {
    }

    @Override
    public boolean canShow() {
        return true;
    }

    @Override
    public boolean canShow(CNPlayer viewer) {
        return true;
    }

    @Override
    public void show() {
        if (isShown) return;
        this.isShown = true;
        for (CNPlayer viewer : viewerArray) {
            show(viewer);
        }
    }

    @Override
    public void show(CNPlayer viewer) {
        if (!renderer.isValid()) return;
        if (!isShown()) throw new IllegalStateException("This tag is currently hidden");
        viewers.add(viewer);
        resetViewerArray();
        owner.trackPassengers(viewer, entityID);
        CustomNameplates.getInstance().getPacketSender().sendPacket(viewer, spawnPacket(viewer));
    }

    @Override
    public void hide() {
        if (!isShown()) return;
        for (CNPlayer viewer : viewerArray) {
            hide(viewer);
        }
        this.isShown = false;
    }

    @Override
    public void hide(CNPlayer viewer) {
        if (!isShown()) return;
        boolean removed = viewers.remove(viewer);
        if (!removed) return;
        resetViewerArray();
        owner.untrackPassengers(viewer, entityID);
        Object packet = CustomNameplates.getInstance().getPlatform().removeEntityPacket(entityID);
        CustomNameplates.getInstance().getPacketSender().sendPacket(viewer, packet);
    }

    @Override
    public void respawn(CNPlayer viewer) {
        ArrayList<Object> packets = new ArrayList<>();
        packets.add(CustomNameplates.getInstance().getPlatform().removeEntityPacket(entityID));
        packets.addAll(spawnPacket(viewer));
        CustomNameplates.getInstance().getPacketSender().sendPacket(viewer, packets);
    }

    @Override
    public void respawn() {
        for (CNPlayer viewer : viewerArray) {
            respawn(viewer);
        }
    }

    @Override
    public void darkTag(boolean dark) {
        for (CNPlayer viewer : viewerArray) {
            darkTag(viewer, dark);
        }
    }

    @Override
    public void onPlayerScaleUpdate(double scale) {
        for (CNPlayer viewer : viewerArray) {
            onPlayerScaleUpdate(viewer, scale);
        }
    }

    @Override
    public void onPlayerScaleUpdate(CNPlayer viewer, double scale) {
        Consumer<List<Object>> modifier1 = CustomNameplates.getInstance().getPlatform().createScaleModifier(scale(viewer).multiply(scale));
        Consumer<List<Object>> modifier2 = CustomNameplates.getInstance().getPlatform().createTranslationModifier(translation(viewer).multiply(scale));
        Object packet = CustomNameplates.getInstance().getPlatform().updateTextDisplayPacket(entityID, List.of(modifier1, modifier2));
        CustomNameplates.getInstance().getPacketSender().sendPacket(viewer, packet);
    }

    @Override
    public void updateTranslation() {
        for (CNPlayer player : viewerArray) {
            updateTranslation(player);
        }
    }

    @Override
    public void updateTranslation(CNPlayer viewer) {
        Tracker tracker = owner.getTracker(viewer);
        if (tracker != null) {
            Consumer<List<Object>> modifier = CustomNameplates.getInstance().getPlatform().createTranslationModifier(translation(viewer).multiply(tracker.getScale()));
            Object packet = CustomNameplates.getInstance().getPlatform().updateTextDisplayPacket(entityID, List.of(modifier));
            CustomNameplates.getInstance().getPacketSender().sendPacket(viewer, packet);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTag that = (AbstractTag) o;
        return owner == that.owner && uuid == that.uuid;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public int entityID() {
        return entityID;
    }

    @Override
    public UUID uuid() {
        return uuid;
    }

    /**
     * Resets the viewer array
     */
    protected void resetViewerArray() {
        this.viewerArray = viewers.toArray(new CNPlayer[0]);
    }

    @Override
    public boolean isShown() {
        return isShown;
    }

    @Override
    public boolean isShown(CNPlayer another) {
        if (!isShown) return false;
        return viewers.contains(another);
    }

    @Override
    public byte opacity() {
        return -1;
    }
}
