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

package net.momirealms.customnameplates.common.event.gen;

import net.momirealms.customnameplates.common.event.NameplatesEvent;
import net.momirealms.customnameplates.common.plugin.NameplatesPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandles;

/**
 * Abstract base class for events in the Nameplates plugin system.
 */
public abstract class AbstractNameplatesEvent implements NameplatesEvent {

    private final NameplatesPlugin plugin;

    /**
     * Constructs an instance of {@code AbstractNameplatesEvent} with the given {@link NameplatesPlugin}.
     *
     * @param plugin the {@link NameplatesPlugin} instance associated with this event
     */
    protected AbstractNameplatesEvent(NameplatesPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Returns the {@link NameplatesPlugin} instance associated with this event.
     *
     * @return the plugin instance
     */
    @NotNull
    @Override
    public NameplatesPlugin plugin() {
        return plugin;
    }

    /**
     * Returns a {@link MethodHandles.Lookup} instance for dynamic method invocation.
     * <p>
     * This method is currently unsupported and will throw an {@link UnsupportedOperationException} if invoked.
     * It may be overridden in subclasses to provide custom logic for dynamic method handles.
     * </p>
     *
     * @return the {@link MethodHandles.Lookup} instance
     * @throws UnsupportedOperationException if the method is not overridden in a subclass
     */
    public MethodHandles.Lookup mhl() {
        throw new UnsupportedOperationException();
    }
}
