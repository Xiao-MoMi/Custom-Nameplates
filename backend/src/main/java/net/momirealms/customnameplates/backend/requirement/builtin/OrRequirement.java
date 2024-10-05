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

package net.momirealms.customnameplates.backend.requirement.builtin;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.requirement.Requirement;
import net.momirealms.customnameplates.backend.requirement.AbstractRequirement;

public class OrRequirement extends AbstractRequirement {

    private final Requirement[] requirements;

    public OrRequirement(int refreshInterval, Requirement[] requirements) {
        super(refreshInterval);
        this.requirements = requirements;
    }

    @Override
    public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
        if (requirements.length == 0) return true;
        for (Requirement requirement : requirements) {
            if (requirement.isSatisfied(p1, p2)) return true;
        }
        return false;
    }

    @Override
    public String type() {
        return "||";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        OrRequirement that = (OrRequirement) object;
        int length = requirements.length;
        if (that.requirements.length != length)
            return false;
        for (int i = 0; i < length; i++) {
            Object e1 = requirements[i];
            Object e2 = that.requirements[i];
            if (e1 == e2)
                continue;
            if (!e1.equals(e2)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 271;
        for (Requirement requirement : requirements) {
            hash += requirement.hashCode();
        }
        return hash;
    }
}
