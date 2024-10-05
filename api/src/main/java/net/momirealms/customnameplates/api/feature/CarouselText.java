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

package net.momirealms.customnameplates.api.feature;

import net.momirealms.customnameplates.api.requirement.Requirement;

public class CarouselText {

    private final int duration;
    private final Requirement[] requirements;
    private final String rawText;
    private final PreParsedDynamicText preParsedDynamicText;
    private final boolean updateOnDisplay;

    public CarouselText(int duration, Requirement[] requirements, String rawText, boolean updateOnDisplay) {
        this.duration = duration;
        this.requirements = requirements;
        this.rawText = rawText;
        this.preParsedDynamicText = new PreParsedDynamicText(rawText);
        this.preParsedDynamicText.init();
        this.updateOnDisplay = updateOnDisplay;
    }

    public int duration() {
        return duration;
    }

    public Requirement[] requirements() {
        return requirements;
    }

    public String text() {
        return rawText;
    }

    public PreParsedDynamicText preParsedDynamicText() {
        return preParsedDynamicText;
    }

    public boolean updateOnDisplay() {
        return updateOnDisplay;
    }
}
