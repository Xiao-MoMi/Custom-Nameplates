package net.momirealms.customnameplates.bukkit.requirement.builtin;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.requirement.AbstractRequirement;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class NotBiomeRequirement extends AbstractRequirement {

	private final Set<String> biomes;

	public NotBiomeRequirement(int refreshInterval, List<String> biomes) {
		super(refreshInterval);
		this.biomes = new HashSet<>(biomes);
	}

	@Override
	public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
		String currentBiome = ((Player) p1.player()).getLocation().getBlock().getBiome().toString();
		return !biomes.contains(currentBiome);
	}

	@Override
	public String type() {
		return "!biome";
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		NotBiomeRequirement that = (NotBiomeRequirement) o;
		return biomes.equals(that.biomes);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(biomes);
	}
}
