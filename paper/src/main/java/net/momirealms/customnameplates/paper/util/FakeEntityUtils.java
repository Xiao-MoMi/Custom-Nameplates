package net.momirealms.customnameplates.paper.util;

public class FakeEntityUtils {

    private static int entityID = 114514520;

    public static int getAndIncrease() {
        return entityID++;
    }
}
