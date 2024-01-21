package net.momirealms.customnameplates.api.mechanic.tag.unlimited;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface NearbyRule {

    boolean isPassed(Player viewer, Entity target);
}
