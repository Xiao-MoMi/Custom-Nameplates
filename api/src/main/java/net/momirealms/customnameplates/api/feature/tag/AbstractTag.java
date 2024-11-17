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

public abstract class AbstractTag implements Tag {

    protected CNPlayer owner;
    protected TagRenderer renderer;
    protected final UUID uuid = UUID.randomUUID();
    protected final int entityID = SelfIncreaseEntityID.getAndIncrease();

    protected boolean isShown = false;

    protected final Vector<CNPlayer> viewers = new Vector<>();
    protected CNPlayer[] viewerArray = new CNPlayer[0];

    public AbstractTag(CNPlayer owner, TagRenderer renderer) {
        this.owner = owner;
        this.renderer = renderer;
    }

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
    public void darkTag(CNPlayer viewer, boolean dark) {
        Consumer<List<Object>> modifiers = CustomNameplates.getInstance().getPlatform().createOpacityModifier(dark ? 64 : opacity());
        Object packet = CustomNameplates.getInstance().getPlatform().updateTextDisplayPacket(entityID, List.of(modifiers));
        CustomNameplates.getInstance().getPacketSender().sendPacket(viewer, packet);
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
