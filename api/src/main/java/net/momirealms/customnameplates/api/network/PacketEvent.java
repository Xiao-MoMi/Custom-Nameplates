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

package net.momirealms.customnameplates.api.network;

import net.momirealms.customnameplates.common.event.Cancellable;

/**
 * Represents a packet event, which can be cancelled and supports delayed tasks that are executed later.
 */
public class PacketEvent implements Cancellable {

    private boolean cancelled;
    private final Object packet;

    /**
     * Constructs a new PacketEvent with the specified packet.
     *
     * @param packet the packet associated with this event
     */
    public PacketEvent(Object packet) {
        this.packet = packet;
    }

    /**
     * Returns the packet associated with this event.
     *
     * @return the packet object
     */
    public Object getPacket() {
        return packet;
    }

    /**
     * Checks if the event has been cancelled.
     *
     * @return true if the event is cancelled, false otherwise
     */
    @Override
    public boolean cancelled() {
        return cancelled;
    }

    /**
     * Sets the cancelled state of the event.
     *
     * @param cancelled true to cancel the event, false to proceed
     */
    @Override
    public void cancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}