package net.momirealms.customnameplates.bukkit.requirement.builtin;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.requirement.AbstractRequirement;
import org.bukkit.entity.Player;

import java.util.*;

public class NotEnvironmentRequirement extends AbstractRequirement {

	private final Set<String> environments;

	public NotEnvironmentRequirement(int refreshInterval, List<String> list) {
		super(refreshInterval);
		this.environments = new HashSet<>(list);
	}

	@Override
	public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
		var name = ((Player) p1.player()).getWorld().getEnvironment().name().toLowerCase(Locale.ENGLISH);
		return !environments.contains(name);
	}

	@Override
	public String type() {
		return "!environment";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		NotEnvironmentRequirement that = (NotEnvironmentRequirement) o;
		return environments.equals(that.environments);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(environments);
	}
}
