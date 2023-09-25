package net.momirealms.customnameplates.object.requirements.papi;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public record PapiRegex(String papi, String regex) implements PapiRequirement {

    @Override
    public boolean isMet(Player player) {
        return PlaceholderAPI.setPlaceholders(player, papi).matches(regex);
    }
}
