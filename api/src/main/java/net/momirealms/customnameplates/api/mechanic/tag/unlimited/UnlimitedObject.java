package net.momirealms.customnameplates.api.mechanic.tag.unlimited;

import net.momirealms.customnameplates.api.manager.UnlimitedTagManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;

import java.util.Vector;

public abstract class UnlimitedObject {

    protected final UnlimitedTagManager manager;
    protected final Entity entity;
    protected final Vector<Player> nearbyPlayers;

    public UnlimitedObject(UnlimitedTagManager manager, Entity entity) {
        this.manager = manager;
        this.entity = entity;
        this.nearbyPlayers = new Vector<>();
    }

    public Vector<Player> getNearbyPlayers() {
        return nearbyPlayers;
    }

    public abstract void addNearbyPlayer(Player player);

    public abstract void removeNearbyPlayer(Player player);

    public abstract void move(Player receiver, short x, short y, short z, boolean onGround);

    public abstract void teleport(Player receiver, double x, double y, double z, boolean onGround);

    public abstract void destroy();

    public abstract void sneak(boolean sneaking, boolean flying);

    public abstract void handlePose(Pose previous, Pose pose);
}
