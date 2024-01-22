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

import net.momirealms.customnameplates.common.team.TeamCollisionRule;
import net.momirealms.customnameplates.common.team.TeamColor;
import net.momirealms.customnameplates.common.team.TeamTagVisibility;
import net.momirealms.customnameplates.paper.adventure.AdventureManagerImpl;

public class TeamUpdate {

    private String teamName;

    /*
    Optional<b> k
     */
    // IChatBaseComponent a
    private Object teamDisplay;
    // IChatBaseComponent b
    private Object teamPrefix;
    // IChatBaseComponent c
    private Object teamSuffix;
    // String d
    private TeamTagVisibility tagVisibility;
    // String e
    private TeamCollisionRule collisionRule;
    // Enum f
    private TeamColor teamColor;

    private TeamUpdate() {
    }

    public TeamUpdate(
            String teamName,
            Object teamDisplay,
            Object teamPrefix,
            Object teamSuffix,
            TeamTagVisibility tagVisibility,
            TeamCollisionRule collisionRule,
            TeamColor teamColor
    ) {
        this.teamName = teamName;
        this.teamDisplay = teamDisplay;
        this.teamPrefix = teamPrefix;
        this.teamSuffix = teamSuffix;
        this.tagVisibility = tagVisibility;
        this.collisionRule = collisionRule;
        this.teamColor = teamColor;
    }

    public String getTeamName() {
        return teamName;
    }

    public Object getTeamDisplay() {
        return teamDisplay;
    }

    public Object getTeamPrefix() {
        return teamPrefix;
    }

    public Object getTeamSuffix() {
        return teamSuffix;
    }

    public TeamTagVisibility getTagVisibility() {
        return tagVisibility;
    }

    public TeamCollisionRule getCollisionRule() {
        return collisionRule;
    }

    public TeamColor getTeamColor() {
        return teamColor;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final TeamUpdate packet;

        public Builder() {
            this.packet = new TeamUpdate();
        }

        public static Builder of() {
            return new Builder();
        }

        public Builder teamName(String name) {
            packet.teamName = name;
            return this;
        }

        public Builder display(String display) {
            packet.teamDisplay = AdventureManagerImpl.getInstance().getIChatComponentFromMiniMessage(display);
            return this;
        }

        public Builder prefix(String prefix) {
            packet.teamPrefix = AdventureManagerImpl.getInstance().getIChatComponentFromMiniMessage(prefix);
            return this;
        }

        public Builder suffix(String suffix) {
            packet.teamSuffix = AdventureManagerImpl.getInstance().getIChatComponentFromMiniMessage(suffix);;
            return this;
        }

        public Builder color(TeamColor color) {
            packet.teamColor = color;
            return this;
        }

        public Builder tagVisibility(TeamTagVisibility visibility) {
            packet.tagVisibility = visibility;
            return this;
        }

        public Builder collisionRule(TeamCollisionRule rule) {
            packet.collisionRule = rule;
            return this;
        }

        public TeamUpdate build() {
            return packet;
        }
    }
}
