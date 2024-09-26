package net.momirealms.customnameplates.bukkit.requirement.builtin;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.requirement.AbstractRequirement;

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
