package net.momirealms.customnameplates.api.placeholder;

import net.momirealms.customnameplates.api.CNPlayer;

import java.util.function.BiFunction;

public class RelationalPlaceholderImpl extends AbstractPlaceholder implements RelationalPlaceholder {

    private final BiFunction<CNPlayer<?>, CNPlayer<?>, String> function;

    protected RelationalPlaceholderImpl(PlaceholderManager manager, String id, int refreshInterval, BiFunction<CNPlayer<?>, CNPlayer<?>, String> function) {
        super(manager, id, refreshInterval);
        this.function = function;
    }

    @Override
    public String request(CNPlayer<?> p1, CNPlayer<?> p2) {
        return function.apply(p1, p2);
    }
}
