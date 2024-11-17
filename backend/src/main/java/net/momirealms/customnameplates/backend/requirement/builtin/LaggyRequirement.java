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
import net.momirealms.customnameplates.api.ConfigManager;
import net.momirealms.customnameplates.backend.requirement.AbstractRequirement;

public class LaggyRequirement extends AbstractRequirement {

    private final int time;

    public LaggyRequirement(int refreshInterval, int time) {
        super(refreshInterval);
        this.time = time;
    }

    @Override
    public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
        if (!ConfigManager.debug()) return true;
        long time1 = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - time1 > time) {
                break;
            }
        }
        return true;
    }

    @Override
    public String type() {
        return "laggy";
    }

    @Override
    public int hashCode() {
        return type().hashCode() + time * 17;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LaggyRequirement that)) return false;
        return time == that.time;
    }
}
