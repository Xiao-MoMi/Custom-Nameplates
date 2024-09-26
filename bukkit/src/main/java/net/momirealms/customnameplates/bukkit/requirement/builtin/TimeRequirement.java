package net.momirealms.customnameplates.bukkit.requirement.builtin;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.requirement.AbstractRequirement;
import net.momirealms.customnameplates.common.util.Pair;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class TimeRequirement extends AbstractRequirement {

	private final List<Pair<Integer, Integer>> timePair;

	public TimeRequirement(int refreshInterval, List<String> list) {
		super(refreshInterval);
		this.timePair = list.stream().map(line -> {
			String[] split = line.split("~");
			return new Pair<>(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
		}).toList();
	}

	@Override
	public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
		long time = ((Player) p1.player()).getWorld().getTime();
		for (Pair<Integer, Integer> pair : timePair)
			if (time >= pair.left() && time <= pair.right())
				return true;
		return false;
	}

	@Override
	public String type() {
		return "time";
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TimeRequirement that = (TimeRequirement) o;
		return timePair.equals(that.timePair);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(timePair);
	}
}
