package net.momirealms.customnameplates.api.requirement;

import net.momirealms.customnameplates.api.CNPlayer;

public class EmptyRequirement implements Requirement {

    public static final EmptyRequirement INSTANCE = new EmptyRequirement();

    public static Requirement instance() {
        return INSTANCE;
    }

    @Override
    public boolean isSatisfied(CNPlayer p1, CNPlayer p2) {
        return true;
    }

    @Override
    public String type() {
        return "empty";
    }

    @Override
    public int refreshInterval() {
        return -1;
    }

    @Override
    public int hashCode() {
        return type().hashCode();
    }
}
