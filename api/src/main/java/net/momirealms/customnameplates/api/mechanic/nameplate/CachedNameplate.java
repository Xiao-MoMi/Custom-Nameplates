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

package net.momirealms.customnameplates.api.mechanic.nameplate;

import net.momirealms.customnameplates.common.team.TeamColor;

public class CachedNameplate {

    private TeamColor teamColor;
    private String tagPrefix;
    private String tagSuffix;
    private String namePrefix;
    private String nameSuffix;
    private String playerName;

    public CachedNameplate() {
        this.tagPrefix = "";
        this.tagSuffix = "";
        this.namePrefix = "";
        this.nameSuffix = "";
        this.playerName = "";
        this.teamColor = TeamColor.WHITE;
    }

    public String getTagPrefix() {
        return tagPrefix;
    }

    public void setTagPrefix(String prefix) {
        this.tagPrefix = prefix;
    }

    public String getTagSuffix() {
        return tagSuffix;
    }

    public void setTagSuffix(String suffix) {
        this.tagSuffix = suffix;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    public void setNameSuffix(String nameSuffix) {
        this.nameSuffix = nameSuffix;
    }

    public String getNamePrefix() {
        return namePrefix;
    }

    public String getNameSuffix() {
        return nameSuffix;
    }

    public TeamColor getTeamColor() {
        return teamColor;
    }

    public void setTeamColor(TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
