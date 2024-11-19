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

public class LevelRequirement extends AbstractRequirement {

	private final int level;

	public LevelRequirement(int refreshInterval, int level) {
		super(refreshInterval);
		this.level = level;
	}

	@Override
	public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
		return ((Player) p1.player()).getLevel() >= level;
	}

	@Override
	public String type() {
		return "level";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LevelRequirement that = (LevelRequirement) o;
		return level == that.level;
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(level);
	}
}
