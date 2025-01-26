/*
 *  Copyright (C) <2024> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customnameplates.common.event;

import net.momirealms.customnameplates.common.event.bus.EventBus;
import net.momirealms.customnameplates.common.plugin.NameplatesPlugin;

import java.util.OptionalInt;

/**
 * Interface for managing and dispatching events within the Nameplates plugin system.
 * <p>
 * This interface provides methods to subscribe to events, dispatch events, and interact with the underlying
 * {@link EventBus}. It allows plugins to register subscribers for specific events and manage the event lifecycle.
 * </p>
 */
public interface EventManager {

    /**
     * A static inner class that holds the singleton instance of {@link EventManager}.
     * This ensures that the {@link EventManager} is initialized only once.
     */
    class SingletonHolder {
        private static EventManager INSTANCE = null;
    }

    /**
     * Creates and returns the singleton instance of {@link EventManager}.
     * <p>
     * This method ensures that only one instance of the {@link EventManager} is created during the lifecycle
     * of the plugin. If the instance does not exist, it initializes the {@link EventManager} using the provided
     * {@link NameplatesPlugin}.
     * </p>
     *
     * @param plugin the {@link NameplatesPlugin} instance to be used for initializing the event manager
     * @return the singleton instance of {@link EventManager}
     */
    static EventManager create(NameplatesPlugin plugin) {
        if (SingletonHolder.INSTANCE == null) {
            SingletonHolder.INSTANCE = new EventManagerImpl(plugin);
        }
        return SingletonHolder.INSTANCE;
    }

    /**
     * Subscribes a {@link EventSubscriber} to an event type.
     * <p>
     * This method allows subscribing a listener (subscriber) to a specific event type. The subscriber will be notified
     * whenever the event is dispatched.
     * </p>
     *
     * @param <T> the type of event to subscribe to
     * @param event the {@link Class} object representing the event type
     * @param subscriber the {@link EventSubscriber} that will handle the event
     * @return an {@link EventSubscription} representing the subscription
     */
    <T extends NameplatesEvent> EventSubscription<T> subscribe(Class<T> event, EventSubscriber<? super T> subscriber);

    /**
     * Subscribes a {@link EventSubscriber} to an event type with a custom event configuration.
     * <p>
     * This method allows subscribing a listener (subscriber) to a specific event type with additional configuration
     * settings for the event. The subscriber will be notified based on the provided configuration when the event is
     * dispatched.
     * </p>
     *
     * @param <T> the type of event to subscribe to
     * @param event the {@link Class} object representing the event type
     * @param config the {@link EventConfig} containing additional event configuration
     * @param subscriber the {@link EventSubscriber} that will handle the event
     * @return an {@link EventSubscription} representing the subscription
     */
    <T extends NameplatesEvent> EventSubscription<T> subscribe(Class<T> event, EventConfig config, EventSubscriber<? super T> subscriber);

    /**
     * Dispatches an event of the specified class with the provided parameters.
     * <p>
     * This method triggers the event, notifying all subscribers of the event. The event is dispatched synchronously
     * to all registered subscribers.
     * </p>
     *
     * @param eventClass the {@link Class} object representing the event type
     * @param params the parameters to pass to the event
     * @return the dispatched {@link NameplatesEvent}
     */
    NameplatesEvent dispatch(Class<? extends NameplatesEvent> eventClass, Object... params);

    /**
     * Dispatches an event of the specified class with the provided order and parameters.
     * <p>
     * This method allows dispatching the event with an optional order. The order is used to prioritize events,
     * allowing certain events to be processed before others. If the order is not provided, the event will be dispatched
     * without any specific order.
     * </p>
     *
     * @param eventClass the {@link Class} object representing the event type
     * @param order the optional {@link OptionalInt} specifying the order of the event
     * @param params the parameters to pass to the event
     * @return the dispatched {@link NameplatesEvent}
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    NameplatesEvent dispatch(Class<? extends NameplatesEvent> eventClass, OptionalInt order, Object... params);

    /**
     * Retrieves the underlying event bus that handles event dispatching and subscriptions.
     * <p>
     * The {@link EventBus} is responsible for managing the lifecycle of events, including dispatching and subscribing
     * to events. This method allows direct access to the {@link EventBus}.
     * </p>
     *
     * @return the underlying {@link EventBus} used by the event manager
     */
    EventBus<?> getEventBus();
}
