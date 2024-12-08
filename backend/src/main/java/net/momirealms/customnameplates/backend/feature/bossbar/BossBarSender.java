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

package net.momirealms.customnameplates.backend.feature.bossbar;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.feature.CarouselText;
import net.momirealms.customnameplates.api.feature.DynamicText;
import net.momirealms.customnameplates.api.feature.Feature;
import net.momirealms.customnameplates.api.feature.bossbar.BossBar;
import net.momirealms.customnameplates.api.feature.bossbar.BossBarConfig;
import net.momirealms.customnameplates.api.helper.AdventureHelper;
import net.momirealms.customnameplates.api.placeholder.Placeholder;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class BossBarSender implements Feature, BossBar {

    private final UUID uuid = UUID.randomUUID();
    private final CNPlayer owner;

    private boolean isShown = false;
    private final BossBarConfig config;

    private int order;
    private int timeLeft;
    private DynamicText currentBossBar;

    private String latestContent;

    public BossBarSender(CNPlayer owner, BossBarConfig config) {
        this.owner = owner;
        this.config = config;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BossBarSender that = (BossBarSender) o;
        return owner == that.owner && uuid == that.uuid;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public UUID uuid() {
        return uuid;
    }

    @Override
    public String name() {
        return "BossBarSender";
    }

    @Override
    public float progress() {
        return config.progress();
    }

    @Override
    public Color color() {
        return config.color();
    }

    @Override
    public Overlay overlay() {
        return config.overlay();
    }

    public boolean checkConditions() {
        return owner.isMet(config.requirements());
    }

    public void init() {
        order = config.carouselTexts().length - 1;
        timeLeft = 0;
    }

    public synchronized void tick() {
        if (timeLeft > 0)
            timeLeft--;

        if (timeLeft == 0) {
            int triedTimes = 0;

            do {
                if (triedTimes == config.carouselTexts().length) {
                    timeLeft = 20;
                    currentBossBar = null;
                    CustomNameplates.getInstance().getPluginLogger().warn("No text in order is available for player " + owner.name() + ". Please check your bossbar's conditions.");
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
            currentBossBar = carouselText.preParsedDynamicText().fastCreate(owner);

            if (carouselText.updateOnDisplay()) {
                owner.forceUpdatePlaceholders(currentBossBar.placeholders(), Collections.emptySet());
            }

            refresh();
            if (isShown()) {
                sendLatestBossBarName();
            }
        }
    }

    public void hide() {
        isShown = false;
        Object packet = CustomNameplates.getInstance().getPlatform().removeBossBarPacket(uuid);
        CustomNameplates.getInstance().getPacketSender().sendPacket(owner, packet);
    }

    public void show() {
        if (this.latestContent == null) {
            this.refresh();
        }
        Object packet = CustomNameplates.getInstance().getPlatform().createBossBarPacket(uuid, AdventureHelper.miniMessageToMinecraftComponent(this.latestContent), progress(), overlay(), color());
        CustomNameplates.getInstance().getPacketSender().sendPacket(owner, packet);
        isShown = true;
    }

    public boolean isShown() {
        return isShown;
    }

    @Override
    public Set<Placeholder> activePlaceholders() {
        if (currentBossBar == null || !isShown()) return Collections.emptySet();
        return currentBossBar.placeholders();
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
    public void notifyPlaceholderUpdates(CNPlayer p1, boolean force) {
        if (isShown()) {
            refresh();
            sendLatestBossBarName();
        }
    }

    public void refresh() {
        if (this.currentBossBar == null) {
            this.latestContent = "";
            return;
        }
        this.latestContent = this.currentBossBar.render(owner);
    }

    public void sendLatestBossBarName() {
        if (this.latestContent != null && isShown()) {
            Object packet = CustomNameplates.getInstance().getPlatform().updateBossBarNamePacket(uuid, AdventureHelper.miniMessageToMinecraftComponent(latestContent));
            CustomNameplates.getInstance().getPacketSender().sendPacket(owner, packet);
        }
    }
}
