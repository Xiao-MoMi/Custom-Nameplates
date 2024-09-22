package net.momirealms.customnameplates.api.placeholder;

import java.util.Objects;

public class DummyPlaceholder implements Placeholder {

    private final String id;

    public DummyPlaceholder(String id) {
        this.id = id;
    }

    @Override
    public int refreshInterval() {
        return -1;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DummyPlaceholder that = (DummyPlaceholder) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
