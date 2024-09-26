package net.momirealms.customnameplates.bukkit.requirement.builtin;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.requirement.AbstractRequirement;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class NotGameModeRequirement extends AbstractRequirement {

	private final List<String> modes;

	public NotGameModeRequirement(int refreshInterval, List<String> list) {
		super(refreshInterval);
		this.modes = list;
	}

	@Override
	public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
		var name = ((Player) p1.player()).getGameMode().name().toLowerCase(Locale.ENGLISH);
		return !modes.contains(name);
	}

	@Override
	public String type() {
		return "!gamemode";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		NotGameModeRequirement that = (NotGameModeRequirement) o;
		return modes.equals(that.modes);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(modes);
	}
}
