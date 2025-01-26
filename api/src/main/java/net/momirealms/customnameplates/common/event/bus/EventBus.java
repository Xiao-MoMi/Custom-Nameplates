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

package net.momirealms.customnameplates.common.event.bus;

import net.momirealms.customnameplates.common.event.EventSubscription;

import java.util.OptionalInt;

public interface EventBus<E> {

    default void post(final E event) {
        this.post(event, OptionalInt.empty());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    void post(final E event, final OptionalInt order);

    @FunctionalInterface
    interface EventExceptionHandler {
        /**
         * Handles a caught exception.
         */
        <E> void eventExceptionCaught(final EventBus<? super E> bus, final EventSubscription<? super E> subscription, final E event, final Throwable throwable);
    }
}
