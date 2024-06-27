/*
 *  Copyright (C) <2022> <XiaoMoMi>
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

package net.momirealms.customnameplates.paper.mechanic.bossbar;

import net.momirealms.customnameplates.paper.adventure.AdventureManagerImpl;
import net.momirealms.sparrow.heart.SparrowHeart;
import net.momirealms.sparrow.heart.feature.bossbar.BossBarColor;
import net.momirealms.sparrow.heart.feature.bossbar.BossBarOverlay;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BossBar {

    private final Overlay overlay;
    private final BarColor barColor;
    private final UUID uuid;
    private final Player owner;
    private String latestMiniMessage;
    private boolean visible;

    public BossBar(Player owner, Overlay overlay, BarColor barColor) {
        this.owner = owner;
        this.overlay = overlay;
        this.barColor = barColor;
        this.uuid = UUID.randomUUID();
        this.visible = false;
    }

    public void show() {
        SparrowHeart.getInstance().createBossBar(owner, uuid, AdventureManagerImpl.getInstance().getJsonComponentFromMiniMessage(latestMiniMessage), BossBarColor.valueOf(barColor.name()), BossBarOverlay.valueOf(overlay.name()), 0f, false, false, false);
        this.visible = true;
    }

    public void hide() {
        SparrowHeart.getInstance().removeBossBar(owner, uuid);
        this.visible = false;
    }

    public void update() {
        SparrowHeart.getInstance().updateBossBarName(owner, uuid, AdventureManagerImpl.getInstance().getJsonComponentFromMiniMessage(latestMiniMessage));
    }

    public boolean isVisible() {
        return visible;
    }

    public void setMiniMessageText(String text) {
        latestMiniMessage = text;
    }
}
