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

import java.util.*;

public class DateRequirement extends AbstractRequirement {

	private final Set<String> dates;

	public DateRequirement(int refreshInterval, List<String> dates) {
		super(refreshInterval);
		this.dates = new HashSet<>(dates);
	}

	@Override
	public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
		Calendar calendar = Calendar.getInstance();
		String current = (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DATE);
		return dates.contains(current);
	}

	@Override
	public String type() {
		return "date";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DateRequirement that = (DateRequirement) o;
		return dates.equals(that.dates);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(dates);
	}
}
