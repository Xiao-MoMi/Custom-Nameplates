package net.momirealms.customnameplates.api.placeholder;

import java.util.Objects;

public abstract class AbstractPlaceholder implements Placeholder {

    protected String id;
    protected int refreshInterval;
    protected PlaceholderManager manager;

    protected AbstractPlaceholder(PlaceholderManager manager, String id, int refreshInterval) {
        if (refreshInterval == 0) {
            refreshInterval = -1;
        }
        if (!(id.startsWith("%") && id.endsWith("%"))) {
            throw new IllegalArgumentException("placeholder must start and end with '%'");
        }
        this.manager = manager;
        this.id = id;
        this.refreshInterval = refreshInterval;
    }

    @Override
    public int refreshInterval() {
        return refreshInterval;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractPlaceholder that = (AbstractPlaceholder) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
