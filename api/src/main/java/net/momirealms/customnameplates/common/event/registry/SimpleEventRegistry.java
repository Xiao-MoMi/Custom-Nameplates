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
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

public class SimpleEventRegistry<E> implements EventRegistry<E> {
    private static final Comparator<EventSubscription<?>> ORDER_COMPARATOR = Comparator.comparingInt(subscription -> subscription.config().order());

    private final Map<Class<? extends E>, Collection<? extends Class<?>>> classes = new HashMap<>();

    private final Map<Class<? extends E>, List<EventSubscription<? super E>>> unbaked = new HashMap<>();
    private final Map<Class<? extends E>, List<EventSubscription<? super E>>> baked = new HashMap<>();

    private final Object lock = new Object();

    private final Class<E> type;

    /**
     * Constructs a new {@code SimpleEventRegistry}.
     *
     * @param type the base event type
     */
    public SimpleEventRegistry(final Class<E> type) {
        this.type = requireNonNull(type, "type");
    }

    @Override
    public Class<E> type() {
        return this.type;
    }

    @Override
    public <T extends E> EventSubscription<T> subscribe(final Class<T> event, final EventConfig config, final EventSubscriber<? super T> subscriber) {
        requireNonNull(event, "event");
        requireNonNull(config, "config");
        requireNonNull(subscriber, "subscriber");
        final EventSubscription<T> subscription = new EventSubscriptionImpl<>(event, config, subscriber);
        synchronized (this.lock) {
            final List<EventSubscription<? super T>> subscriptions = yayGenerics(this.unbaked.computeIfAbsent(event, key -> new ArrayList<>()));
            subscriptions.add(subscription);
            this.baked.clear();
        }
        return subscription;
    }

    @Override
    public void unsubscribeIf(final Predicate<EventSubscription<? super E>> predicate) {
        synchronized (this.lock) {
            boolean removedAny = false;
            for (final List<EventSubscription<? super E>> subscriptions : this.unbaked.values()) {
                removedAny |= subscriptions.removeIf(predicate);
            }
            if (removedAny) {
                this.baked.clear();
            }
        }
    }

    @Override
    public List<EventSubscription<? super E>> subscriptions(final Class<? extends E> event) {
        synchronized (this.lock) {
            return this.baked.computeIfAbsent(event, this::computeSubscriptions);
        }
    }

    private List<EventSubscription<? super E>> computeSubscriptions(final Class<? extends E> event) {
        final List<EventSubscription<? super E>> subscriptions = new ArrayList<>();
        final Collection<? extends Class<?>> types = this.classes.computeIfAbsent(event, this::findClasses);
        for (final Class<?> type : types) {
            subscriptions.addAll(this.unbaked.getOrDefault(type, Collections.emptyList()));
        }
        subscriptions.sort(ORDER_COMPARATOR);
        return subscriptions;
    }

    private Collection<? extends Class<?>> findClasses(final Class<?> type) {
        final Collection<? extends Class<?>> classes = Internals.ancestors(type);
        classes.removeIf(klass -> !this.type.isAssignableFrom(klass));
        return classes;
    }

    @SuppressWarnings("unchecked")
    private static <T extends U, U> List<U> yayGenerics(final List<T> list) {
        return (List<U>) list;
    }

    private class EventSubscriptionImpl<T extends E> implements EventSubscription<T> {
        private final Class<T> event;
        private final EventConfig config;
        private final EventSubscriber<? super T> subscriber;

        EventSubscriptionImpl(final Class<T> event, final EventConfig config, final EventSubscriber<? super T> subscriber) {
            this.event = event;
            this.config = config;
            this.subscriber = subscriber;
        }

        @Override
        public Class<T> event() {
            return this.event;
        }

        @Override
        public EventConfig config() {
            return this.config;
        }

        @Override
        public EventSubscriber<? super T> subscriber() {
            return this.subscriber;
        }

        @Override
        public void dispose() {
            synchronized (SimpleEventRegistry.this.lock) {
                final @Nullable List<EventSubscription<? super T>> subscriptions = yayGenerics(SimpleEventRegistry.this.unbaked.get(this.event));
                if (subscriptions != null) {
                    subscriptions.remove(this);
                    SimpleEventRegistry.this.baked.clear();
                }
            }
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                    .add("event=" + this.event)
                    .add("config=" + this.config)
                    .add("subscriber=" + this.subscriber)
                    .toString();
        }
    }
}