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

public class MainTask implements Runnable {

    private static int RUN_TICKS = 0;

    private static final Map<Integer, Integer> TIME_1 = new ConcurrentHashMap<>(2048);
    private static final Map<Integer, Integer> TIME_2 = new ConcurrentHashMap<>(2048);
    private static final Set<Integer> requestedSharedPlaceholders = Collections.synchronizedSet(new ObjectOpenHashSet<>());
    private int timer;

    private final CustomNameplates plugin;
    private boolean state;

    public MainTask(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    public static int getTicks() {
        return RUN_TICKS;
    }

    public static boolean hasRequested(int countId) {
        return !requestedSharedPlaceholders.add(countId);
    }

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

    public static class HealthyProfile {

        private final double load;
        private final long totalTimeNS;

        private final int actionBarConditionNS;
        private final int refreshPlaceholderNS;

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

        public int getActionBarConditionNS() {
            return actionBarConditionNS;
        }

        public int getRefreshPlaceholderNS() {
            return refreshPlaceholderNS;
        }

        public double getLoad() {
            return load;
        }

        public long getTotalTimeNS() {
            return totalTimeNS;
        }
    }
}
