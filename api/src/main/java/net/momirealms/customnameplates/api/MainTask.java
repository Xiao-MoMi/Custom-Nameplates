package net.momirealms.customnameplates.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainTask implements Runnable {

    private static final Map<Integer, Integer> TIME_1 = new ConcurrentHashMap<>(1600, 0.8F);
    private static final Map<Integer, Integer> TIME_2 = new ConcurrentHashMap<>(1600, 0.8F);
    private int timer;

    private final CustomNameplates plugin;

    public MainTask(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        long time1 = System.nanoTime();
        plugin.actionBarManager.refreshConditions();
        plugin.bossBarManager.onTick();
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
    }

    public static HealthyProfile getHealthyProfile() {
        long total1 = 0;
        long total2 = 0;
        for (int value : TIME_1.values()) {
            total1 += value;
        }
        for (int value : TIME_2.values()) {
            total2 += value;
        }
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
