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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectRequirement extends AbstractRequirement {

	private final PotionEffectType type;
	private final String operator;
	private final int required;

	public PotionEffectRequirement(int refreshInterval, PotionEffectType type, String operator, int required) {
		super(refreshInterval);
		this.type = type;
		this.operator = operator;
		this.required = required;
	}

	@Override
	public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
		int level = -1;
		PotionEffect potionEffect = ((Player) p1.player()).getPotionEffect(type);
		if (potionEffect != null) {
			level = potionEffect.getAmplifier();
		}
		boolean result = false;
		switch (operator) {
			case ">=" -> {
				if (level >= required) result = true;
			}
			case ">" -> {
				if (level > required) result = true;
			}
			case "==", "=" -> {
				if (level == required) result = true;
			}
			case "!=" -> {
				if (level != required) result = true;
			}
			case "<=" -> {
				if (level <= required) result = true;
			}
			case "<" -> {
				if (level < required) result = true;
			}
		}
		return result;
	}

	@Override
	public String type() {
		return "potion-effect";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PotionEffectRequirement that = (PotionEffectRequirement) o;
		return type.equals(that.type) && operator.equals(that.operator) && required == that.required;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash += type.hashCode() * 7;
		hash += operator.hashCode() * 13;
		hash += required * 19;
		return hash;
	}
}
