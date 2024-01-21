package net.momirealms.customnameplates.paper.mechanic.requirement.papi;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;

public record PapiRegex(String papi, String regex) implements PapiRequirement {

    @Override
    public boolean isMet(OfflinePlayer player) {
        return PlaceholderAPI.setPlaceholders(player, papi).matches(regex);
    }
}
