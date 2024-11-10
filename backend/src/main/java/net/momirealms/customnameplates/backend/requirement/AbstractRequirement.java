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

package net.momirealms.customnameplates.backend.requirement;

import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.requirement.Requirement;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractRequirement implements Requirement {

    protected final int refreshInterval;
    @Nullable
    protected Integer countId;

    public AbstractRequirement(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    @Override
    public int countId() {
        // lazy init
        if (countId == null) {
            countId = CustomNameplates.getInstance().getRequirementManager().countId(this);
        }
        return countId;
    }

    @Override
    public int refreshInterval() {
        return refreshInterval;
    }
}
