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

package net.momirealms.customnameplates.paper.mechanic.misc;

import net.momirealms.customnameplates.api.requirement.Requirement;

public class TimeLimitText {

    private int duration;
    private int refreshFrequency;
    private String text;
    private Requirement[] requirements;

    private TimeLimitText() {
        this.duration = 100;
        this.refreshFrequency = -1;
        this.text = "";
        this.requirements = new Requirement[0];
    }

    public TimeLimitText(int duration, int refreshFrequency, String text, Requirement[] requirements) {
        this.duration = duration;
        this.text = text;
        this.refreshFrequency = refreshFrequency;
        this.requirements = requirements;
    }

    public int getDuration() {
        return duration;
    }

    public String getText() {
        return text;
    }

    public Requirement[] getRequirements() {
        return requirements;
    }

    public int getRefreshFrequency() {
        return refreshFrequency;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final TimeLimitText text;

        public Builder() {
            this.text = new TimeLimitText();
        }

        public static Builder of() {
            return new Builder();
        }

        public Builder duration(int duration) {
            text.duration = duration;
            return this;
        }

        public Builder refreshFrequency(int refreshFrequency) {
            text.refreshFrequency = refreshFrequency;
            return this;
        }

        public Builder text(String content) {
            text.text = content;
            return this;
        }

        public Builder requirement(Requirement[] requirements) {
            text.requirements = requirements;
            return this;
        }

        public TimeLimitText build() {
            return text;
        }
    }
}
