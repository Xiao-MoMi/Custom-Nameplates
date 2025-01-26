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

/**
 * Interface representing an event or task that can be cancelled.
 * This interface defines methods to check and set the cancellation state of the event or task.
 */
public interface Cancellable {

    /**
     * Gets the current cancelled state of the event or task.
     *
     * @return true if the event or task has been cancelled, false otherwise.
     */
    boolean cancelled();

    /**
     * Sets the cancelled state of the event or task.
     *
     * @param cancelled a boolean indicating whether the event is cancelled
     */
    void cancelled(final boolean cancelled);
}
