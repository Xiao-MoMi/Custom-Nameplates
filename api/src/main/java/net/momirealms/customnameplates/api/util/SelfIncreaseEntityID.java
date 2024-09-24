package net.momirealms.customnameplates.api.util;

import java.util.concurrent.ThreadLocalRandom;

public class SelfIncreaseEntityID {

    private static int id = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE / 4, Integer.MAX_VALUE / 3);

    public static int getAndIncrease() {
        int i = id;
        id++;
        return i;
    }
}
