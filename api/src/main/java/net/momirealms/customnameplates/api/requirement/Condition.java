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

package net.momirealms.customnameplates.api.requirement;

import org.bukkit.OfflinePlayer;

public class Condition {

    private OfflinePlayer player;

    public Condition() {
        this.player = null;
    }

    public Condition(OfflinePlayer player) {
        this.player = player;
    }

    public OfflinePlayer getOfflinePlayer() {
        return player;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final Condition condition;

        public Builder() {
            this.condition = new Condition();
        }

        public Builder player(OfflinePlayer player) {
            condition.player = player;
            return this;
        }

        public Condition build() {
            return condition;
        }
    }
}
