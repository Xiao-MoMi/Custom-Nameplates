package net.momirealms.customnameplates.bukkit.requirement.builtin;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.requirement.AbstractRequirement;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

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
			case "==" -> {
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
		return Objects.hash(type, operator, required);
	}
}
