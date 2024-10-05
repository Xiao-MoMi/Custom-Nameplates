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

import java.util.Objects;

public class CooldownRequirement extends AbstractRequirement {

	private final String key;
	private final int time;

	public CooldownRequirement(int refreshInterval, String key, int time) {
		super(refreshInterval);
		this.key = key;
		this.time = time;
	}

	@Override
	public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
		return false;
	}

	@Override
	public String type() {
		return "cooldown";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CooldownRequirement that = (CooldownRequirement) o;
		return key.equals(that.key);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(key);
	}
}
