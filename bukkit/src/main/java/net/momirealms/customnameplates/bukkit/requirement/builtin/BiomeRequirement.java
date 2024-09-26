package net.momirealms.customnameplates.bukkit.requirement.builtin;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.requirement.AbstractRequirement;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BiomeRequirement extends AbstractRequirement {

	private final Set<String> biomes;

	public BiomeRequirement(int refreshInterval, List<String> biomes) {
		super(refreshInterval);
		this.biomes = new HashSet<>(biomes);
	}

	@Override
	public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
		String currentBiome = ((Player) p1.player()).getLocation().getBlock().getBiome().toString();
		return biomes.contains(currentBiome);
	}

	@Override
	public String type() {
		return "biome";
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BiomeRequirement that = (BiomeRequirement) o;
		return biomes.equals(that.biomes);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(biomes);
	}
}
