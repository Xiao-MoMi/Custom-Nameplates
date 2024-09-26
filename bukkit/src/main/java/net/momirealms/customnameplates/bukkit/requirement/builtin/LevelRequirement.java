package net.momirealms.customnameplates.bukkit.requirement.builtin;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.requirement.AbstractRequirement;
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
