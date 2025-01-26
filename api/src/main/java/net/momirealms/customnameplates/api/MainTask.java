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

package net.momirealms.customnameplates.api;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MainTask is a periodic task that handles various operations related to the plugin, such as refreshing conditions,
 * managing placeholders, and monitoring performance. It runs periodically and executes different plugin managers
 * in each cycle to maintain the smooth operation of the system.
 */
public class MainTask implements Runnable {
    private static int RUN_TICKS = 0;
    private static final Map<Integer, Integer> TIME_1 = new ConcurrentHashMap<>(2048);
    private static final Map<Integer, Integer> TIME_2 = new ConcurrentHashMap<>(2048);
    private static final Set<Integer> requestedSharedPlaceholders = Collections.synchronizedSet(new ObjectOpenHashSet<>());
    private int timer;
    private final CustomNameplates plugin;
    private boolean state;

    /**
     * Constructs a new MainTask instance.
     *
     * @param plugin the plugin instance associated with this task
     */
    public MainTask(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    /**
     * Returns the total number of ticks the task has executed.
     *
     * @return the number of ticks
     */
    public static int getTicks() {
        return RUN_TICKS;
    }

    /**
     * Checks if a shared placeholder request for the specified count ID has already been made.
     *
     * @param countId the ID to check
     * @return true if the request has been made, false otherwise
     */
    public static boolean hasRequested(int countId) {
        return !requestedSharedPlaceholders.add(countId);
    }

    /**
     * Resets the tick counter to 0.
     */
    public static void reset() {
        RUN_TICKS = 0;
    }

    @Override
    public void run() {
        // we should skip the task if the server is heavily loaded
        if (this.state) return;
        this.state = true;
        try {
            RUN_TICKS++;
            requestedSharedPlaceholders.clear();
            long time1 = System.nanoTime();
            plugin.actionBarManager.refreshConditions();
            plugin.bossBarManager.onTick();
            plugin.unlimitedTagManager.onTick();
            long time2 = System.nanoTime();
            plugin.placeholderManager.refreshPlaceholders();
            long time3 = System.nanoTime();
            plugin.actionBarManager.checkHeartBeats();
            int diff1 = (int) (time2 - time1);
            TIME_1.put(timer, diff1);
            int diff2 = (int) (time3 - time2);
            TIME_2.put(timer, diff2);
            timer++;
            if (timer >= 1200) timer = 0;
            if (RUN_TICKS < 0) {
                CustomNameplates.getInstance().reload();
            }
        } finally {
            this.state = false;
        }
    }

    /**
     * Retrieves the performance profile of the task based on the execution time and load.
     *
     * @return a HealthyProfile object containing the performance statistics
     */
    public static HealthyProfile getHealthyProfile() {
        long total1 = TIME_1.values().stream().mapToLong(Integer::longValue).sum();
        long total2 = TIME_2.values().stream().mapToLong(Integer::longValue).sum();

        long total = total1 + total2;
        double size = TIME_1.size();

        double load = total / size / 50_000_000;

        return new HealthyProfile(
                load,
                (long) (total / size),
                (int) (total1 / size),
                (int) (total2 / size)
        );
    }

    /**
     * Represents the health profile of the task, including performance metrics such as load and execution times.
     */
    public static class HealthyProfile {
        private final double load;
        private final long totalTimeNS;
        private final int actionBarConditionNS;
        private final int refreshPlaceholderNS;

        /**
         * Constructs a new HealthyProfile instance with the provided metrics.
         *
         * @param load the load factor based on execution time
         * @param totalTimeNS the total time spent on the task, in nanoseconds
         * @param actionBarConditionNS the average time spent on action bar conditions, in nanoseconds
         * @param refreshPlaceholderNS the average time spent refreshing placeholders, in nanoseconds
         */
        public HealthyProfile(
                double load,
                long totalTimeNS,
                int actionBarConditionNS,
                int refreshPlaceholderNS
        ) {
            this.actionBarConditionNS = actionBarConditionNS;
            this.refreshPlaceholderNS = refreshPlaceholderNS;
            this.load = load;
            this.totalTimeNS = totalTimeNS;
        }

        /**
         * Gets the average time spent on action bar condition checks, in nanoseconds.
         *
         * @return the action bar condition time in nanoseconds
         */
        public int getActionBarConditionNS() {
            return actionBarConditionNS;
        }

        /**
         * Gets the average time spent refreshing placeholders, in nanoseconds.
         *
         * @return the placeholder refresh time in nanoseconds
         */
        public int getRefreshPlaceholderNS() {
            return refreshPlaceholderNS;
        }

        /**
         * Gets the load factor based on the task's execution time.
         *
         * @return the load factor
         */
        public double getLoad() {
            return load;
        }

        /**
         * Gets the total time spent on the task, in nanoseconds.
         *
         * @return the total execution time in nanoseconds
         */
        public long getTotalTimeNS() {
            return totalTimeNS;
        }
    }
}
