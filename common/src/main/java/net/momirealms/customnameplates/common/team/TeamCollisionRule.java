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

package net.momirealms.customnameplates.common.team;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public enum TeamCollisionRule {

    ALWAYS("always"),
    NEVER("never"),
    PUSH_OTHER_TEAMS("pushOtherTeams"),
    PUSH_OWN_TEAM("pushOwnTeam");

    private final String id;

    TeamCollisionRule(@NotNull String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @NotNull
    public static TeamCollisionRule byId(String id) {
        return Arrays.stream(values())
                .filter(mode -> mode.id.equals(id))
                .findFirst()
                .orElse(ALWAYS);
    }
}
