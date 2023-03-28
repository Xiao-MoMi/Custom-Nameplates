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

package net.momirealms.customnameplates.object.bossbar;

import net.momirealms.customnameplates.object.requirements.Requirement;
import org.bukkit.boss.BarColor;

public class BossBarConfig {

    private final String[] texts;
    private final int interval;
    private final Overlay overlay;
    private final BarColor color;
    private final Requirement[] conditions;

    public BossBarConfig(String[] texts, Overlay overlay, BarColor color, int interval, Requirement[] conditions) {
        this.texts = texts;
        this.overlay = overlay;
        this.color = color;
        this.interval = interval;
        this.conditions = conditions;
    }

    public String[] getTexts() {
        return texts;
    }

    public int getInterval() {
        return interval;
    }

    public Overlay getOverlay() {
        return overlay;
    }

    public BarColor getColor() {
        return color;
    }

    public Requirement[] getConditions() {
        return conditions;
    }
}
