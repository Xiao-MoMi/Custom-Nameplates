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

package net.momirealms.customnameplates.api.mechanic.placeholder;

public class CachedText {

    private long refreshInterval;
    private String text;

    private CachedText() {
    }

    public CachedText(long refreshInterval, String text) {
        this.refreshInterval = refreshInterval;
        this.text = text;
    }

    public long getRefreshInterval() {
        return refreshInterval;
    }

    public String getText() {
        return text;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final CachedText text;

        public Builder() {
            this.text = new CachedText();
        }

        public static Builder of() {
            return new Builder();
        }

        public Builder refreshInterval(long time) {
            this.text.refreshInterval = time;
            return this;
        }

        public Builder text(String text) {
            this.text.text = text;
            return this;
        }

        public CachedText build() {
            return text;
        }
    }
}
