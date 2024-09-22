package net.momirealms.customnameplates.bukkit.player;

import net.momirealms.customnameplates.api.AbstractCNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BukkitCNPlayer extends AbstractCNPlayer<Player> {

    public BukkitCNPlayer(CustomNameplates plugin, Player player) {
        super(plugin, player);
    }

    @Override
    public String name() {
        return player().getName();
    }

    @Override
    public UUID uuid() {
        return player().getUniqueId();
    }

    @Override
    public boolean isOnline() {
        return player().isOnline();
    }
}
