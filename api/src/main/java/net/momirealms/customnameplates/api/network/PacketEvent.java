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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Represents a packet event, which can be cancelled and supports delayed tasks that are executed later.
 */
public class PacketEvent implements Cancellable {
    private final Object packet;
    private boolean cancelled;
    private List<Runnable> afterSendTasks;

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
     * Registers a task that must run only after this packet has been forwarded
     * to the next outbound handler.
     *
     * @param task task to execute after the packet is sent
     */
    public void afterSend(Runnable task) {
        this.afterSendTasks.add(Objects.requireNonNull(task, "task"));
    }

    public List<Runnable> afterSendTasks() {
        if (this.afterSendTasks == null) {
            this.afterSendTasks = new ArrayList<>(1);
        }
        return this.afterSendTasks;
    }

    /**
     * Executes and clears all registered after-send tasks. A failure in one
     * task is reported without preventing the remaining tasks from running.
     *
     * @param exceptionHandler handler for failures raised by after-send tasks
     */
    public void runAfterSendTasks(Consumer<Throwable> exceptionHandler) {
        Objects.requireNonNull(exceptionHandler, "exceptionHandler");
        List<Runnable> tasks = List.copyOf(afterSendTasks);
        afterSendTasks.clear();
        for (Runnable task : tasks) {
            try {
                task.run();
            } catch (Throwable throwable) {
                exceptionHandler.accept(throwable);
            }
        }
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
