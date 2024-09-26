package net.momirealms.customnameplates.bukkit.requirement.builtin;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.requirement.AbstractRequirement;

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
