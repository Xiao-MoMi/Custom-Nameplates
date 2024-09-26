package net.momirealms.customnameplates.common.event.gen;

import net.momirealms.customnameplates.common.event.NameplatesEvent;
import net.momirealms.customnameplates.common.plugin.NameplatesPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandles;

public abstract class AbstractNameplatesEvent implements NameplatesEvent {

    private final NameplatesPlugin plugin;

    protected AbstractNameplatesEvent(NameplatesPlugin plugin) {
        this.plugin = plugin;
    }

    @NotNull
    @Override
    public NameplatesPlugin plugin() {
        return plugin;
    }

    public MethodHandles.Lookup mhl() {
        throw new UnsupportedOperationException();
    }
}
