package net.momirealms.customnameplates.api.requirement;

public abstract class AbstractRequirement implements Requirement {

    protected final int refreshInterval;

    public AbstractRequirement(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    @Override
    public int refreshInterval() {
        return refreshInterval;
    }
}
