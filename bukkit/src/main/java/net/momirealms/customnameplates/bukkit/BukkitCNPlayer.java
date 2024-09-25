package net.momirealms.customnameplates.bukkit;

import net.momirealms.customnameplates.api.AbstractCNPlayer;
import net.momirealms.customnameplates.api.CustomNameplates;
import net.momirealms.customnameplates.api.helper.VersionHelper;
import net.momirealms.customnameplates.api.util.Vector3;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BukkitCNPlayer extends AbstractCNPlayer {

    private static final Attribute scaleAttribute = Registry.ATTRIBUTE.get(NamespacedKey.minecraft("generic.scale"));

    private final Player player;

    public BukkitCNPlayer(CustomNameplates plugin, Player player) {
        super(plugin, player);
        this.player = player;
    }

    @Override
    public String name() {
        return player.getName();
    }

    @Override
    public UUID uuid() {
        return player.getUniqueId();
    }

    @Override
    public int entityID() {
        return player.getEntityId();
    }

    @Override
    public Vector3 position() {
        Location location = player.getLocation();
        return new Vector3(location.x(), location.y(), location.z());
    }

    @Override
    public boolean isOnline() {
        return player.isOnline();
    }

    @Override
    public Set<Integer> passengers() {
        return player.getPassengers().stream().map(Entity::getEntityId).collect(Collectors.toSet());
    }

    @Override
    public boolean isFlying() {
        return player.isFlying();
    }
}
