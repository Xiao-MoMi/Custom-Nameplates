package net.momirealms.customnameplates.api.placeholder;

import net.momirealms.customnameplates.api.CNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceholderUpdateTask implements Runnable {

    private final List<Placeholder> placeholders;
    private final Map<SharedPlaceholder, String> sharedResults = new HashMap<>();
    private final Map<PlayerPlaceholder, String> playerResults = new HashMap<>();
    private final Map<RelationalPlaceholder, Map<CNPlayer<?>, String>> relationalResults = new HashMap<>();
    private final CNPlayer<?> owner;

    public PlaceholderUpdateTask(CNPlayer<?> owner, List<Placeholder> placeholders) {
        this.placeholders = placeholders;
        this.owner = owner;
    }

    @Override
    public void run() {
        for (Placeholder placeholder : placeholders) {
            if (placeholder instanceof SharedPlaceholder shared) {
                sharedResults.put(shared, shared.getLatestValue());
            } else if (placeholder instanceof PlayerPlaceholder player) {
                playerResults.put(player, player.request(owner));
            } else if (placeholder instanceof RelationalPlaceholder relational) {
                Map<CNPlayer<?>, String> values = new HashMap<>();
                for (CNPlayer<?> player : owner.getTracker().nearbyPlayers()) {
                    values.put(player, relational.request(owner, player));
                }
                relationalResults.put(relational, values);
            }
        }
    }

    public Map<SharedPlaceholder, String> getSharedResults() {
        return sharedResults;
    }

    public Map<PlayerPlaceholder, String> getPlayerResults() {
        return playerResults;
    }

    public Map<RelationalPlaceholder, Map<CNPlayer<?>, String>> getRelationalResults() {
        return relationalResults;
    }
}
