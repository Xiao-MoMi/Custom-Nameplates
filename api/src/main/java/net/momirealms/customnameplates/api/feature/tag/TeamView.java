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

package net.momirealms.customnameplates.api.feature.tag;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class TeamView {

    private final Map<String, Set<String>> teamMembers = new Object2ObjectOpenHashMap<>();

    @Nullable
    public Set<String> getTeamMembers(String team) {
        return teamMembers.get(team);
    }

    public void addTeamMembers(String team, Collection<String> members) {
        teamMembers.computeIfAbsent(team, k -> new ObjectOpenHashSet<>(members));
    }

    public void removeTeamMembers(String team, Collection<String> members) {
        Set<String> membersSet = teamMembers.get(team);
        if (membersSet != null) {
            membersSet.removeAll(members);
        }
    }

    public void removeTeam(String team) {
        this.teamMembers.remove(team);
    }
}
