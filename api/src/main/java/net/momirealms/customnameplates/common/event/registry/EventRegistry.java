/*
 * This file is part of event, licensed under the MIT License.
 *
 * Copyright (c) 2021-2023 Seiama
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.momirealms.customnameplates.common.event.registry;

import net.momirealms.customnameplates.common.event.EventConfig;
import net.momirealms.customnameplates.common.event.EventSubscriber;
import net.momirealms.customnameplates.common.event.EventSubscription;

import java.util.List;
import java.util.function.Predicate;

public interface EventRegistry<E> {

    /**
     * Gets the base event type.
     *
     * <p>This is represented by the {@code E} type parameter.</p>
     */
    Class<E> type();

    /**
     * Determines whether the specified event has subscribers.
     *
     * @param event the event type
     * @return whether the specified event has subscribers
     */
    default boolean subscribed(final Class<? extends E> event) {
        return !this.subscriptions(event).isEmpty();
    }

    /**
     * Registers the given {@code subscriber} to receive events, using the default {@link EventConfig configuration}.
     *
     * @param event the event type
     * @param subscriber the subscriber
     * @param <T> the event type
     */
    default <T extends E> EventSubscription<T> subscribe(final Class<T> event, final EventSubscriber<? super T> subscriber) {
        return this.subscribe(event, EventConfig.defaults(), subscriber);
    }

    /**
     * Registers the given {@code subscriber} to receive events.
     *
     * @param event the event type
     * @param config the event configuration
     * @param subscriber the subscriber
     * @param <T> the event type
     */
    <T extends E> EventSubscription<T> subscribe(final Class<T> event, final EventConfig config, final EventSubscriber<? super T> subscriber);

    /**
     * Removes subscriptions matching {@code predicate}.
     *
     * @param predicate the predicate used to determine which subscriptions to remove
     */
    void unsubscribeIf(final Predicate<EventSubscription<? super E>> predicate);

    /**
     * Gets an unmodifiable list containing all subscriptions currently registered for events of type {@code event}.
     *
     * @return a list of all subscriptions for events of type {@code event}
     */
    List<EventSubscription<? super E>> subscriptions(final Class<? extends E> event);
}