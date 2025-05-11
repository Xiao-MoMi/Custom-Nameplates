package net.momirealms.customnameplates.api;

import net.momirealms.customnameplates.api.util.Vector3;

import java.util.Set;
import java.util.UUID;

public class DummyPlayer extends AbstractCNPlayer<Object> {
    private final CNPlayer target;
    private final int megEntityId;
    private final Vector3 position;

    public DummyPlayer(CustomNameplates plugin, CNPlayer delegate, int entityId, Vector3 location) {
        super(plugin, delegate.channel());
        this.target = delegate;
        this.megEntityId = entityId;
        this.position = location;
    }

    @Override
    public int entityID() {
        return this.megEntityId;
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.target.hasPermission(permission);
    }

    @Override
    public Vector3 position() {
        return this.position;
    }

    @Override
    public String world() {
        return this.target.world();
    }

    @Override
    public boolean isOnline() {
        return this.target.isOnline();
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public double scale() {
        return this.target.scale();
    }

    @Override
    public boolean isCrouching() {
        return false;
    }

    @Override
    public long playerTime() {
        return this.target.playerTime();
    }

    @Override
    public boolean isFlying() {
        return false;
    }

    @Override
    public int remainingAir() {
        return this.target.remainingAir();
    }

    @Override
    public Set<Integer> passengers() {
        return Set.of();
    }

    @Override
    public boolean isInitialized() {
        return false;
    }

    @Override
    public String name() {
        return this.target.name();
    }

    @Override
    public UUID uuid() {
        return this.target.uuid();
    }

    @Override
    public Object player() {
        return this.target.player();
    }

    @Override
    public String currentNameplate() {
        return this.target.currentNameplate();
    }

    @Override
    public String currentBubble() {
        return this.target.currentBubble();
    }
}
