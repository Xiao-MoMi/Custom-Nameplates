package net.momirealms.customnameplates.common.event;

import net.momirealms.customnameplates.common.event.bus.EventBus;
import net.momirealms.customnameplates.common.event.bus.SimpleEventBus;
import net.momirealms.customnameplates.common.event.gen.EventGenerator;
import net.momirealms.customnameplates.common.event.registry.EventRegistry;
import net.momirealms.customnameplates.common.event.registry.SimpleEventRegistry;
import net.momirealms.customnameplates.common.plugin.NameplatesPlugin;

import java.util.OptionalInt;

public class EventManagerImpl implements EventManager {

    private final EventBus<NameplatesEvent> eventBus;
    private final EventRegistry<NameplatesEvent> registry;
    private final NameplatesPlugin plugin;

    protected EventManagerImpl(NameplatesPlugin plugin) {
        this.plugin = plugin;
        this.registry = new SimpleEventRegistry<>(NameplatesEvent.class);
        this.eventBus = new SimpleEventBus<>(registry, new EventBus.EventExceptionHandler() {
            @Override
            public <E> void eventExceptionCaught(EventBus<? super E> bus, EventSubscription<? super E> subscription, E event, Throwable throwable) {
                plugin.getPluginLogger().severe("Exception caught in event handler", throwable);
            }
        });
    }

    @Override
    public <T extends NameplatesEvent> EventSubscription<T> subscribe(final Class<T> event, final EventSubscriber<? super T> subscriber) {
        return registry.subscribe(event, subscriber);
    }

    @Override
    public <T extends NameplatesEvent> EventSubscription<T> subscribe(final Class<T> event, EventConfig config, final EventSubscriber<? super T> subscriber) {
        return registry.subscribe(event, config, subscriber);
    }

    @Override
    public NameplatesEvent dispatch(Class<? extends NameplatesEvent> eventClass, Object... params) {
        NameplatesEvent event = generate(eventClass, params);
        this.eventBus.post(event, OptionalInt.empty());
        return event;
    }

    @Override
    public NameplatesEvent dispatch(Class<? extends NameplatesEvent> eventClass, OptionalInt order, Object... params) {
        NameplatesEvent event = generate(eventClass, params);
        this.eventBus.post(event, order);
        return event;
    }

    @Override
    public EventBus<?> getEventBus() {
        return this.eventBus;
    }

    private NameplatesEvent generate(Class<? extends NameplatesEvent> eventClass, Object... params) {
        try {
            return EventGenerator.generate(eventClass).newInstance(this.plugin, params);
        } catch (Throwable e) {
            throw new RuntimeException("Exception occurred whilst generating event instance", e);
        }
    }
}
