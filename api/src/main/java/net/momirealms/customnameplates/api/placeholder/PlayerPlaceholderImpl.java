package net.momirealms.customnameplates.api.placeholder;

import net.momirealms.customnameplates.api.CNPlayer;

import java.util.function.Function;

public class PlayerPlaceholderImpl extends AbstractPlaceholder implements PlayerPlaceholder {

    private final Function<CNPlayer<?>, String> function;

    protected PlayerPlaceholderImpl(PlaceholderManager manager, String id, int refreshInterval, Function<CNPlayer<?>, String> function) {
        super(manager, id, refreshInterval);
        this.function = function;
    }

    @Override
    public String request(CNPlayer<?> player) {
        return function.apply(player);
    }
}
