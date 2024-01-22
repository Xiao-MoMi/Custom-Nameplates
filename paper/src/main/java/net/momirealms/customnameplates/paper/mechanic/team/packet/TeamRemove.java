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

package net.momirealms.customnameplates.paper.mechanic.team.packet;

public class TeamRemove {

    private String teamName;

    private TeamRemove() {
    }

    public TeamRemove(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final TeamRemove packet;

        public Builder() {
            this.packet = new TeamRemove();
        }

        public Builder teamName(String teamName) {
            packet.teamName = teamName;
            return this;
        }

        public TeamRemove build() {
            return packet;
        }
    }
}
