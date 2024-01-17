package net.momirealms.customnameplates.paper.util;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class LocationUtils {

    public static double getDistance(Entity e1, Entity e2) {
        Location loc1 = e1.getLocation();
        Location loc2 = e2.getLocation();
        return Math.sqrt(Math.pow(loc1.getX()-loc2.getX(), 2) + Math.pow(loc1.getZ()-loc2.getZ(), 2));
    }
}
