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

record EventConfigImpl(
        int order,
        boolean acceptsCancelled,
        boolean exact
) implements EventConfig {
    static final EventConfigImpl DEFAULTS = new EventConfigImpl(DEFAULT_ORDER, DEFAULT_ACCEPTS_CANCELLED, DEFAULT_EXACT);

    static EventConfigImpl create(
            final int order,
            final boolean acceptsCancelled,
            final boolean exact
    ) {
        if (order == DEFAULT_ORDER && acceptsCancelled == DEFAULT_ACCEPTS_CANCELLED && exact == DEFAULT_EXACT) {
            return DEFAULTS;
        }
        return new EventConfigImpl(order, acceptsCancelled, exact);
    }

    @Override
    public EventConfig order(final int order) {
        return create(order, this.acceptsCancelled, this.exact);
    }

    @Override
    public EventConfig acceptsCancelled(final boolean acceptsCancelled) {
        return create(this.order, acceptsCancelled, this.exact);
    }

    @Override
    public EventConfig exact(final boolean exact) {
        return create(this.order, this.acceptsCancelled, exact);
    }

    static final class BuilderImpl implements Builder {
        private int order = DEFAULT_ORDER;
        private boolean acceptsCancelled = DEFAULT_ACCEPTS_CANCELLED;
        private boolean exact = DEFAULT_EXACT;

        @Override
        public Builder order(final int order) {
            this.order = order;
            return this;
        }

        @Override
        public Builder acceptsCancelled(final boolean acceptsCancelled) {
            this.acceptsCancelled = acceptsCancelled;
            return this;
        }

        @Override
        public Builder exact(final boolean exact) {
            this.exact = exact;
            return this;
        }

        @Override
        public EventConfig build() {
            return create(this.order, this.acceptsCancelled, this.exact);
        }
    }
}
