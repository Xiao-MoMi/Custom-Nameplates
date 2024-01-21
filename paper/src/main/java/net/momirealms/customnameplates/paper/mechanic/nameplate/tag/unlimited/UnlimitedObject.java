package net.momirealms.customnameplates.paper.mechanic.nameplate.tag.unlimited;

import net.momirealms.customnameplates.api.util.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;

import java.util.Vector;

public abstract class UnlimitedObject {

    protected final UnlimitedTagManagerImpl manager;
    protected final Entity entity;
    protected final Vector<Player> nearbyPlayers;

    public UnlimitedObject(UnlimitedTagManagerImpl manager, Entity entity) {
        this.manager = manager;
        this.entity = entity;
        this.nearbyPlayers = new Vector<>();
    }

    public Entity getEntity() {
        return entity;
    }

    public Vector<Player> getNearbyPlayers() {
        return nearbyPlayers;
    }

    public abstract void addNearbyPlayerNaturally(Player player);

    public abstract void removeNearbyPlayerNaturally(Player player);

    public void addNearByPlayerToMap(int range) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (       online == entity
                    || !online.canSee(entity)
                    || LocationUtils.getDistance(online, entity) > range
                    || online.getWorld() != entity.getWorld()
                    || online.isDead()
            ) continue;
            addNearbyPlayerNaturally(online);
        }
    }

    public abstract void move(Player receiver, short x, short y, short z, boolean onGround);

    public abstract void teleport(Player receiver, double x, double y, double z, boolean onGround);

    public abstract void handlePose(Pose previous, Pose pose);

    public abstract void destroy();
}
