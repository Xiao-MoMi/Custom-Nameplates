package net.momirealms.customnameplates.bukkit.requirement.builtin;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.requirement.AbstractRequirement;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class WeatherRequirement extends AbstractRequirement {

	private final Set<String> weathers;

	public WeatherRequirement(int refreshInterval, List<String> weathers) {
		super(refreshInterval);
		this.weathers = new HashSet<>(weathers);
	}

	@Override
	public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
		String currentWeather;
		World world = ((Player) p1).getWorld();
		if (world.isClearWeather()) currentWeather = "clear";
		else if (world.isThundering()) currentWeather = "thunder";
		else currentWeather = "rain";
		return weathers.contains(currentWeather);
	}

	@Override
	public String type() {
		return "weather";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		WeatherRequirement that = (WeatherRequirement) o;
		return weathers.equals(that.weathers);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(weathers);
	}
}
