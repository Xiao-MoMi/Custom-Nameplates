package net.momirealms.customnameplates.api.requirement;

import org.bukkit.OfflinePlayer;

public class Condition {

    private final OfflinePlayer player;

    public Condition(OfflinePlayer player) {
        this.player = player;
    }

    public OfflinePlayer getOfflinePlayer() {
        return player;
    }
}
