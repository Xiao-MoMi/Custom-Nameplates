package net.momirealms.customnameplates.bukkit.requirement.builtin;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.requirement.AbstractRequirement;

import java.util.*;

public class DateRequirement extends AbstractRequirement {

	private final Set<String> dates;

	public DateRequirement(int refreshInterval, List<String> dates) {
		super(refreshInterval);
		this.dates = new HashSet<>(dates);
	}

	@Override
	public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
		Calendar calendar = Calendar.getInstance();
		String current = (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DATE);
		return dates.contains(current);
	}

	@Override
	public String type() {
		return "date";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DateRequirement that = (DateRequirement) o;
		return dates.equals(that.dates);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(dates);
	}
}
