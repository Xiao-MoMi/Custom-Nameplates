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

package net.momirealms.customnameplates.common.plugin.scheduler;

import java.util.concurrent.ScheduledFuture;

/**
 * Represents an asynchronous task that is scheduled to run at a later time.
 * This class wraps a {@link ScheduledFuture} and provides methods to cancel
 * the task and check if it has been cancelled.
 */
public class AsyncTask implements SchedulerTask {

    /**
     * The {@link ScheduledFuture} representing the scheduled task.
     * This is used to interact with the task, such as cancelling it.
     */
    private final ScheduledFuture<?> future;

    /**
     * Constructs an {@link AsyncTask} using the provided {@link ScheduledFuture}.
     *
     * @param future the {@link ScheduledFuture} representing the scheduled task.
     */
    public AsyncTask(ScheduledFuture<?> future) {
        this.future = future;
    }

    /**
     * Cancels the scheduled task. The task will not run if it has not started yet,
     * or it will stop if it is currently running.
     *
     * @see ScheduledFuture#cancel(boolean)
     */
    @Override
    public void cancel() {
        future.cancel(false);
    }

    /**
     * Checks if the task has been cancelled.
     *
     * @return true if the task has been cancelled, false otherwise.
     * @see ScheduledFuture#isCancelled()
     */
    @Override
    public boolean cancelled() {
        return future.isCancelled();
    }
}
