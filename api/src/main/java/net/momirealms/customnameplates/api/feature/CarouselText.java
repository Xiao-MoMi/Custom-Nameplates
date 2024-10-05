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

/**
 * Represents a piece of carousel text, which cycles through a series of texts with associated requirements and duration.
 */
public class CarouselText {

    private final int duration;
    private final Requirement[] requirements;
    private final String rawText;
    private final PreParsedDynamicText preParsedDynamicText;
    private final boolean updateOnDisplay;

    /**
     * Constructs a new CarouselText instance.
     *
     * @param duration        the duration (in ticks) for how long this text is displayed
     * @param requirements    the requirements for displaying this text
     * @param rawText         the raw text content
     * @param updateOnDisplay whether the text should be updated when displayed
     */
    public CarouselText(int duration, Requirement[] requirements, String rawText, boolean updateOnDisplay) {
        this.duration = duration;
        this.requirements = requirements;
        this.rawText = rawText;
        this.preParsedDynamicText = new PreParsedDynamicText(rawText);
        this.preParsedDynamicText.init();
        this.updateOnDisplay = updateOnDisplay;
    }

    /**
     * Returns the duration (in ticks) for how long this text will be displayed.
     *
     * @return the display duration
     */
    public int duration() {
        return duration;
    }

    /**
     * Returns the requirements that must be met to display this text.
     *
     * @return an array of requirements
     */
    public Requirement[] requirements() {
        return requirements;
    }

    /**
     * Returns the raw text content.
     *
     * @return the raw text
     */
    public String text() {
        return rawText;
    }

    /**
     * Returns the pre-parsed dynamic text, which processes placeholders or dynamic content.
     *
     * @return the pre-parsed dynamic text
     */
    public PreParsedDynamicText preParsedDynamicText() {
        return preParsedDynamicText;
    }

    /**
     * Checks if the text should be updated each time it is displayed.
     *
     * @return true if the text updates on display, false otherwise
     */
    public boolean updateOnDisplay() {
        return updateOnDisplay;
    }
}