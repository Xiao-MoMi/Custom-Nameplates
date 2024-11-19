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
import net.momirealms.customnameplates.common.util.Pair;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class YRequirement extends AbstractRequirement {

	private final List<Pair<Integer, Integer>> yPair;

	public YRequirement(int refreshInterval, List<String> list) {
		super(refreshInterval);
		this.yPair = list.stream().map(line -> {
			String[] split = line.split("~");
			return new Pair<>(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
		}).toList();
	}

	@Override
	public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
		double y = ((Player) p1.player()).getY();
		for (Pair<Integer, Integer> pair : yPair)
			if (y >= pair.left() && y <= pair.right())
				return true;
		return false;
	}

	@Override
	public String type() {
		return "ypos";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		YRequirement that = (YRequirement) o;
		return yPair.equals(that.yPair);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(yPair);
	}
}
