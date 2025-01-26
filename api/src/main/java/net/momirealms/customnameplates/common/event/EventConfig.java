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

package net.momirealms.customnameplates.common.event;

public interface EventConfig {

    /**
     * The default value for {@link #order()}.
     *
     */
    int DEFAULT_ORDER = 0;

    /**
     * The default value for {@link #acceptsCancelled()}.
     *
     */
    boolean DEFAULT_ACCEPTS_CANCELLED = true;

    /**
     * The default value for {@link #exact()}.
     *
     */
    boolean DEFAULT_EXACT = false;

    /**
     * Gets the post order.
     *
     * @return the post order
     */
    int order();

    /**
     * Sets the post order.
     *
     * @param order the post order
     * @return an {@link EventConfig}
     */
    EventConfig order(final int order);

    /**
     * Gets if cancelled events are accepted.
     *
     * @return if cancelled events are accepted
     */
    boolean acceptsCancelled();

    /**
     * Sets if cancelled events are accepted.
     *
     * @param acceptsCancelled if cancelled events are accepted
     * @return an {@link EventConfig}
     */
    EventConfig acceptsCancelled(final boolean acceptsCancelled);

    /**
     * Gets if only the exact event type is accepted.
     *
     * @return if only the exact event type is accepted
     */
    boolean exact();

    /**
     * Sets if only the exact event type is accepted.
     *
     * @param exact if only the exact event type is accepted
     * @return an {@link EventConfig}
     */
    EventConfig exact(final boolean exact);

    static EventConfig defaults() {
        return EventConfigImpl.DEFAULTS;
    }

    static EventConfig of(
            final int order,
            final boolean acceptsCancelled,
            final boolean exact
    ) {
        return new EventConfigImpl(order, acceptsCancelled, exact);
    }

    static Builder builder() {
        return new EventConfigImpl.BuilderImpl();
    }

    /**
     * Builder.
     */
    interface Builder {
        /**
         * Sets the post order.
         *
         * @param order the post order
         * @return {@code this}
         */
        Builder order(final int order);

        /**
         * Sets if cancelled events are accepted.
         *
         * @param acceptsCancelled if cancelled events are accepted
         * @return {@code this}
         */
        Builder acceptsCancelled(final boolean acceptsCancelled);

        /**
         * Sets if only the exact event type is accepted.
         *
         * @param exact if only the exact event type is accepted
         * @return {@code this}
         */
        Builder exact(final boolean exact);

        /**
         * Builds.
         *
         * @return an {@link EventConfig}
         */
        EventConfig build();
    }
}
