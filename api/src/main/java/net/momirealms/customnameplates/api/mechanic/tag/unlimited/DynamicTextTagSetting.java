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

package net.momirealms.customnameplates.api.mechanic.tag.unlimited;

import net.momirealms.customnameplates.api.requirement.Requirement;

public class DynamicTextTagSetting {

    private double verticalOffset;
    private String rawText;
    private int checkFrequency;
    private int refreshFrequency;
    private Requirement[] viewerRequirements;
    private Requirement[] ownerRequirements;

    private DynamicTextTagSetting() {
        verticalOffset = 0;
        rawText = "";
        checkFrequency = 10;
        refreshFrequency = 10;
        viewerRequirements = new Requirement[0];
        ownerRequirements = new Requirement[0];
    }

    public DynamicTextTagSetting(double verticalOffset, String rawText, int checkFrequency, int refreshFrequency, Requirement[] viewerRequirements, Requirement[] ownerRequirements) {
        this.verticalOffset = verticalOffset;
        this.rawText = rawText;
        this.checkFrequency = checkFrequency;
        this.refreshFrequency = refreshFrequency;
        this.viewerRequirements = viewerRequirements;
        this.ownerRequirements = ownerRequirements;
    }

    public static Builder builder() {
        return new Builder();
    }

    public double getVerticalOffset() {
        return verticalOffset;
    }

    public String getRawText() {
        return rawText;
    }

    public int getCheckFrequency() {
        return checkFrequency;
    }

    public int getRefreshFrequency() {
        return refreshFrequency;
    }

    public Requirement[] getViewerRequirements() {
        return viewerRequirements;
    }

    public Requirement[] getOwnerRequirements() {
        return ownerRequirements;
    }

    public static class Builder {

        private final DynamicTextTagSetting setting;

        public static Builder of() {
            return new Builder();
        }

        public Builder() {
            this.setting = new DynamicTextTagSetting();
        }

        public Builder checkFrequency(int checkFrequency) {
            this.setting.checkFrequency = checkFrequency;
            return this;
        }

        public Builder refreshFrequency(int refreshFrequency) {
            this.setting.refreshFrequency = refreshFrequency;
            return this;
        }

        public Builder rawText(String rawText) {
            this.setting.rawText = rawText;
            return this;
        }

        public Builder verticalOffset(double verticalOffset) {
            this.setting.verticalOffset = verticalOffset;
            return this;
        }

        public Builder ownerRequirements(Requirement[] ownerRequirements) {
            this.setting.ownerRequirements = ownerRequirements;
            return this;
        }

        public Builder viewerRequirements(Requirement[] viewerRequirements) {
            this.setting.viewerRequirements = viewerRequirements;
            return this;
        }

        public DynamicTextTagSetting build() {
            return setting;
        }
    }
}
