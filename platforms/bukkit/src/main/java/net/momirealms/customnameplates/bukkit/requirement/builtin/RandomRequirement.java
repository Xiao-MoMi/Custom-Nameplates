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

public class RandomRequirement extends AbstractRequirement {

	private final double random;

	public RandomRequirement(int refreshInterval, double random) {
		super(refreshInterval);
		this.random = random;
	}

	@Override
	public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
		return Math.random() < random;
	}

	@Override
	public String type() {
		return "random";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RandomRequirement that = (RandomRequirement) o;
		return random == that.random;
	}

	@Override
	public int hashCode() {
		return Double.hashCode(random);
	}
}
