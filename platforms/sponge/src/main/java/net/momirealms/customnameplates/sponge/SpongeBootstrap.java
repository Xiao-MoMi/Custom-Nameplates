package net.momirealms.customnameplates.sponge;

import com.google.inject.Inject;
import com.google.inject.Injector;
import net.momirealms.customnameplates.api.CustomNameplates;
import org.spongepowered.api.Server;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.util.function.Supplier;

@Plugin("customnameplates")
public class SpongeBootstrap implements Supplier<Injector> {

    private final CustomNameplates plugin;
    private final Injector injector;

    @Inject
    public SpongeBootstrap(Injector injector) {
        this.injector = injector;
        this.plugin = new SpongeCustomNameplates(this);
    }

    @Listener(order = Order.FIRST)
    public void onEnable(ConstructPluginEvent event) {
        this.plugin.load();
        this.plugin.enable();
    }

    @Listener
    public void onDisable(StoppingEngineEvent<Server> event) {
        this.plugin.disable();
    }

    @Override
    public Injector get() {
        return this.injector;
    }
}
