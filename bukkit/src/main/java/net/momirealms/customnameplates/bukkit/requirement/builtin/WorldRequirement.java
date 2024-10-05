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

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class WorldRequirement extends AbstractRequirement {

	private final Set<String> worlds;

	public WorldRequirement(int refreshInterval, List<String> worlds) {
		super(refreshInterval);
		this.worlds = new HashSet<>(worlds);
	}

	@Override
	public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
		String currentWorld = ((Player) p1.player()).getWorld().getName();
		return worlds.contains(currentWorld);
	}

	@Override
	public String type() {
		return "world";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		WorldRequirement that = (WorldRequirement) o;
		return worlds.equals(that.worlds);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(worlds);
	}
}
