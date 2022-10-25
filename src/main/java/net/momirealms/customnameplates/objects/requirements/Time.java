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

package net.momirealms.customnameplates.objects.requirements;

import org.apache.commons.lang.StringUtils;

import java.util.List;

public record Time(List<String> times) implements Requirement{

    public List<String> getTimes() {
        return this.times;
    }

    @Override
    public boolean isConditionMet(PlayerCondition playerCondition) {
        long time = playerCondition.getLocation().getWorld().getTime();
        for (String range : times) {
            String[] timeMinMax = StringUtils.split(range, "~");
            if (time > Long.parseLong(timeMinMax[0]) && time < Long.parseLong(timeMinMax[1])) {
                return true;
            }
        }
        return false;
    }
}
