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

/**
 * Represents a view of teams and their associated members.
 * <p>
 * This class allows you to manage a collection of teams, with each team having a set of member names.
 * You can add or remove members from teams, retrieve the list of members for a specific team,
 * or remove entire teams.
 * </p>
 */
public class TeamView {
    /**
     * A map that stores the team names as keys and their associated members as values.
     * The members are stored as a set of strings to avoid duplicates.
     */
    private final Map<String, Set<String>> teamMembers = new Object2ObjectOpenHashMap<>();

    /**
     * Retrieves the members of a given team.
     *
     * @param team the name of the team
     * @return a set of member names, or {@code null} if the team does not exist
     */
    @Nullable
    public Set<String> getTeamMembers(String team) {
        return teamMembers.get(team);
    }

    /**
     * Adds a collection of members to a team. If the team does not exist, it will be created.
     *
     * @param team    the name of the team
     * @param members the collection of members to add
     */
    public void addTeamMembers(String team, Collection<String> members) {
        teamMembers.computeIfAbsent(team, k -> new ObjectOpenHashSet<>(members));
    }

    /**
     * Removes a collection of members from a team. If the team exists, the members will be removed
     * from the team's member set.
     *
     * @param team    the name of the team
     * @param members the collection of members to remove
     */
    public void removeTeamMembers(String team, Collection<String> members) {
        Set<String> membersSet = teamMembers.get(team);
        if (membersSet != null) {
            membersSet.removeAll(members);
        }
    }

    /**
     * Removes an entire team and all its members.
     *
     * @param team the name of the team to remove
     */
    public void removeTeam(String team) {
        this.teamMembers.remove(team);
    }
}
