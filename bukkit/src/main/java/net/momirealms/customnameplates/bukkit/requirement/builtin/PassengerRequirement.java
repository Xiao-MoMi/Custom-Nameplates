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

import com.google.common.base.Objects;
import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.backend.requirement.AbstractRequirement;
import org.bukkit.entity.Player;

public class PassengerRequirement extends AbstractRequirement {

	private final boolean is;

	public PassengerRequirement(int refreshInterval, boolean is) {
		super(refreshInterval);
		this.is = is;
	}

	@Override
	public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
		return ((Player) p1.player()).isInsideVehicle();
	}

	@Override
	public String type() {
		return "is-passenger";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PassengerRequirement that)) return false;
        return is == that.is;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(is);
	}
}
