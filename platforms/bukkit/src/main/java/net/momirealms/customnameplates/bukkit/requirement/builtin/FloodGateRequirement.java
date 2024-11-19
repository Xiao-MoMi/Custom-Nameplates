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
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.backend.requirement.AbstractRequirement;
import net.momirealms.customnameplates.bukkit.BukkitPlatform;
import net.momirealms.customnameplates.bukkit.compatibility.bedrock.FloodGateUtils;

public class FloodGateRequirement extends AbstractRequirement {

	private final boolean is;

	public FloodGateRequirement(int refreshInterval, boolean is) {
		super(refreshInterval);
		this.is = is;
	}

	@Override
	public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
		BukkitPlatform platform = (BukkitPlatform) CustomNameplates.getInstance().getPlatform();
		if (!platform.hasFloodGate()) return true;
		if (is) {
			return FloodGateUtils.isBedrockPlayer(p1.uuid());
		} else {
			return !FloodGateUtils.isBedrockPlayer(p1.uuid());
		}
	}

	@Override
	public String type() {
		return "floodgate";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FloodGateRequirement that = (FloodGateRequirement) o;
		return is == that.is;
	}

	@Override
	public int hashCode() {
		return is ? 17 : 3;
	}
}
