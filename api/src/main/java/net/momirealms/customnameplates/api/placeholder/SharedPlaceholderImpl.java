package net.momirealms.customnameplates.api.placeholder;

import java.util.function.Supplier;

public class SharedPlaceholderImpl extends AbstractPlaceholder implements SharedPlaceholder {

    private final Supplier<String> supplier;
    private String latestValue;

    protected SharedPlaceholderImpl(PlaceholderManager manager, String id, int refreshInterval, Supplier<String> supplier) {
        super(manager, id, refreshInterval);
        this.supplier = supplier;
    }

    @Override
    public String request() {
        return supplier.get();
    }

    @Override
    public void update() {
        latestValue = request();
    }

    @Override
    public String getLatestValue() {
        return latestValue;
    }
}
