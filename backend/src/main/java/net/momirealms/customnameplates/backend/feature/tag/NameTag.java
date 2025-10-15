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
import net.momirealms.customnameplates.api.feature.CarouselText;
import net.momirealms.customnameplates.api.feature.DynamicText;
import net.momirealms.customnameplates.api.feature.RelationalFeature;
import net.momirealms.customnameplates.api.feature.tag.AbstractTag;
import net.momirealms.customnameplates.api.feature.tag.NameTagConfig;
import net.momirealms.customnameplates.api.feature.tag.TagRenderer;
import net.momirealms.customnameplates.api.helper.AdventureHelper;
import net.momirealms.customnameplates.api.network.Tracker;
import net.momirealms.customnameplates.api.placeholder.Placeholder;
import net.momirealms.customnameplates.api.util.Vector3;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class NameTag extends AbstractTag implements RelationalFeature {

    private final NameTagConfig config;
    private int order;
    private int timeLeft;
    private DynamicText currentText;

    public NameTag(CNPlayer owner, NameTagConfig config, TagRenderer renderer) {
        super(owner, renderer);
        this.config = config;
    }

    @Override
    public void init() {
        order = config.carouselTexts().length - 1;
        timeLeft = 0;
    }

    @Override
    protected List<Object> spawnPacket(CNPlayer viewer) {
        String newName = currentText.render(viewer);
        Object component = AdventureHelper.miniMessageToMinecraftComponent(newName);
        Tracker tracker = owner.getTracker(viewer);
        return CustomNameplates.getInstance().getPlatform().createTextDisplayPacket(
                entityID, uuid,
                owner.position().add(0,(1.8 + (affectedByCrouching() && tracker.isCrouching() && !owner.isFlying() ? -0.3 : 0) + renderer.hatOffset()) * (affectedByScaling() ? tracker.getScale() : 1),0),
                0f, 0f, 0d,
                0, 0, 0,
                component, config.backgroundColor(),
                (owner.isSpectator() && affectedBySpectator()) || (owner.isCrouching() && affectedByCrouching()) ? 64 : opacity(),
                config.hasShadow(), config.isSeeThrough().asBoolean() && (!affectedByCrouching() || !tracker.isCrouching()), config.useDefaultBackgroundColor(),
                config.alignment(), config.viewRange(), config.shadowRadius(), config.shadowStrength(),
                (affectedByScaling() ? scale(viewer).multiply(tracker.getScale()) : scale(viewer)),
                (affectedByScaling() ? translation(viewer).multiply(tracker.getScale()) : translation(viewer)),
                config.lineWidth(),
                (affectedByCrouching() && tracker.isCrouching())
        );
    }

    @Override
    public void darkTag(CNPlayer viewer, boolean dark) {
        Tracker tracker = owner.getTracker(viewer);
        boolean seeThrough = config.isSeeThrough().asBoolean() && (!affectedByCrouching() || !tracker.isCrouching());
        Consumer<List<Object>> modifiers = CustomNameplates.getInstance().getPlatform().createSneakModifier(dark, seeThrough, this.config);
        Object packet = CustomNameplates.getInstance().getPlatform().updateTextDisplayPacket(entityID, List.of(modifiers));
        CustomNameplates.getInstance().getPacketSender().sendPacket(viewer, packet);
    }

    @Override
    public void tick() {
        if (timeLeft > 0)
            timeLeft--;

        if (timeLeft == 0) {
            int triedTimes = 0;

            do {
                if (triedTimes == config.carouselTexts().length) {
                    timeLeft = 20;
                    currentText = null;
                    CustomNameplates.getInstance().getPluginLogger().warn("No text in order is available for player " + owner.name() + ". Please check your tag's conditions.");
                    return;
                }
                order++;
                if (order >= config.carouselTexts().length) {
                    order = 0;
                }
                triedTimes++;
            } while (
                    !owner.isMet(config.carouselTexts()[order].requirements())
            );

            CarouselText carouselText = config.carouselTexts()[order];
            timeLeft = carouselText.duration();
            currentText = carouselText.preParsedDynamicText().fastCreate(owner);

            if (carouselText.updateOnDisplay()) {
                owner.forceUpdatePlaceholders(currentText.placeholders(), owner.nearbyPlayers());
            }

            refresh();
        }
    }

    @Override
    public void notifyPlaceholderUpdates(CNPlayer p1, CNPlayer p2, boolean force) {
        refresh(p2);
    }

    @Override
    public void notifyPlaceholderUpdates(CNPlayer p1, boolean force) {
        refresh();
    }

    @Override
    public boolean canShow() {
        return owner.isMet(config.ownerRequirements());
    }

    @Override
    public boolean canShow(CNPlayer viewer) {
        return viewer.isMet(owner, config.viewerRequirements());
    }

    public void refresh() {
        if (!isShown()) return;
        for (CNPlayer viewer : viewerArray) {
            refresh(viewer);
        }
    }

    public void refresh(CNPlayer viewer) {
        if (isShown(viewer)) {
            String newName = currentText.render(viewer);
            Object component = AdventureHelper.miniMessageToMinecraftComponent(newName);
            Object packet = CustomNameplates.getInstance().getPlatform().updateTextDisplayPacket(entityID, List.of(CustomNameplates.getInstance().getPlatform().createTextComponentModifier(component)));
            CustomNameplates.getInstance().getPacketSender().sendPacket(viewer, packet);
        }
    }

    @Override
    public double getTextHeight(CNPlayer viewer) {
        String current = currentText.render(viewer);
        Tracker tracker = owner.getTracker(viewer);
        int lines = CustomNameplates.getInstance().getAdvanceManager().getLines(current, config.lineWidth());
        return ((lines * (9+1) + config.translation().y()) * config.scale().y() * (config.affectedByScaling() ? tracker.getScale() : 1)) / 40;
    }

    @Override
    public void hide() {
        if (!isShown()) return;
        Object packet = CustomNameplates.getInstance().getPlatform().removeEntityPacket(entityID);
        for (CNPlayer viewer : viewerArray) {
            owner.untrackPassengers(viewer, entityID);
            CustomNameplates.getInstance().getPacketSender().sendPacket(viewer, packet);
        }
        this.isShown = false;
        this.viewers.clear();
        resetViewerArray();
    }

    @Override
    public Vector3 scale(CNPlayer viewer) {
        return config.scale();
    }

    @Override
    public Vector3 translation(CNPlayer viewer) {
        return config.translation().add(0, renderer.hatOffset(), 0);
    }

    @Override
    public boolean relativeTranslation() {
        return false;
    }

    @Override
    public String name() {
        return "UnlimitedTag";
    }

    @Override
    public Set<Placeholder> activePlaceholders() {
        if (currentText == null || !isShown()) return Collections.emptySet();
        return currentText.placeholders();
    }

    @Override
    public Set<Placeholder> allPlaceholders() {
        Set<Placeholder> placeholders = new ObjectOpenHashSet<>();
        for (CarouselText text : config.carouselTexts()) {
            placeholders.addAll(text.preParsedDynamicText().placeholders());
        }
        return placeholders;
    }

    @Override
    public byte opacity() {
        return config.opacity();
    }

    @Override
    public String id() {
        return "nametag";
    }

    @Override
    public boolean affectedByCrouching() {
        return config.affectedByCrouching();
    }

    @Override
    public boolean affectedByScaling() {
        return config.affectedByScaling();
    }

    @Override
    public boolean affectedBySpectator() {
        return config.affectedBySpectator();
    }
}
