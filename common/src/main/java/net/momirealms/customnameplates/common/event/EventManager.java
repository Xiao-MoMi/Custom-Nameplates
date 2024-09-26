package net.momirealms.customnameplates.common.event;

import net.momirealms.customnameplates.common.event.bus.EventBus;
import net.momirealms.customnameplates.common.plugin.NameplatesPlugin;

import java.util.OptionalInt;

public interface EventManager {

    class SingletonHolder {
        private static EventManager INSTANCE = null;
    }

    static EventManager create(NameplatesPlugin plugin) {
        if (SingletonHolder.INSTANCE == null) {
            SingletonHolder.INSTANCE = new EventManagerImpl(plugin);
        }
        return SingletonHolder.INSTANCE;
    }

    <T extends NameplatesEvent> EventSubscription<T> subscribe(Class<T> event, EventSubscriber<? super T> subscriber);

    <T extends NameplatesEvent> EventSubscription<T> subscribe(Class<T> event, EventConfig config, EventSubscriber<? super T> subscriber);

    NameplatesEvent dispatch(Class<? extends NameplatesEvent> eventClass, Object... params);

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    NameplatesEvent dispatch(Class<? extends NameplatesEvent> eventClass, OptionalInt order, Object... params);

    EventBus<?> getEventBus();
}
