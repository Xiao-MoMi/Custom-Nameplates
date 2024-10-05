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

package net.momirealms.customnameplates.bukkit.requirement.builtin;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.backend.requirement.AbstractRequirement;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class NotPermissionRequirement extends AbstractRequirement {

    private final Set<String> permissions;

    public NotPermissionRequirement(int refreshInterval, List<String> permissions) {
        super(refreshInterval);
        this.permissions = new HashSet<>(permissions);
    }

    @Override
    public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
        for (String perm : permissions) {
            if (((Player) p1.player()).hasPermission(perm)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String type() {
        return "!permission";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotPermissionRequirement that = (NotPermissionRequirement) o;
        return permissions.equals(that.permissions);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(permissions);
    }
}
