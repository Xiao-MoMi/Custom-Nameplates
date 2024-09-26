package net.momirealms.customnameplates.bukkit.requirement.builtin;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.requirement.AbstractRequirement;
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
